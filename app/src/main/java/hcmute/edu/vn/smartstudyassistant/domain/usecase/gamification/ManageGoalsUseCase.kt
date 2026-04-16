package hcmute.edu.vn.smartstudyassistant.domain.usecase.gamification

import javax.inject.Inject

import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.StudyGoalEntity
import hcmute.edu.vn.smartstudyassistant.domain.repository.GamificationRepository
import kotlinx.coroutines.flow.Flow

class ManageGoalsUseCase @Inject constructor(private val gamificationRepository: GamificationRepository) {

    suspend fun createGoal(goal: StudyGoalEntity): Result<Long> =
        try { Result.success(gamificationRepository.createGoal(goal)) } catch (e: Exception) { Result.failure(e) }

    suspend fun updateGoal(goal: StudyGoalEntity): Result<Unit> =
        try { gamificationRepository.updateGoal(goal); Result.success(Unit) } catch (e: Exception) { Result.failure(e) }

    suspend fun deleteGoal(goal: StudyGoalEntity): Result<Unit> =
        try { gamificationRepository.deleteGoal(goal); Result.success(Unit) } catch (e: Exception) { Result.failure(e) }

    fun getActiveGoals(userId: Long): Flow<List<StudyGoalEntity>> =
        gamificationRepository.getActiveGoals(userId)
}
