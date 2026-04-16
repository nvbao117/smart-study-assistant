package hcmute.edu.vn.smartstudyassistant.domain.usecase.task

import javax.inject.Inject

import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.TaskEntity
import hcmute.edu.vn.smartstudyassistant.domain.repository.TaskRepository

class UpdateTaskUseCase @Inject constructor(private val taskRepository: TaskRepository) {

    suspend operator fun invoke(task: TaskEntity): Result<Unit> {
        if (task.title.isBlank()) {
            return Result.failure(IllegalArgumentException("Task title cannot be empty"))
        }
        return try {
            taskRepository.updateTask(task)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
