package hcmute.edu.vn.smartstudyassistant.domain.model

import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.TaskEntity

data class DashboardData(
    val userName: String,
    val todayTasks: List<TaskEntity>,        // top 3 by priority
    val currentStreak: Int,
    val upcomingDeadlines: List<TaskEntity>, // next 7 days
    val subjectProgress: List<SubjectProgress>,
    val focusMinutesToday: Int,
    val cardsDueToday: Int,
    val dailyTip: String?
)

data class SubjectProgress(
    val subjectId: Long,
    val subjectName: String,
    val totalTasks: Int,
    val completedTasks: Int
)
