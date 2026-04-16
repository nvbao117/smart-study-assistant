package hcmute.edu.vn.smartstudyassistant.data.local.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "achievement_progress",
    primaryKeys = ["userId", "achievementId"],
    foreignKeys = [
        ForeignKey(entity = UserEntity::class, parentColumns = ["id"], childColumns = ["userId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = AchievementEntity::class, parentColumns = ["id"], childColumns = ["achievementId"], onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index("userId"), Index("achievementId")]
)
data class AchievementProgressEntity(
    val userId: Long,
    val achievementId: Long,
    val currentValue: Int = 0,
    val isUnlocked: Boolean = false,
    val unlockedAt: Long? = null
)
