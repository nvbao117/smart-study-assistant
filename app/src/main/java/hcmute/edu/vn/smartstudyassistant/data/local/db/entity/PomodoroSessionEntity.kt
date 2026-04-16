package hcmute.edu.vn.smartstudyassistant.data.local.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "pomodoro_sessions",
    foreignKeys = [
        ForeignKey(entity = UserEntity::class, parentColumns = ["id"], childColumns = ["userId"], onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index("userId")]
)
data class PomodoroSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val startedAt: Long,
    val endedAt: Long,
    val durationSeconds: Int,
    val type: PomodoroSessionType,
    val status: PomodoroSessionStatus,
    val interruptionCount: Int = 0
)
