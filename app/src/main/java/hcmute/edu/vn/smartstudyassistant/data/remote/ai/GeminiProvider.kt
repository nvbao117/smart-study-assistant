package hcmute.edu.vn.smartstudyassistant.data.remote.ai

import com.google.gson.Gson
import com.google.gson.JsonObject
import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.ChatMessageEntity
import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.MessageRole
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class GeminiProvider : AiChatProvider {

    private val client = OkHttpClient()
    private val gson = Gson()

    override suspend fun sendMessage(
        messages: List<ChatMessageEntity>,
        apiKey: String,
        temperature: Double,
        maxTokens: Int
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val body = buildRequestBody(messages, temperature, maxTokens)
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=$apiKey"
            val request = Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/json")
                .post(body.toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                return@withContext Result.failure(IOException("Gemini error ${response.code}: ${response.body?.string()}"))
            }

            val json = response.body?.string()
                ?: return@withContext Result.failure(IOException("Empty response"))
            val content = gson.fromJson(json, JsonObject::class.java)
                .getAsJsonArray("candidates")?.get(0)?.asJsonObject
                ?.getAsJsonObject("content")
                ?.getAsJsonArray("parts")?.get(0)?.asJsonObject
                ?.get("text")?.asString
                ?: return@withContext Result.failure(IOException("Failed to parse Gemini response"))

            Result.success(content)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun buildRequestBody(messages: List<ChatMessageEntity>, temperature: Double, maxTokens: Int): String {
        val contents = messages
            .filter { it.role != MessageRole.SYSTEM }
            .map { mapOf("role" to if (it.role == MessageRole.USER) "user" else "model",
                         "parts" to listOf(mapOf("text" to it.content))) }
        return gson.toJson(mapOf(
            "contents" to contents,
            "generationConfig" to mapOf("temperature" to temperature, "maxOutputTokens" to maxTokens)
        ))
    }
}
