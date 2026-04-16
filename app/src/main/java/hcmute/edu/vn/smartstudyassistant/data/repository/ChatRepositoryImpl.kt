package hcmute.edu.vn.smartstudyassistant.data.repository

import javax.inject.Inject

import hcmute.edu.vn.smartstudyassistant.data.local.dao.AiSettingsDao
import hcmute.edu.vn.smartstudyassistant.data.local.dao.ChatMessageDao
import hcmute.edu.vn.smartstudyassistant.data.local.dao.ChatSessionDao
import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.AiSettingsEntity
import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.ChatMessageEntity
import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.ChatSessionEntity
import hcmute.edu.vn.smartstudyassistant.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow

class ChatRepositoryImpl @Inject constructor(
    private val chatSessionDao: ChatSessionDao,
    private val chatMessageDao: ChatMessageDao,
    private val aiSettingsDao: AiSettingsDao
) : ChatRepository {
    override suspend fun createSession(session: ChatSessionEntity): Long = chatSessionDao.insert(session)
    override suspend fun deleteSession(session: ChatSessionEntity) = chatSessionDao.delete(session)
    override suspend fun getSessionById(id: Long): ChatSessionEntity? = chatSessionDao.getById(id)
    override fun getSessionsByUserId(userId: Long): Flow<List<ChatSessionEntity>> = chatSessionDao.getByUserId(userId)
    override suspend fun insertMessage(message: ChatMessageEntity): Long = chatMessageDao.insert(message)
    override fun getMessagesBySessionId(sessionId: Long): Flow<List<ChatMessageEntity>> = chatMessageDao.getBySessionId(sessionId)
    override suspend fun getRecentMessages(sessionId: Long, limit: Int): List<ChatMessageEntity> =
        chatMessageDao.getRecentBySessionId(sessionId, limit).reversed()
    override suspend fun getAiSettings(userId: Long): AiSettingsEntity? = aiSettingsDao.getByUserId(userId)
    override suspend fun saveAiSettings(settings: AiSettingsEntity) = aiSettingsDao.insertOrUpdate(settings)
}
