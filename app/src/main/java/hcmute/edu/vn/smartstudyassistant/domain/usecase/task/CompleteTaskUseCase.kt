package hcmute.edu.vn.smartstudyassistant.domain.usecase.task

import javax.inject.Inject

import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.TaskStatus
import hcmute.edu.vn.smartstudyassistant.domain.repository.TaskRepository

class CompleteTaskUseCase @Inject constructor(private val taskRepository: TaskRepository) {

    suspend operator fun invoke(taskId: Long): Result<Unit> {
        return try {
            val now = System.currentTimeMillis()
            taskRepository.updateTaskStatus(taskId, TaskStatus.DONE, now)
            taskRepository.completeAllSubtasks(taskId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
