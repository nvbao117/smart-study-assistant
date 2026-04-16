package hcmute.edu.vn.smartstudyassistant.data.local.dao

import androidx.room.*
import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.ReminderEntity

@Dao
interface ReminderDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(reminder: ReminderEntity): Long

    @Update
    suspend fun update(reminder: ReminderEntity)

    @Delete
    suspend fun delete(reminder: ReminderEntity)

    @Query("SELECT * FROM reminders WHERE taskId = :taskId")
    suspend fun getByTaskId(taskId: Long): List<ReminderEntity>

    @Query("""
        SELECT * FROM reminders
        WHERE remindAt >= :now AND isSent = 0
        ORDER BY remindAt ASC
        LIMIT :limit
    """)
    suspend fun getUpcoming(now: Long, limit: Int): List<ReminderEntity>

    @Query("UPDATE reminders SET isSent = 1 WHERE id = :id")
    suspend fun markSent(id: Long)

    @Query("DELETE FROM reminders WHERE taskId = :taskId")
    suspend fun deleteByTaskId(taskId: Long)
}
