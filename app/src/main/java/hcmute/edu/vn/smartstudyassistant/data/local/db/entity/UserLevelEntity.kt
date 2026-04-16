package hcmute.edu.vn.smartstudyassistant.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_levels")
data class UserLevelEntity(
    @PrimaryKey val userId: Long,
    val totalXp: Int = 0,
    val currentLevel: Int = 1,
    val xpToNextLevel: Int = 100
)
