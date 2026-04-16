package hcmute.edu.vn.smartstudyassistant.domain.model

import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.AnalyticsDailyEntity

data class Analytics(
    val period: String,   // "daily", "weekly", "monthly"
    val data: List<AnalyticsDailyEntity>,
    val totalFocusMinutes: Int,
    val totalTasksCompleted: Int,
    val totalCardsReviewed: Int,
    val totalXpEarned: Int
)
