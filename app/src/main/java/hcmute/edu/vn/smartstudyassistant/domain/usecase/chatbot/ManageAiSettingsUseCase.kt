package hcmute.edu.vn.smartstudyassistant.domain.usecase.chatbot

import javax.inject.Inject

import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.AiProviderType
import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.AiSettingsEntity
import hcmute.edu.vn.smartstudyassistant.domain.repository.ChatRepository
import hcmute.edu.vn.smartstudyassistant.util.EncryptionUtil

class ManageAiSettingsUseCase @Inject constructor(private val chatRepository: ChatRepository) {

    suspend fun getSettings(userId: Long): AiSettingsEntity =
        chatRepository.getAiSettings(userId) ?: AiSettingsEntity(userId = userId)

    suspend fun saveSettings(settings: AiSettingsEntity): Result<Unit> =
        try { chatRepository.saveAiSettings(settings); Result.success(Unit) } catch (e: Exception) { Result.failure(e) }

    /** Encrypts the API key before saving */
    suspend fun saveApiKey(userId: Long, provider: AiProviderType, rawApiKey: String): Result<Unit> {
        val currentSettings = getSettings(userId)
        val encrypted = try { EncryptionUtil.encrypt(rawApiKey) } catch (e: Exception) {
            return Result.failure(e)
        }
        val updated = when (provider) {
            AiProviderType.OPENAI -> currentSettings.copy(openaiApiKeyEncrypted = encrypted)
            AiProviderType.GEMINI -> currentSettings.copy(geminiApiKeyEncrypted = encrypted)
            AiProviderType.ANTHROPIC -> currentSettings.copy(anthropicApiKeyEncrypted = encrypted)
        }
        return saveSettings(updated)
    }

    /** Returns the decrypted API key for the given provider */
    suspend fun getDecryptedApiKey(userId: Long, provider: AiProviderType): String? {
        val settings = chatRepository.getAiSettings(userId) ?: return null
        val encrypted = when (provider) {
            AiProviderType.OPENAI -> settings.openaiApiKeyEncrypted
            AiProviderType.GEMINI -> settings.geminiApiKeyEncrypted
            AiProviderType.ANTHROPIC -> settings.anthropicApiKeyEncrypted
        } ?: return null
        return try { EncryptionUtil.decrypt(encrypted) } catch (e: Exception) { null }
    }
}
