package hcmute.edu.vn.smartstudyassistant.domain.usecase.task

import javax.inject.Inject

import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.*
import hcmute.edu.vn.smartstudyassistant.domain.repository.TaskRepository

class CreateTaskUseCase @Inject constructor(private val taskRepository: TaskRepository) {

    suspend operator fun invoke(
        task: TaskEntity,
        subtasks: List<SubtaskEntity> = emptyList(),
        recurringRule: RecurringRuleEntity? = null,
        reminder: ReminderEntity? = null
    ): Result<Long> {
        if (task.title.isBlank()) {
            return Result.failure(IllegalArgumentException("Task title cannot be empty"))
        }
        return try {
            Result.success(taskRepository.createTask(task, subtasks, recurringRule, reminder))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
