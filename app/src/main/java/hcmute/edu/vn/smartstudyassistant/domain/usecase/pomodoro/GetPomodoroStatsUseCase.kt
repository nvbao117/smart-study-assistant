package hcmute.edu.vn.smartstudyassistant.domain.usecase.pomodoro

import javax.inject.Inject

import hcmute.edu.vn.smartstudyassistant.domain.model.PomodoroStats
import hcmute.edu.vn.smartstudyassistant.domain.repository.PomodoroRepository
import hcmute.edu.vn.smartstudyassistant.util.daysFromNow
import hcmute.edu.vn.smartstudyassistant.util.todayMillis

class GetPomodoroStatsUseCase @Inject constructor(private val pomodoroRepository: PomodoroRepository) {

    suspend operator fun invoke(userId: Long): PomodoroStats {
        val todayStart = todayMillis()
        val todayEnd = daysFromNow(1)
        val weekStart = daysFromNow(-7)

        return PomodoroStats(
            focusToday = pomodoroRepository.getFocusMinutesByDate(userId, todayStart, todayEnd),
            focusWeek = pomodoroRepository.getFocusMinutesByDate(userId, weekStart, todayEnd),
            totalSessions = pomodoroRepository.getTotalFocusSessions(userId),
            avgDuration = pomodoroRepository.getAverageFocusDuration(userId),
            mostProductiveHour = pomodoroRepository.getMostProductiveHour(userId)
        )
    }
}
