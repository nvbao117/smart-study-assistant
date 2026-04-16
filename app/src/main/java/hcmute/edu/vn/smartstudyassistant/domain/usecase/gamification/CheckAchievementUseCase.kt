package hcmute.edu.vn.smartstudyassistant.domain.usecase.gamification

import javax.inject.Inject

import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.XpActionType
import hcmute.edu.vn.smartstudyassistant.domain.repository.GamificationRepository

class CheckAchievementUseCase @Inject constructor(
    private val gamificationRepository: GamificationRepository,
    private val awardXpUseCase: AwardXpUseCase
) {
    suspend operator fun invoke(userId: Long, requirementType: String, currentValue: Int) {
        val achievements = gamificationRepository.getAchievementsByRequirementType(requirementType)

        for (achievement in achievements) {
            val progress = gamificationRepository.getOrCreateProgress(userId, achievement.id)

            // Skip already unlocked
            if (progress.isUnlocked) continue

            // Update progress
            gamificationRepository.updateAchievementProgress(userId, achievement.id, currentValue)

            // Check if unlocked
            if (currentValue >= achievement.requirementValue) {
                val now = System.currentTimeMillis()
                gamificationRepository.unlockAchievement(userId, achievement.id, now)

                // Award bonus XP
                awardXpUseCase(
                    userId = userId,
                    actionType = XpActionType.ACHIEVEMENT_UNLOCK,
                    xpAmount = achievement.xpReward,
                    description = "Unlocked: ${achievement.name}"
                )
            }
        }
    }
}
