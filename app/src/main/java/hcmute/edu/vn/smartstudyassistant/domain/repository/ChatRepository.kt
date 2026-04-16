package hcmute.edu.vn.smartstudyassistant.domain.repository

import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.*
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    suspend fun createSession(session: ChatSessionEntity): Long
    suspend fun deleteSession(session: ChatSessionEntity)
    suspend fun getSessionById(id: Long): ChatSessionEntity?
    fun getSessionsByUserId(userId: Long): Flow<List<ChatSessionEntity>>
    suspend fun insertMessage(message: ChatMessageEntity): Long
    fun getMessagesBySessionId(sessionId: Long): Flow<List<ChatMessageEntity>>
    suspend fun getRecentMessages(sessionId: Long, limit: Int): List<ChatMessageEntity>
    suspend fun getAiSettings(userId: Long): AiSettingsEntity?
    suspend fun saveAiSettings(settings: AiSettingsEntity)
}
