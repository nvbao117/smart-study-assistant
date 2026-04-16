package hcmute.edu.vn.smartstudyassistant.data.remote.ai

import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.ChatMessageEntity

interface AiChatProvider {
    suspend fun sendMessage(
        messages: List<ChatMessageEntity>,
        apiKey: String,
        temperature: Double,
        maxTokens: Int
    ): Result<String>
}
