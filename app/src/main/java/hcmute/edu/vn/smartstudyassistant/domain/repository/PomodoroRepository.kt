package hcmute.edu.vn.smartstudyassistant.domain.repository

import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.PomodoroSessionEntity
import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.PomodoroSettingsEntity

interface PomodoroRepository {
    suspend fun saveSession(session: PomodoroSessionEntity): Long
    suspend fun getSettings(userId: Long): PomodoroSettingsEntity?
    suspend fun saveSettings(settings: PomodoroSettingsEntity)
    suspend fun getSessionsByDateRange(userId: Long, startMillis: Long, endMillis: Long): List<PomodoroSessionEntity>
    suspend fun getFocusMinutesByDate(userId: Long, startMillis: Long, endMillis: Long): Int
    suspend fun getMostProductiveHour(userId: Long): Int?
    suspend fun getSessionCount(userId: Long, type: String): Int
    suspend fun getAverageFocusDuration(userId: Long): Double
    suspend fun getTotalFocusSessions(userId: Long): Int
}
