package hcmute.edu.vn.smartstudyassistant.data.local.dao

import androidx.room.*
import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.UserLevelEntity

@Dao
interface UserLevelDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(level: UserLevelEntity)

    @Query("SELECT * FROM user_levels WHERE userId = :userId LIMIT 1")
    suspend fun getByUserId(userId: Long): UserLevelEntity?

    @Query("UPDATE user_levels SET totalXp = :xp, currentLevel = :level, xpToNextLevel = :xpToNext WHERE userId = :userId")
    suspend fun updateLevel(userId: Long, xp: Int, level: Int, xpToNext: Int)
}
