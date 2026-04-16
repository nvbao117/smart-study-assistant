package hcmute.edu.vn.smartstudyassistant.domain.model

data class PomodoroStats(
    val focusToday: Int,        // minutes
    val focusWeek: Int,         // minutes
    val totalSessions: Int,
    val avgDuration: Double,    // seconds
    val mostProductiveHour: Int?
)
