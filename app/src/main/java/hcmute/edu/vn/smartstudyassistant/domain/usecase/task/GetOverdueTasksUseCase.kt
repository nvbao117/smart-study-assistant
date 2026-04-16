package hcmute.edu.vn.smartstudyassistant.domain.usecase.task

import javax.inject.Inject

import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.TaskEntity
import hcmute.edu.vn.smartstudyassistant.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow

class GetOverdueTasksUseCase @Inject constructor(private val taskRepository: TaskRepository) {

    operator fun invoke(userId: Long): Flow<List<TaskEntity>> {
        return taskRepository.getOverdueTasks(userId, System.currentTimeMillis())
    }
}
