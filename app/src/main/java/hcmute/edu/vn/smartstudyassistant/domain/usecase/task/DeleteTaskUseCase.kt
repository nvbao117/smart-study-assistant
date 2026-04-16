package hcmute.edu.vn.smartstudyassistant.domain.usecase.task

import javax.inject.Inject

import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.TaskEntity
import hcmute.edu.vn.smartstudyassistant.domain.repository.TaskRepository

class DeleteTaskUseCase @Inject constructor(private val taskRepository: TaskRepository) {

    suspend operator fun invoke(task: TaskEntity): Result<Unit> {
        return try {
            taskRepository.deleteTask(task)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
