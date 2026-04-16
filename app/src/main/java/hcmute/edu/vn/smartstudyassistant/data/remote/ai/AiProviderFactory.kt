package hcmute.edu.vn.smartstudyassistant.data.remote.ai

import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.AiProviderType
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AiProviderFactory @Inject constructor() {

    fun getProvider(type: AiProviderType): AiChatProvider = when (type) {
        AiProviderType.OPENAI -> OpenAiProvider()
        AiProviderType.GEMINI -> GeminiProvider()
        AiProviderType.ANTHROPIC -> AnthropicProvider()
    }
}
