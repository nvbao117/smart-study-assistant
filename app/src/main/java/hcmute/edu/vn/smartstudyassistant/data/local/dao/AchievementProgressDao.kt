package hcmute.edu.vn.smartstudyassistant.data.local.dao

import androidx.room.*
import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.AchievementProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AchievementProgressDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(progress: AchievementProgressEntity)

    @Update
    suspend fun update(progress: AchievementProgressEntity)

    @Query("SELECT * FROM achievement_progress WHERE userId = :userId AND achievementId = :achievementId LIMIT 1")
    suspend fun get(userId: Long, achievementId: Long): AchievementProgressEntity?

    @Query("SELECT * FROM achievement_progress WHERE userId = :userId")
    fun getByUserId(userId: Long): Flow<List<AchievementProgressEntity>>

    @Query("SELECT * FROM achievement_progress WHERE userId = :userId AND isUnlocked = 0")
    suspend fun getNotUnlocked(userId: Long): List<AchievementProgressEntity>

    @Query("UPDATE achievement_progress SET currentValue = :value WHERE userId = :userId AND achievementId = :achievementId")
    suspend fun updateProgress(userId: Long, achievementId: Long, value: Int)

    @Query("UPDATE achievement_progress SET isUnlocked = 1, unlockedAt = :timestamp WHERE userId = :userId AND achievementId = :achievementId")
    suspend fun markUnlocked(userId: Long, achievementId: Long, timestamp: Long)
}
