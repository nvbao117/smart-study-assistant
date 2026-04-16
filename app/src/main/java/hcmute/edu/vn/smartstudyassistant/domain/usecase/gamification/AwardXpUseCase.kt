package hcmute.edu.vn.smartstudyassistant.domain.usecase.gamification

import javax.inject.Inject

import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.XpLogEntity
import hcmute.edu.vn.smartstudyassistant.domain.model.LevelInfo
import hcmute.edu.vn.smartstudyassistant.domain.repository.GamificationRepository
import hcmute.edu.vn.smartstudyassistant.util.Constants
import hcmute.edu.vn.smartstudyassistant.util.todayMillis

class AwardXpUseCase @Inject constructor(private val gamificationRepository: GamificationRepository) {

    suspend operator fun invoke(
        userId: Long,
        actionType: hcmute.edu.vn.smartstudyassistant.data.local.db.entity.XpActionType,
        xpAmount: Int,
        description: String = ""
    ): LevelInfo {
        val log = XpLogEntity(
            userId = userId,
            actionType = actionType,
            xpAmount = xpAmount,
            description = description
        )
        val levelInfo = gamificationRepository.awardXp(log)

        // Update daily analytics
        gamificationRepository.addXpEarned(userId, todayMillis(), xpAmount)

        return levelInfo
    }
}
