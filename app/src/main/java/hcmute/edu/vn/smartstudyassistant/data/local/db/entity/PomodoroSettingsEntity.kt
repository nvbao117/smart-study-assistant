package hcmute.edu.vn.smartstudyassistant.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import hcmute.edu.vn.smartstudyassistant.util.Constants

@Entity(tableName = "pomodoro_settings")
data class PomodoroSettingsEntity(
    @PrimaryKey
    val userId: Long,
    val focusMinutes: Int = Constants.DEFAULT_FOCUS_MINUTES,
    val shortBreakMinutes: Int = Constants.DEFAULT_SHORT_BREAK_MINUTES,
    val longBreakMinutes: Int = Constants.DEFAULT_LONG_BREAK_MINUTES,
    val sessionsBeforeLongBreak: Int = Constants.DEFAULT_SESSIONS_BEFORE_LONG_BREAK
)
