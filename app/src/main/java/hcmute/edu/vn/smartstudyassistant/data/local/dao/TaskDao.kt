package hcmute.edu.vn.smartstudyassistant.data.local.dao

import androidx.room.*
import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(task: TaskEntity): Long

    @Update
    suspend fun update(task: TaskEntity)

    @Delete
    suspend fun delete(task: TaskEntity)

    @Query("SELECT * FROM tasks WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): TaskEntity?

    @Query("SELECT * FROM tasks WHERE userId = :userId ORDER BY createdAt DESC")
    fun getByUserId(userId: Long): Flow<List<TaskEntity>>

    @Query("""
        SELECT * FROM tasks
        WHERE userId = :userId AND dueDate >= :startMillis AND dueDate < :endMillis
        ORDER BY dueDate ASC, priority DESC
    """)
    fun getByDateRange(userId: Long, startMillis: Long, endMillis: Long): Flow<List<TaskEntity>>

    @Query("""
        SELECT * FROM tasks
        WHERE userId = :userId AND dueDate < :now AND status != 'DONE'
        ORDER BY dueDate ASC
    """)
    fun getOverdue(userId: Long, now: Long): Flow<List<TaskEntity>>

    @Query("""
        SELECT * FROM tasks
        WHERE userId = :userId AND dueDate >= :now AND dueDate < :sevenDaysLater AND status != 'DONE'
        ORDER BY dueDate ASC
    """)
    fun getUpcoming7Days(userId: Long, now: Long, sevenDaysLater: Long): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE userId = :userId AND priority = :priority ORDER BY dueDate ASC")
    fun getByPriority(userId: Long, priority: String): Flow<List<TaskEntity>>

    @Query("""
        SELECT * FROM tasks
        WHERE userId = :userId
        AND (title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%')
        ORDER BY createdAt DESC
    """)
    fun search(userId: Long, query: String): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE userId = :userId AND status != 'DONE' ORDER BY priority DESC LIMIT :limit")
    suspend fun getTopByPriority(userId: Long, limit: Int): List<TaskEntity>

    @Query("UPDATE tasks SET status = :status, completedAt = :completedAt WHERE id = :id")
    suspend fun updateStatus(id: Long, status: String, completedAt: Long?)

    @Query("SELECT COUNT(*) FROM tasks WHERE userId = :userId AND status = 'DONE'")
    suspend fun getCompletedTaskCount(userId: Long): Int

    @Query("SELECT COUNT(*) FROM tasks WHERE userId = :userId AND dueDate < :now AND status != 'DONE'")
    suspend fun getOverdueCount(userId: Long, now: Long): Int
}
