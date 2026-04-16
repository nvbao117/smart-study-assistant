package hcmute.edu.vn.smartstudyassistant.data.local.dao

import androidx.room.*
import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.SubtaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SubtaskDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(subtask: SubtaskEntity): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAll(subtasks: List<SubtaskEntity>)

    @Update
    suspend fun update(subtask: SubtaskEntity)

    @Delete
    suspend fun delete(subtask: SubtaskEntity)

    @Query("SELECT * FROM subtasks WHERE taskId = :taskId ORDER BY sortOrder ASC")
    fun getByTaskId(taskId: Long): Flow<List<SubtaskEntity>>

    @Query("UPDATE subtasks SET isCompleted = :isCompleted WHERE id = :id")
    suspend fun toggleComplete(id: Long, isCompleted: Boolean)

    @Query("UPDATE subtasks SET isCompleted = 1 WHERE taskId = :taskId")
    suspend fun completeAllForTask(taskId: Long)

    @Query("DELETE FROM subtasks WHERE taskId = :taskId")
    suspend fun deleteByTaskId(taskId: Long)
}
