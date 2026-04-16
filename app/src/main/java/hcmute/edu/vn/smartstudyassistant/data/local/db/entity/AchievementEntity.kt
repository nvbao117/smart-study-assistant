package hcmute.edu.vn.smartstudyassistant.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "achievements")
data class AchievementEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String,
    val xpReward: Int,
    val requirementType: String,  // TASKS_COMPLETED, POMODORO_SESSIONS, etc.
    val requirementValue: Int
)
