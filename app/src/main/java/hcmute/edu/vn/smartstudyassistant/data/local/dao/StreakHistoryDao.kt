package hcmute.edu.vn.smartstudyassistant.data.local.dao

import androidx.room.*
import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.StreakHistoryEntity

@Dao
interface StreakHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(streak: StreakHistoryEntity)

    @Query("SELECT * FROM streak_history WHERE userId = :userId AND date = :date LIMIT 1")
    suspend fun getByDate(userId: Long, date: Long): StreakHistoryEntity?

    @Query("SELECT * FROM streak_history WHERE userId = :userId ORDER BY date DESC LIMIT 1")
    suspend fun getLatest(userId: Long): StreakHistoryEntity?

    @Query("SELECT * FROM streak_history WHERE userId = :userId ORDER BY date DESC LIMIT :limit")
    suspend fun getRecent(userId: Long, limit: Int): List<StreakHistoryEntity>
}
