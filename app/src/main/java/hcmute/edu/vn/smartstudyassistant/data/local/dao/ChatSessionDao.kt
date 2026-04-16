package hcmute.edu.vn.smartstudyassistant.data.local.dao

import androidx.room.*
import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.ChatSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatSessionDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(session: ChatSessionEntity): Long

    @Update
    suspend fun update(session: ChatSessionEntity)

    @Delete
    suspend fun delete(session: ChatSessionEntity)

    @Query("SELECT * FROM chat_sessions WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): ChatSessionEntity?

    @Query("SELECT * FROM chat_sessions WHERE userId = :userId ORDER BY createdAt DESC")
    fun getByUserId(userId: Long): Flow<List<ChatSessionEntity>>
}
