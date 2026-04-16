package hcmute.edu.vn.smartstudyassistant.data.repository

import javax.inject.Inject

import hcmute.edu.vn.smartstudyassistant.data.local.dao.*
import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.*
import hcmute.edu.vn.smartstudyassistant.domain.model.LevelInfo
import hcmute.edu.vn.smartstudyassistant.domain.repository.GamificationRepository
import hcmute.edu.vn.smartstudyassistant.util.Constants
import hcmute.edu.vn.smartstudyassistant.util.todayMillis
import kotlinx.coroutines.flow.Flow
import kotlin.math.sqrt

class GamificationRepositoryImpl @Inject constructor(
    private val xpLogDao: XpLogDao,
    private val userLevelDao: UserLevelDao,
    private val achievementDao: AchievementDao,
    private val achievementProgressDao: AchievementProgressDao,
    private val streakHistoryDao: StreakHistoryDao,
    private val studyGoalDao: StudyGoalDao,
    private val analyticsDailyDao: AnalyticsDailyDao
) : GamificationRepository {

    // ── XP & Level ───────────────────────────────────────────────────────────

    override suspend fun awardXp(log: XpLogEntity): LevelInfo {
        xpLogDao.insert(log)
        val current = userLevelDao.getByUserId(log.userId)
            ?: UserLevelEntity(userId = log.userId)
        val newTotalXp = current.totalXp + log.xpAmount
        val newLevel = calculateLevel(newTotalXp)
        val xpToNext = calculateXpToNext(newTotalXp, newLevel)
        val didLevelUp = newLevel > current.currentLevel
        if (current.totalXp == 0 && newTotalXp > 0) {
            userLevelDao.insertOrUpdate(UserLevelEntity(log.userId, newTotalXp, newLevel, xpToNext))
        } else {
            userLevelDao.updateLevel(log.userId, newTotalXp, newLevel, xpToNext)
        }
        return LevelInfo(newLevel, newTotalXp, xpToNext, didLevelUp)
    }

    override suspend fun getUserLevel(userId: Long): UserLevelEntity? = userLevelDao.getByUserId(userId)

    override suspend fun initUserLevel(userId: Long) {
        if (userLevelDao.getByUserId(userId) == null) {
            userLevelDao.insertOrUpdate(UserLevelEntity(userId = userId))
        }
    }

    private fun calculateLevel(totalXp: Int): Int =
        sqrt(totalXp / Constants.XP_PER_LEVEL_FACTOR).toInt() + 1

    private fun calculateXpToNext(totalXp: Int, level: Int): Int =
        (level * level * Constants.XP_PER_LEVEL_FACTOR - totalXp).toInt().coerceAtLeast(0)

    // ── Achievements ──────────────────────────────────────────────────────────

    override fun getAllAchievements(): Flow<List<AchievementEntity>> = achievementDao.getAll()

    override suspend fun getAchievementsByRequirementType(type: String) =
        achievementDao.getByRequirementType(type)

    override fun getUserAchievementProgress(userId: Long) =
        achievementProgressDao.getByUserId(userId)

    override suspend fun getOrCreateProgress(userId: Long, achievementId: Long): AchievementProgressEntity {
        return achievementProgressDao.get(userId, achievementId) ?: run {
            val progress = AchievementProgressEntity(userId, achievementId)
            achievementProgressDao.insert(progress)
            progress
        }
    }

    override suspend fun updateAchievementProgress(userId: Long, achievementId: Long, value: Int) =
        achievementProgressDao.updateProgress(userId, achievementId, value)

    override suspend fun unlockAchievement(userId: Long, achievementId: Long, timestamp: Long) =
        achievementProgressDao.markUnlocked(userId, achievementId, timestamp)

    // ── Streak ────────────────────────────────────────────────────────────────

    override suspend fun getOrCreateTodayStreak(userId: Long): StreakHistoryEntity {
        val today = todayMillis()
        return streakHistoryDao.getByDate(userId, today) ?: run {
            val entry = StreakHistoryEntity(userId = userId, date = today)
            streakHistoryDao.insertOrUpdate(entry)
            entry
        }
    }

    override suspend fun updateStreak(streak: StreakHistoryEntity) =
        streakHistoryDao.insertOrUpdate(streak)

    override suspend fun getLatestStreak(userId: Long): StreakHistoryEntity? =
        streakHistoryDao.getLatest(userId)

    // ── Analytics ─────────────────────────────────────────────────────────────

    override suspend fun getOrCreateTodayAnalytics(userId: Long): AnalyticsDailyEntity {
        val today = todayMillis()
        return analyticsDailyDao.getByDate(userId, today) ?: run {
            val entry = AnalyticsDailyEntity(userId = userId, date = today)
            analyticsDailyDao.insert(entry)
            entry
        }
    }

    override suspend fun updateAnalytics(analytics: AnalyticsDailyEntity) =
        analyticsDailyDao.update(analytics)

    override suspend fun getAnalyticsByDateRange(userId: Long, startMillis: Long, endMillis: Long) =
        analyticsDailyDao.getByDateRange(userId, startMillis, endMillis)

    override suspend fun incrementTasksCompleted(userId: Long, date: Long) {
        getOrCreateTodayAnalytics(userId)
        analyticsDailyDao.incrementTasksCompleted(userId, date)
    }

    override suspend fun addFocusMinutes(userId: Long, date: Long, minutes: Int) {
        getOrCreateTodayAnalytics(userId)
        analyticsDailyDao.addFocusMinutes(userId, date, minutes)
    }

    override suspend fun addCardsReviewed(userId: Long, date: Long, count: Int) {
        getOrCreateTodayAnalytics(userId)
        analyticsDailyDao.addCardsReviewed(userId, date, count)
    }

    override suspend fun addXpEarned(userId: Long, date: Long, xp: Int) {
        getOrCreateTodayAnalytics(userId)
        analyticsDailyDao.addXpEarned(userId, date, xp)
    }

    // ── Goals ─────────────────────────────────────────────────────────────────

    override suspend fun createGoal(goal: StudyGoalEntity): Long = studyGoalDao.insert(goal)
    override suspend fun updateGoal(goal: StudyGoalEntity) = studyGoalDao.update(goal)
    override suspend fun deleteGoal(goal: StudyGoalEntity) = studyGoalDao.delete(goal)
    override fun getActiveGoals(userId: Long): Flow<List<StudyGoalEntity>> = studyGoalDao.getActiveByUserId(userId)
}
