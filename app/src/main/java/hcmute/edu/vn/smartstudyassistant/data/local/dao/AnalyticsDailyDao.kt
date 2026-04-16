package hcmute.edu.vn.smartstudyassistant.data.local.dao

import androidx.room.*
import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.AnalyticsDailyEntity

@Dao
interface AnalyticsDailyDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(analytics: AnalyticsDailyEntity)

    @Update
    suspend fun update(analytics: AnalyticsDailyEntity)

    @Query("SELECT * FROM analytics_daily WHERE userId = :userId AND date = :date LIMIT 1")
    suspend fun getByDate(userId: Long, date: Long): AnalyticsDailyEntity?

    @Query("SELECT * FROM analytics_daily WHERE userId = :userId AND date >= :startMillis AND date < :endMillis ORDER BY date ASC")
    suspend fun getByDateRange(userId: Long, startMillis: Long, endMillis: Long): List<AnalyticsDailyEntity>

    @Query("UPDATE analytics_daily SET tasksCompleted = tasksCompleted + 1 WHERE userId = :userId AND date = :date")
    suspend fun incrementTasksCompleted(userId: Long, date: Long)

    @Query("UPDATE analytics_daily SET focusMinutes = focusMinutes + :minutes WHERE userId = :userId AND date = :date")
    suspend fun addFocusMinutes(userId: Long, date: Long, minutes: Int)

    @Query("UPDATE analytics_daily SET cardsReviewed = cardsReviewed + :count WHERE userId = :userId AND date = :date")
    suspend fun addCardsReviewed(userId: Long, date: Long, count: Int)

    @Query("UPDATE analytics_daily SET xpEarned = xpEarned + :xp WHERE userId = :userId AND date = :date")
    suspend fun addXpEarned(userId: Long, date: Long, xp: Int)

    @Query("SELECT COALESCE(SUM(focusMinutes), 0) FROM analytics_daily WHERE userId = :userId AND date >= :startMillis AND date < :endMillis")
    suspend fun getTotalFocusMinutes(userId: Long, startMillis: Long, endMillis: Long): Int

    @Query("SELECT COALESCE(SUM(tasksCompleted), 0) FROM analytics_daily WHERE userId = :userId AND date >= :startMillis AND date < :endMillis")
    suspend fun getTotalTasksCompleted(userId: Long, startMillis: Long, endMillis: Long): Int
}
