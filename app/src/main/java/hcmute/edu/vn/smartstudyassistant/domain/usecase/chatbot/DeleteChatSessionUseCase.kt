package hcmute.edu.vn.smartstudyassistant.domain.usecase.chatbot

import javax.inject.Inject

import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.ChatSessionEntity
import hcmute.edu.vn.smartstudyassistant.domain.repository.ChatRepository

class DeleteChatSessionUseCase @Inject constructor(private val chatRepository: ChatRepository) {
    suspend operator fun invoke(session: ChatSessionEntity): Result<Unit> =
        try { chatRepository.deleteSession(session); Result.success(Unit) } catch (e: Exception) { Result.failure(e) }
}
