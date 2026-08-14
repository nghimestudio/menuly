package com.menuly.app.data.api

import com.google.gson.Gson
import com.google.gson.JsonParser
import com.menuly.app.BuildConfig
import com.menuly.app.data.model.AnalyzeResponse
import com.menuly.app.data.model.DishPick
import com.menuly.app.data.model.RunnerUp
import com.menuly.app.data.model.SkipDish
import com.menuly.app.data.model.WaiterResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class MenulyApi(
    private val baseUrl: String = BuildConfig.API_BASE_URL.trimEnd('/'),
    private val appSecret: String = BuildConfig.APP_SECRET,
) {
    private val gson = Gson()
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(90, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun analyze(
        menuText: String,
        mood: String,
        customNote: String = "",
        mode: String = "surprise",
        language: String = "vi",
    ): WaiterResult = withContext(Dispatchers.IO) {
        val payload = JSONObject()
            .put("menuText", menuText)
            .put("mood", mood)
            .put("customNote", customNote)
            .put("mode", mode)
            .put("language", language)
            .toString()

        val builder = Request.Builder()
            .url("$baseUrl/analyze")
            .post(payload.toRequestBody("application/json".toMediaType()))
            .header("Content-Type", "application/json")

        if (appSecret.isNotBlank()) {
            builder.header("X-App-Secret", appSecret)
        }

        client.newCall(builder.build()).execute().use { response ->
            val body = response.body?.string().orEmpty()
            if (!response.isSuccessful) {
                val err = runCatching {
                    JsonParser.parseString(body).asJsonObject.get("error")?.asString
                }.getOrNull()
                throw IllegalStateException(err ?: "HTTP ${response.code}")
            }
            val parsed = gson.fromJson(body, AnalyzeResponse::class.java)
            if (parsed.error != null) throw IllegalStateException(parsed.error)
            parsed.result ?: throw IllegalStateException("Empty result")
        }
    }

    fun toJson(result: WaiterResult): String = gson.toJson(result)

    fun fromJson(json: String): WaiterResult =
        gson.fromJson(json, WaiterResult::class.java) ?: WaiterResult()
}
