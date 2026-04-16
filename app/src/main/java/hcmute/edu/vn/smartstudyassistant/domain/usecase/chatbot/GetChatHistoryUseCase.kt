package hcmute.edu.vn.smartstudyassistant.domain.usecase.chatbot

import javax.inject.Inject

import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.ChatMessageEntity
import hcmute.edu.vn.smartstudyassistant.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow

class GetChatHistoryUseCase @Inject constructor(private val chatRepository: ChatRepository) {
    operator fun invoke(sessionId: Long): Flow<List<ChatMessageEntity>> =
        chatRepository.getMessagesBySessionId(sessionId)
}
