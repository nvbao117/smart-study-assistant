package hcmute.edu.vn.smartstudyassistant.domain.usecase.dashboard

import javax.inject.Inject

import hcmute.edu.vn.smartstudyassistant.domain.model.DashboardData
import hcmute.edu.vn.smartstudyassistant.domain.model.SubjectProgress
import hcmute.edu.vn.smartstudyassistant.domain.repository.*
import hcmute.edu.vn.smartstudyassistant.util.Constants
import hcmute.edu.vn.smartstudyassistant.util.daysFromNow
import hcmute.edu.vn.smartstudyassistant.util.todayMillis
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class GetDashboardDataUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val taskRepository: TaskRepository,
    private val flashcardRepository: FlashcardRepository,
    private val pomodoroRepository: PomodoroRepository,
    private val gamificationRepository: GamificationRepository,
    private val generateDailyTipUseCase: GenerateDailyTipUseCase
) {
    suspend operator fun invoke(userId: Long): DashboardData = coroutineScope {
        val now = System.currentTimeMillis()
        val today = todayMillis()
        val todayEnd = daysFromNow(1)
        val user = userRepository.getById(userId)

        val todayTasksDeferred = async {
            taskRepository.getTopTasksByPriority(userId, Constants.DASHBOARD_MAX_TODAY_TASKS)
        }
        val focusMinutesDeferred = async {
            pomodoroRepository.getFocusMinutesByDate(userId, today, todayEnd)
        }
        val streakDeferred = async {
            gamificationRepository.getLatestStreak(userId)?.currentStreak ?: 0
        }
        val tipDeferred = async { generateDailyTipUseCase(userId) }

        DashboardData(
            userName = user?.displayName ?: "",
            todayTasks = todayTasksDeferred.await(),
            currentStreak = streakDeferred.await(),
            // upcomingDeadlines: collect from Flow in ViewModel layer (UI concern)
            upcomingDeadlines = emptyList(),
            subjectProgress = emptyList(),
            focusMinutesToday = focusMinutesDeferred.await(),
            cardsDueToday = 0,
            dailyTip = tipDeferred.await()
        )
    }
}
