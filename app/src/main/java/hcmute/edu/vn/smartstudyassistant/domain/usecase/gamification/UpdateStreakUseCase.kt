package hcmute.edu.vn.smartstudyassistant.domain.usecase.gamification

import javax.inject.Inject

import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.XpActionType
import hcmute.edu.vn.smartstudyassistant.domain.repository.GamificationRepository
import hcmute.edu.vn.smartstudyassistant.util.Constants
import hcmute.edu.vn.smartstudyassistant.util.todayMillis

class UpdateStreakUseCase @Inject constructor(
    private val gamificationRepository: GamificationRepository,
    private val awardXpUseCase: AwardXpUseCase
) {
    suspend operator fun invoke(userId: Long) {
        val today = todayMillis()
        val yesterday = today - 24 * 60 * 60 * 1000L

        val todayStreak = gamificationRepository.getOrCreateTodayStreak(userId)
        if (todayStreak.hasActivity) return  // Already processed today

        val latest = gamificationRepository.getLatestStreak(userId)
        val wasActiveYesterday = latest?.date == yesterday && latest.hasActivity

        val newCurrentStreak = if (wasActiveYesterday) (latest?.currentStreak ?: 0) + 1 else 1
        val newLongestStreak = maxOf(newCurrentStreak, latest?.longestStreak ?: 0)

        gamificationRepository.updateStreak(
            todayStreak.copy(
                hasActivity = true,
                currentStreak = newCurrentStreak,
                longestStreak = newLongestStreak
            )
        )

        // Award streak bonus XP
        val bonusXp = newCurrentStreak * Constants.XP_STREAK_MULTIPLIER
        awardXpUseCase(userId, XpActionType.STREAK_BONUS, bonusXp, "Streak day $newCurrentStreak")
    }
}
