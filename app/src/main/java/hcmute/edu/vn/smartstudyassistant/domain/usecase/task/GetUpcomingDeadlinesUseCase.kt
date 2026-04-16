package hcmute.edu.vn.smartstudyassistant.domain.usecase.task

import javax.inject.Inject

import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.TaskEntity
import hcmute.edu.vn.smartstudyassistant.domain.repository.TaskRepository
import hcmute.edu.vn.smartstudyassistant.util.daysFromNow
import kotlinx.coroutines.flow.Flow

class GetUpcomingDeadlinesUseCase @Inject constructor(private val taskRepository: TaskRepository) {

    operator fun invoke(userId: Long): Flow<List<TaskEntity>> {
        val now = System.currentTimeMillis()
        val sevenDaysLater = daysFromNow(7)
        return taskRepository.getUpcoming7Days(userId, now, sevenDaysLater)
    }
}
