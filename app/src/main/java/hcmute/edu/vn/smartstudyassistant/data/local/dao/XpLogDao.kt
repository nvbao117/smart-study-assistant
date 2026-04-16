package hcmute.edu.vn.smartstudyassistant.data.local.dao

import androidx.room.*
import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.XpLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface XpLogDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(log: XpLogEntity): Long

    @Query("SELECT * FROM xp_logs WHERE userId = :userId ORDER BY earnedAt DESC")
    fun getByUserId(userId: Long): Flow<List<XpLogEntity>>

    @Query("SELECT COALESCE(SUM(xpAmount), 0) FROM xp_logs WHERE userId = :userId AND earnedAt >= :startMillis AND earnedAt < :endMillis")
    suspend fun getTotalXpByDateRange(userId: Long, startMillis: Long, endMillis: Long): Int

    @Query("SELECT COALESCE(SUM(xpAmount), 0) FROM xp_logs WHERE userId = :userId")
    suspend fun getTotalXp(userId: Long): Int
}
