package hcmute.edu.vn.smartstudyassistant.data.local.dao

import androidx.room.*
import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.DailyTipEntity

@Dao
interface DailyTipDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(tip: DailyTipEntity)

    @Query("SELECT * FROM daily_tips WHERE userId = :userId AND date = :date LIMIT 1")
    suspend fun getByDate(userId: Long, date: Long): DailyTipEntity?

    @Query("SELECT * FROM daily_tips WHERE userId = :userId ORDER BY date DESC LIMIT :limit")
    suspend fun getRecent(userId: Long, limit: Int): List<DailyTipEntity>

    @Query("DELETE FROM daily_tips WHERE userId = :userId AND date < :before")
    suspend fun deleteOlderThan(userId: Long, before: Long)
}
