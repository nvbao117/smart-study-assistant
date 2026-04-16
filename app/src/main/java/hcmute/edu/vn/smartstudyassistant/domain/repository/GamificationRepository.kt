package hcmute.edu.vn.smartstudyassistant.domain.repository

import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.*
import hcmute.edu.vn.smartstudyassistant.domain.model.LevelInfo
import kotlinx.coroutines.flow.Flow

interface GamificationRepository {
    // XP & Level
    suspend fun awardXp(log: XpLogEntity): LevelInfo
    suspend fun getUserLevel(userId: Long): UserLevelEntity?
    suspend fun initUserLevel(userId: Long)
    // Achievements
    fun getAllAchievements(): Flow<List<AchievementEntity>>
    suspend fun getAchievementsByRequirementType(type: String): List<AchievementEntity>
    fun getUserAchievementProgress(userId: Long): Flow<List<AchievementProgressEntity>>
    suspend fun getOrCreateProgress(userId: Long, achievementId: Long): AchievementProgressEntity
    suspend fun updateAchievementProgress(userId: Long, achievementId: Long, value: Int)
    suspend fun unlockAchievement(userId: Long, achievementId: Long, timestamp: Long)
    // Streak
    suspend fun getOrCreateTodayStreak(userId: Long): StreakHistoryEntity
    suspend fun updateStreak(streak: StreakHistoryEntity)
    suspend fun getLatestStreak(userId: Long): StreakHistoryEntity?
    // Analytics
    suspend fun getOrCreateTodayAnalytics(userId: Long): AnalyticsDailyEntity
    suspend fun updateAnalytics(analytics: AnalyticsDailyEntity)
    suspend fun getAnalyticsByDateRange(userId: Long, startMillis: Long, endMillis: Long): List<AnalyticsDailyEntity>
    suspend fun incrementTasksCompleted(userId: Long, date: Long)
    suspend fun addFocusMinutes(userId: Long, date: Long, minutes: Int)
    suspend fun addCardsReviewed(userId: Long, date: Long, count: Int)
    suspend fun addXpEarned(userId: Long, date: Long, xp: Int)
    // Goals
    suspend fun createGoal(goal: StudyGoalEntity): Long
    suspend fun updateGoal(goal: StudyGoalEntity)
    suspend fun deleteGoal(goal: StudyGoalEntity)
    fun getActiveGoals(userId: Long): Flow<List<StudyGoalEntity>>
}
