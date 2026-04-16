package hcmute.edu.vn.smartstudyassistant.domain.usecase.task

import javax.inject.Inject

import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.TaskEntity
import hcmute.edu.vn.smartstudyassistant.domain.repository.TaskRepository
import hcmute.edu.vn.smartstudyassistant.util.endOfDay
import hcmute.edu.vn.smartstudyassistant.util.startOfDay
import kotlinx.coroutines.flow.Flow

class GetTasksByDateUseCase @Inject constructor(private val taskRepository: TaskRepository) {

    operator fun invoke(userId: Long, dateMillis: Long): Flow<List<TaskEntity>> {
        val start = dateMillis.startOfDay()
        val end = dateMillis.endOfDay()
        return taskRepository.getTasksByDateRange(userId, start, end)
    }
}
