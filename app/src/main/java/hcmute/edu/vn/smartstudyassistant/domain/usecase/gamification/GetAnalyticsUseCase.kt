package hcmute.edu.vn.smartstudyassistant.domain.usecase.gamification

import javax.inject.Inject

import hcmute.edu.vn.smartstudyassistant.domain.model.Analytics
import hcmute.edu.vn.smartstudyassistant.domain.repository.GamificationRepository
import hcmute.edu.vn.smartstudyassistant.util.daysFromNow
import hcmute.edu.vn.smartstudyassistant.util.todayMillis

class GetAnalyticsUseCase @Inject constructor(private val gamificationRepository: GamificationRepository) {

    suspend operator fun invoke(userId: Long, periodDays: Int = 7): Analytics {
        val end = daysFromNow(1)
        val start = daysFromNow(-periodDays)
        val data = gamificationRepository.getAnalyticsByDateRange(userId, start, end)

        return Analytics(
            period = when (periodDays) { 1 -> "daily"; 7 -> "weekly"; else -> "monthly" },
            data = data,
            totalFocusMinutes = data.sumOf { it.focusMinutes },
            totalTasksCompleted = data.sumOf { it.tasksCompleted },
            totalCardsReviewed = data.sumOf { it.cardsReviewed },
            totalXpEarned = data.sumOf { it.xpEarned }
        )
    }
}
