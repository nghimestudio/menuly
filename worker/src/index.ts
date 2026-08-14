/**
 * Menuly API — Cloudflare Worker
 * Proxies menu text + mood to Replicate openai/gpt-4o-mini and returns structured picks.
 */

export interface Env {
  REPLICATE_API_TOKEN: string;
  APP_SECRET?: string;
}

interface AnalyzeRequest {
  menuText: string;
  mood?: string;
  customNote?: string;
  mode?: "surprise" | "recommend";
  language?: string;
}

const SYSTEM_PROMPT = `You are Menuly, a charismatic AI waiter at a restaurant.
Your job: help the guest decide what to order from a scanned menu.
Speak warm, direct, and helpful — like a great waiter who knows the menu cold.

RULES:
1. Only recommend dishes that appear in the menu text. Never invent dishes or prices.
2. Default mode is "surprise" (Pick for Me): choose ONE best dish and explain WHY (taste, value, nutrition/satiety, signature vibe, portion, mood fit).
3. Also include a runner-up and a skip (what to avoid for this mood), when possible.
4. Allergen / nutrition notes are ESTIMATES only. Always include a short disclaimer that ingredients, sauces, oil, and cross-contact vary by restaurant — never claim medical accuracy.
5. Score fit 0–10 for the guest's mood.
6. ALWAYS clean up the messy OCR into menuSections: group by category when possible, fix obvious OCR typos in dish names, keep original prices when present. Do NOT invent dishes that are not in the OCR. Skip garbage lines (page numbers, wifi passwords, pure noise).
7. Respond with VALID JSON only (no markdown fences). Match this schema exactly:

{
  "menuSections": [
    {
      "title": string,
      "items": [
        { "name": string, "price": string | null }
      ]
    }
  ],
  "pick": {
    "name": string,
    "price": string | null,
    "score": number,
    "tags": string[],
    "taste": string,
    "spiciness": "none" | "low" | "medium" | "high",
    "protein": "low" | "medium" | "high",
    "priceLevel": "$" | "$$" | "$$$",
    "why": string,
    "ingredients": string[],
    "allergens": string[]
  },
  "runnerUp": {
    "name": string,
    "price": string | null,
    "why": string
  } | null,
  "skip": {
    "name": string,
    "why": string
  } | null,
  "waiterNote": string,
  "disclaimer": string
}`;

function corsHeaders(origin: string | null): HeadersInit {
  return {
    "Access-Control-Allow-Origin": origin || "*",
    "Access-Control-Allow-Methods": "GET, POST, OPTIONS",
    "Access-Control-Allow-Headers": "Content-Type, Authorization, X-App-Secret",
    "Access-Control-Max-Age": "86400",
  };
}

function json(data: unknown, status = 200, origin: string | null = null): Response {
  return new Response(JSON.stringify(data), {
    status,
    headers: {
      "Content-Type": "application/json",
      ...corsHeaders(origin),
    },
  });
}

function extractJson(text: string): unknown {
  const trimmed = text.trim();
  try {
    return JSON.parse(trimmed);
  } catch {
    const fence = trimmed.match(/```(?:json)?\s*([\s\S]*?)```/);
    if (fence?.[1]) {
      return JSON.parse(fence[1].trim());
    }
    const start = trimmed.indexOf("{");
    const end = trimmed.lastIndexOf("}");
    if (start >= 0 && end > start) {
      return JSON.parse(trimmed.slice(start, end + 1));
    }
    throw new Error("Model did not return valid JSON");
  }
}

async function callReplicate(
  token: string,
  menuText: string,
  mood: string,
  customNote: string,
  mode: string,
  language: string
): Promise<unknown> {
  const userPrompt = [
    `Mode: ${mode === "recommend" ? "recommend (rank by mood)" : "surprise (Pick for Me — one decisive pick)"}`,
    `Guest mood: ${mood || "Surprise me — choose the most delicious / interesting dish"}`,
    customNote ? `Extra note from guest: ${customNote}` : null,
    `Reply language: ${language || "vi"} (waiterNote, why, AND menuSections.title in this language when natural; dish names keep original menu language; JSON keys stay English)`,
    "",
    "MENU TEXT (raw OCR — clean & structure into menuSections):",
    menuText.slice(0, 12000),
  ]
    .filter(Boolean)
    .join("\n");

  const res = await fetch(
    "https://api.replicate.com/v1/models/openai/gpt-4o-mini/predictions",
    {
      method: "POST",
      headers: {
        Authorization: `Bearer ${token}`,
        "Content-Type": "application/json",
        Prefer: "wait=60",
      },
      body: JSON.stringify({
        input: {
          system_prompt: SYSTEM_PROMPT,
          prompt: userPrompt,
          temperature: 0.7,
          max_completion_tokens: 2048,
        },
      }),
    }
  );

  if (!res.ok) {
    const errText = await res.text();
    throw new Error(`Replicate error ${res.status}: ${errText}`);
  }

  const prediction = (await res.json()) as {
    status?: string;
    output?: string | string[];
    error?: string;
    urls?: { get?: string };
  };

  if (prediction.error) {
    throw new Error(prediction.error);
  }

  let output = prediction.output;
  let status = prediction.status;

  // Poll if Prefer: wait didn't finish
  if ((!output || status === "starting" || status === "processing") && prediction.urls?.get) {
    for (let i = 0; i < 30; i++) {
      await new Promise((r) => setTimeout(r, 1000));
      const poll = await fetch(prediction.urls.get, {
        headers: { Authorization: `Bearer ${token}` },
      });
      const body = (await poll.json()) as typeof prediction;
      status = body.status;
      if (body.error) throw new Error(body.error);
      if (status === "succeeded") {
        output = body.output;
        break;
      }
      if (status === "failed" || status === "canceled") {
        throw new Error(`Prediction ${status}`);
      }
    }
  }

  if (!output) {
    throw new Error("Empty model output");
  }

  const text = Array.isArray(output) ? output.join("") : String(output);
  return extractJson(text);
}

export default {
  async fetch(request: Request, env: Env): Promise<Response> {
    const origin = request.headers.get("Origin");

    if (request.method === "OPTIONS") {
      return new Response(null, { status: 204, headers: corsHeaders(origin) });
    }

    const url = new URL(request.url);

    if (request.method === "GET" && (url.pathname === "/" || url.pathname === "/health")) {
      return json({ ok: true, service: "menuly-api", version: "1.0.0" }, 200, origin);
    }

    if (request.method === "POST" && url.pathname === "/analyze") {
      if (env.APP_SECRET) {
        const secret = request.headers.get("X-App-Secret");
        if (secret !== env.APP_SECRET) {
          return json({ error: "Unauthorized" }, 401, origin);
        }
      }

      if (!env.REPLICATE_API_TOKEN) {
        return json({ error: "Server misconfigured: missing REPLICATE_API_TOKEN" }, 500, origin);
      }

      let body: AnalyzeRequest;
      try {
        body = (await request.json()) as AnalyzeRequest;
      } catch {
        return json({ error: "Invalid JSON body" }, 400, origin);
      }

      const menuText = (body.menuText || "").trim();
      if (menuText.length < 10) {
        return json({ error: "menuText too short — scan the menu again" }, 400, origin);
      }

      try {
        const result = await callReplicate(
          env.REPLICATE_API_TOKEN,
          menuText,
          body.mood || "Surprise me",
          body.customNote || "",
          body.mode || "surprise",
          body.language || "vi"
        );
        return json({ ok: true, result }, 200, origin);
      } catch (e) {
        const message = e instanceof Error ? e.message : "Unknown error";
        return json({ error: message }, 502, origin);
      }
    }

    return json({ error: "Not found" }, 404, origin);
  },
};
