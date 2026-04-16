package hcmute.edu.vn.smartstudyassistant.domain.usecase.chatbot

import javax.inject.Inject

import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.ChatSessionEntity
import hcmute.edu.vn.smartstudyassistant.domain.repository.ChatRepository

class CreateChatSessionUseCase @Inject constructor(private val chatRepository: ChatRepository) {
    suspend operator fun invoke(session: ChatSessionEntity): Result<Long> =
        try { Result.success(chatRepository.createSession(session)) } catch (e: Exception) { Result.failure(e) }
}
