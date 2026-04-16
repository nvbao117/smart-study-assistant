package hcmute.edu.vn.smartstudyassistant.util

object Constants {

    // XP values
    const val XP_TASK_COMPLETE = 10
    const val XP_TASK_HIGH_PRIORITY = 15
    const val XP_TASK_URGENT_PRIORITY = 20
    const val XP_POMODORO_COMPLETE = 15
    const val XP_FLASHCARD_REVIEW = 5
    const val XP_STREAK_MULTIPLIER = 5

    // Level formula: level = floor(sqrt(totalXp / XP_PER_LEVEL_FACTOR)) + 1
    const val XP_PER_LEVEL_FACTOR = 100.0

    // Pomodoro defaults
    const val DEFAULT_FOCUS_MINUTES = 25
    const val DEFAULT_SHORT_BREAK_MINUTES = 5
    const val DEFAULT_LONG_BREAK_MINUTES = 15
    const val DEFAULT_SESSIONS_BEFORE_LONG_BREAK = 4

    // SM-2 defaults
    const val SM2_DEFAULT_EASE_FACTOR = 2.5
    const val SM2_MIN_EASE_FACTOR = 1.3
    const val SM2_FIRST_REVIEW_INTERVAL = 1   // days
    const val SM2_SECOND_REVIEW_INTERVAL = 6  // days

    // Dashboard
    const val DASHBOARD_MAX_TODAY_TASKS = 3
    const val DASHBOARD_UPCOMING_DAYS = 7

    // Daily tip thresholds
    const val TIP_OVERDUE_THRESHOLD = 3
    const val TIP_CARDS_DUE_THRESHOLD = 20
    const val TIP_FOCUS_YESTERDAY_THRESHOLD_MINUTES = 30
    const val TIP_STREAK_THRESHOLD = 7

    // AI
    const val AI_DEFAULT_TEMPERATURE = 0.7
    const val AI_DEFAULT_MAX_TOKENS = 1024
    const val AI_CONTEXT_MESSAGE_LIMIT = 20
}
