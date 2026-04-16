package hcmute.edu.vn.smartstudyassistant.domain.repository

import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.*
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    suspend fun createTask(
        task: TaskEntity,
        subtasks: List<SubtaskEntity> = emptyList(),
        recurringRule: RecurringRuleEntity? = null,
        reminder: ReminderEntity? = null
    ): Long
    suspend fun updateTask(task: TaskEntity)
    suspend fun deleteTask(task: TaskEntity)
    suspend fun getTaskById(id: Long): TaskEntity?
    fun getTasksByUserId(userId: Long): Flow<List<TaskEntity>>
    fun getTasksByDateRange(userId: Long, startMillis: Long, endMillis: Long): Flow<List<TaskEntity>>
    fun getOverdueTasks(userId: Long, now: Long): Flow<List<TaskEntity>>
    fun getUpcoming7Days(userId: Long, now: Long, sevenDaysLater: Long): Flow<List<TaskEntity>>
    fun searchTasks(userId: Long, query: String): Flow<List<TaskEntity>>
    suspend fun updateTaskStatus(id: Long, status: TaskStatus, completedAt: Long?)
    suspend fun completeAllSubtasks(taskId: Long)
    suspend fun getCompletedTaskCount(userId: Long): Int
    suspend fun getOverdueCount(userId: Long, now: Long): Int
    suspend fun getTopTasksByPriority(userId: Long, limit: Int): List<TaskEntity>
    suspend fun getByTaskId(taskId: Long): RecurringRuleEntity?
    suspend fun getDueRecurring(now: Long): List<RecurringRuleEntity>
    suspend fun updateNextOccurrence(id: Long, next: Long)
}
