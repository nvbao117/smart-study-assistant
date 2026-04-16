package hcmute.edu.vn.smartstudyassistant.domain.usecase.task

import javax.inject.Inject

import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.*
import hcmute.edu.vn.smartstudyassistant.domain.repository.TaskRepository
import hcmute.edu.vn.smartstudyassistant.util.daysFromNow
import java.util.Calendar

class ProcessRecurringTasksUseCase @Inject constructor(private val taskRepository: TaskRepository) {

    suspend operator fun invoke(userId: Long): Int {
        val now = System.currentTimeMillis()
        val dueRules = taskRepository.getDueRecurring(now)
        var created = 0

        for (rule in dueRules) {
            val templateTask = taskRepository.getTaskById(rule.taskId) ?: continue

            // Create a new task instance based on the template
            val newTask = templateTask.copy(
                id = 0,
                status = TaskStatus.TODO,
                completedAt = null,
                createdAt = now,
                dueDate = rule.nextOccurrence
            )
            taskRepository.createTask(newTask)
            created++

            // Update next occurrence
            val nextOccurrence = calculateNextOccurrence(rule)
            taskRepository.updateNextOccurrence(rule.id, nextOccurrence)
        }
        return created
    }

    private fun calculateNextOccurrence(rule: RecurringRuleEntity): Long {
        val cal = Calendar.getInstance().apply { timeInMillis = rule.nextOccurrence }
        when (rule.type) {
            RecurringType.DAILY -> cal.add(Calendar.DAY_OF_YEAR, rule.interval)
            RecurringType.WEEKLY -> cal.add(Calendar.WEEK_OF_YEAR, rule.interval)
            RecurringType.MONTHLY -> cal.add(Calendar.MONTH, rule.interval)
        }
        return cal.timeInMillis
    }
}
