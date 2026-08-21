# Menuly — AI Waiter

Android app + Cloudflare Worker. Scan a restaurant menu, pick a mood (or **Surprise me**), get one decisive dish recommendation with waiter-style reasoning.

## Architecture

```
Camera → ML Kit OCR → menu text → Cloudflare Worker → Replicate GPT-4o-mini → JSON pick
                                                              ↓
                                                         Room History
```

## 1. Backend (Cloudflare Worker)

```bash
cd worker
npm install
# Load token from repo .env or set interactively:
npx wrangler secret put REPLICATE_API_TOKEN
npx wrangler deploy
```

Local:

```bash
cd worker
export REPLICATE_API_TOKEN="$(grep REPLICATE_API_TOKEN ../.env | cut -d= -f2-)"
npx wrangler dev
# → http://127.0.0.1:8787
```

### API

`POST /analyze`

```json
{
  "menuText": "Pho Bo — $18\nLemongrass Chicken — $24",
  "mood": "🎲 Surprise me",
  "customNote": "",
  "mode": "surprise",
  "language": "vi"
}
```

Response includes `pick`, `runnerUp`, `skip`, `waiterNote`, `disclaimer`.

## 2. Android app

Open `android/` in Android Studio (Giraffe+ / Ladybug). **Use JDK 17** (not JDK 21+/26 — Gradle Kotlin DSL can fail parsing those versions).

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
```

1. Set API URL in `android/app/build.gradle.kts`:
   - **debug**: `http://10.0.2.2:8787` (emulator → host wrangler)
   - **release**: your `https://menuly-api.<subdomain>.workers.dev`
2. Emulator + local API: `adb reverse tcp:8787 tcp:8787` (optional if using 10.0.2.2)
3. Run on device/emulator (minSdk 24).

Debug APK (after build): `android/app/build/outputs/apk/debug/app-debug.apk`

### Screens

| Screen | Job |
|--------|-----|
| Home | Mood chips + note + **Scan Menu** / Surprise me |
| Scan | Camera + fake scan line + ML Kit OCR (multi-page) |
| Analyzing | “Chờ xíu…” while Worker calls Replicate |
| Result | Winner / runner-up / skip + disclaimer |
| History | Past picks (Room) |

## Differentiator

Not a calorie counter — an **AI waiter**: mood-first, default **Surprise me**, one pick + why (taste, value, protein, signature vibe).

Allergen/nutrition fields are estimates only; the UI always shows a disclaimer.

## Play Store pack

Ready-to-upload assets + listing copy live in [`play-store/`](play-store/UPLOAD_CHECKLIST.md):

- Icons, feature graphic, screenshots
- EN/VI store listing text
- Privacy policy (`docs/privacy-policy.html` for GitHub Pages)
- Console answers + release signing notes (`android/KEYSTORE.md`)

