package hcmute.edu.vn.smartstudyassistant.data.local.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "analytics_daily",
    primaryKeys = ["userId", "date"],
    foreignKeys = [ForeignKey(entity = UserEntity::class, parentColumns = ["id"], childColumns = ["userId"], onDelete = ForeignKey.CASCADE)],
    indices = [Index("userId")]
)
data class AnalyticsDailyEntity(
    val userId: Long,
    val date: Long,               // startOfDay millis
    val tasksCompleted: Int = 0,
    val focusMinutes: Int = 0,
    val cardsReviewed: Int = 0,
    val xpEarned: Int = 0
)
