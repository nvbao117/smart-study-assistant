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

class AnthropicProvider : AiChatProvider {

    private val client = OkHttpClient()
    private val gson = Gson()

    override suspend fun sendMessage(
        messages: List<ChatMessageEntity>,
        apiKey: String,
        temperature: Double,
        maxTokens: Int
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val systemMsg = messages.firstOrNull { it.role == MessageRole.SYSTEM }?.content
            val chatMessages = messages.filter { it.role != MessageRole.SYSTEM }
            val body = buildRequestBody(systemMsg, chatMessages, temperature, maxTokens)

            val request = Request.Builder()
                .url("https://api.anthropic.com/v1/messages")
                .addHeader("x-api-key", apiKey)
                .addHeader("anthropic-version", "2023-06-01")
                .addHeader("Content-Type", "application/json")
                .post(body.toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                return@withContext Result.failure(IOException("Anthropic error ${response.code}: ${response.body?.string()}"))
            }

            val json = response.body?.string()
                ?: return@withContext Result.failure(IOException("Empty response"))
            val content = gson.fromJson(json, JsonObject::class.java)
                .getAsJsonArray("content")?.get(0)?.asJsonObject
                ?.get("text")?.asString
                ?: return@withContext Result.failure(IOException("Failed to parse Anthropic response"))

            Result.success(content)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun buildRequestBody(
        systemMsg: String?,
        messages: List<ChatMessageEntity>,
        temperature: Double,
        maxTokens: Int
    ): String {
        val params = mutableMapOf<String, Any>(
            "model" to "claude-3-haiku-20240307",
            "max_tokens" to maxTokens,
            "temperature" to temperature,
            "messages" to messages.map { mapOf("role" to it.role.name.lowercase(), "content" to it.content) }
        )
        systemMsg?.let { params["system"] = it }
        return gson.toJson(params)
    }
}
