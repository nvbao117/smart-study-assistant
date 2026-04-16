package hcmute.edu.vn.smartstudyassistant.data.remote.ai

import com.google.gson.Gson
import com.google.gson.JsonObject
import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.ChatMessageEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class OpenAiProvider : AiChatProvider {

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
            val request = Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .addHeader("Authorization", "Bearer $apiKey")
                .addHeader("Content-Type", "application/json")
                .post(body.toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                return@withContext Result.failure(IOException("OpenAI error ${response.code}: ${response.body?.string()}"))
            }

            val json = response.body?.string()
                ?: return@withContext Result.failure(IOException("Empty response"))
            val content = gson.fromJson(json, JsonObject::class.java)
                .getAsJsonArray("choices")?.get(0)?.asJsonObject
                ?.getAsJsonObject("message")?.get("content")?.asString
                ?: return@withContext Result.failure(IOException("Failed to parse OpenAI response"))

            Result.success(content)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun buildRequestBody(messages: List<ChatMessageEntity>, temperature: Double, maxTokens: Int): String =
        gson.toJson(mapOf(
            "model" to "gpt-4o-mini",
            "messages" to messages.map { mapOf("role" to it.role.name.lowercase(), "content" to it.content) },
            "temperature" to temperature,
            "max_tokens" to maxTokens
        ))
}
