package hcmute.edu.vn.smartstudyassistant.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import hcmute.edu.vn.smartstudyassistant.util.Constants

@Entity(tableName = "ai_settings")
data class AiSettingsEntity(
    @PrimaryKey val userId: Long,
    val selectedProvider: AiProviderType = AiProviderType.GEMINI,
    val openaiApiKeyEncrypted: String? = null,
    val geminiApiKeyEncrypted: String? = null,
    val anthropicApiKeyEncrypted: String? = null,
    val temperature: Double = Constants.AI_DEFAULT_TEMPERATURE,
    val maxTokens: Int = Constants.AI_DEFAULT_MAX_TOKENS
)
