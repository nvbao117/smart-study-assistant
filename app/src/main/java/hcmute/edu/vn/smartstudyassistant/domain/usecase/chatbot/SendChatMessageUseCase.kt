package hcmute.edu.vn.smartstudyassistant.domain.usecase.chatbot

import javax.inject.Inject

import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.ChatMessageEntity
import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.MessageRole
import hcmute.edu.vn.smartstudyassistant.data.remote.ai.AiProviderFactory
import hcmute.edu.vn.smartstudyassistant.domain.repository.ChatRepository
import hcmute.edu.vn.smartstudyassistant.util.Constants
import hcmute.edu.vn.smartstudyassistant.util.EncryptionUtil

class SendChatMessageUseCase @Inject constructor(
    private val chatRepository: ChatRepository,
    private val aiProviderFactory: AiProviderFactory
) {
    suspend operator fun invoke(sessionId: Long, userId: Long, userText: String): Result<String> {
        if (userText.isBlank()) return Result.failure(IllegalArgumentException("Message cannot be empty"))

        // 1. Insert user message
        val userMsg = ChatMessageEntity(sessionId = sessionId, role = MessageRole.USER, content = userText)
        chatRepository.insertMessage(userMsg)

        // 2. Load recent context
        val history = chatRepository.getRecentMessages(sessionId, Constants.AI_CONTEXT_MESSAGE_LIMIT)

        // 3. Get AI settings
        val settings = chatRepository.getAiSettings(userId)
            ?: return Result.failure(IllegalStateException("AI settings not configured"))

        // 4. Get & decrypt API key
        val encryptedKey = when (settings.selectedProvider) {
            hcmute.edu.vn.smartstudyassistant.data.local.db.entity.AiProviderType.OPENAI -> settings.openaiApiKeyEncrypted
            hcmute.edu.vn.smartstudyassistant.data.local.db.entity.AiProviderType.GEMINI -> settings.geminiApiKeyEncrypted
            hcmute.edu.vn.smartstudyassistant.data.local.db.entity.AiProviderType.ANTHROPIC -> settings.anthropicApiKeyEncrypted
        } ?: return Result.failure(IllegalStateException("API key not set for ${settings.selectedProvider}"))

        val apiKey = try { EncryptionUtil.decrypt(encryptedKey) } catch (e: Exception) {
            return Result.failure(IllegalStateException("Failed to decrypt API key"))
        }

        // 5. Call AI provider
        val provider = aiProviderFactory.getProvider(settings.selectedProvider)
        val aiResult = provider.sendMessage(history, apiKey, settings.temperature, settings.maxTokens)

        // 6. Persist assistant response
        aiResult.onSuccess { content ->
            val assistantMsg = ChatMessageEntity(sessionId = sessionId, role = MessageRole.ASSISTANT, content = content)
            chatRepository.insertMessage(assistantMsg)
        }

        return aiResult
    }
}
