package hcmute.edu.vn.smartstudyassistant.data.repository

import javax.inject.Inject

import androidx.room.withTransaction
import hcmute.edu.vn.smartstudyassistant.data.local.dao.*
import hcmute.edu.vn.smartstudyassistant.data.local.db.AppDatabase
import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.*
import hcmute.edu.vn.smartstudyassistant.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow

class TaskRepositoryImpl @Inject constructor(
    private val database: AppDatabase,
    private val taskDao: TaskDao,
    private val subtaskDao: SubtaskDao,
    private val recurringRuleDao: RecurringRuleDao,
    private val reminderDao: ReminderDao
) : TaskRepository {

    override suspend fun createTask(
        task: TaskEntity,
        subtasks: List<SubtaskEntity>,
        recurringRule: RecurringRuleEntity?,
        reminder: ReminderEntity?
    ): Long = database.withTransaction {
        val taskId = taskDao.insert(task)
        if (subtasks.isNotEmpty()) {
            subtaskDao.insertAll(subtasks.map { it.copy(taskId = taskId) })
        }
        recurringRule?.let { recurringRuleDao.insert(it.copy(taskId = taskId)) }
        reminder?.let { reminderDao.insert(it.copy(taskId = taskId)) }
        taskId
    }

    override suspend fun updateTask(task: TaskEntity) = taskDao.update(task)

    override suspend fun deleteTask(task: TaskEntity) = taskDao.delete(task)

    override suspend fun getTaskById(id: Long): TaskEntity? = taskDao.getById(id)

    override fun getTasksByUserId(userId: Long): Flow<List<TaskEntity>> =
        taskDao.getByUserId(userId)

    override fun getTasksByDateRange(userId: Long, startMillis: Long, endMillis: Long) =
        taskDao.getByDateRange(userId, startMillis, endMillis)

    override fun getOverdueTasks(userId: Long, now: Long) = taskDao.getOverdue(userId, now)

    override fun getUpcoming7Days(userId: Long, now: Long, sevenDaysLater: Long) =
        taskDao.getUpcoming7Days(userId, now, sevenDaysLater)

    override fun searchTasks(userId: Long, query: String) = taskDao.search(userId, query)

    override suspend fun updateTaskStatus(id: Long, status: TaskStatus, completedAt: Long?) =
        taskDao.updateStatus(id, status.name, completedAt)

    override suspend fun completeAllSubtasks(taskId: Long) =
        subtaskDao.completeAllForTask(taskId)

    override suspend fun getCompletedTaskCount(userId: Long) =
        taskDao.getCompletedTaskCount(userId)

    override suspend fun getOverdueCount(userId: Long, now: Long) =
        taskDao.getOverdueCount(userId, now)

    override suspend fun getTopTasksByPriority(userId: Long, limit: Int) =
        taskDao.getTopByPriority(userId, limit)

    override suspend fun getByTaskId(taskId: Long): RecurringRuleEntity? =
        recurringRuleDao.getByTaskId(taskId)

    override suspend fun getDueRecurring(now: Long): List<RecurringRuleEntity> =
        recurringRuleDao.getDueRecurring(now)

    override suspend fun updateNextOccurrence(id: Long, next: Long) =
        recurringRuleDao.updateNextOccurrence(id, next)
}
