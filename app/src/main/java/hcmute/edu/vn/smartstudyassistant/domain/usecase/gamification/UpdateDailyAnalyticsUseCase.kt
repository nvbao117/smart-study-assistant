package hcmute.edu.vn.smartstudyassistant.domain.usecase.gamification

import javax.inject.Inject

import hcmute.edu.vn.smartstudyassistant.domain.repository.GamificationRepository
import hcmute.edu.vn.smartstudyassistant.util.todayMillis

class UpdateDailyAnalyticsUseCase @Inject constructor(private val gamificationRepository: GamificationRepository) {

    suspend fun onTaskCompleted(userId: Long) {
        val today = todayMillis()
        gamificationRepository.incrementTasksCompleted(userId, today)
    }

    suspend fun onFocusSessionCompleted(userId: Long, focusMinutes: Int) {
        val today = todayMillis()
        gamificationRepository.addFocusMinutes(userId, today, focusMinutes)
    }

    suspend fun onCardsReviewed(userId: Long, cardCount: Int) {
        val today = todayMillis()
        gamificationRepository.addCardsReviewed(userId, today, cardCount)
    }
}
