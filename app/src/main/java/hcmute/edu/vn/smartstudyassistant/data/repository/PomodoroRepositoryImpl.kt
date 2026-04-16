package hcmute.edu.vn.smartstudyassistant.data.repository

import javax.inject.Inject

import hcmute.edu.vn.smartstudyassistant.data.local.dao.PomodoroSessionDao
import hcmute.edu.vn.smartstudyassistant.data.local.dao.PomodoroSettingsDao
import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.PomodoroSessionEntity
import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.PomodoroSettingsEntity
import hcmute.edu.vn.smartstudyassistant.domain.repository.PomodoroRepository

class PomodoroRepositoryImpl @Inject constructor(
    private val sessionDao: PomodoroSessionDao,
    private val settingsDao: PomodoroSettingsDao
) : PomodoroRepository {
    override suspend fun saveSession(session: PomodoroSessionEntity): Long = sessionDao.insert(session)
    override suspend fun getSettings(userId: Long): PomodoroSettingsEntity? = settingsDao.getByUserId(userId)
    override suspend fun saveSettings(settings: PomodoroSettingsEntity) = settingsDao.insertOrUpdate(settings)
    override suspend fun getSessionsByDateRange(userId: Long, startMillis: Long, endMillis: Long) = sessionDao.getByDateRange(userId, startMillis, endMillis)
    override suspend fun getFocusMinutesByDate(userId: Long, startMillis: Long, endMillis: Long) = sessionDao.getFocusMinutesByDate(userId, startMillis, endMillis)
    override suspend fun getMostProductiveHour(userId: Long): Int? = sessionDao.getMostProductiveHour(userId)
    override suspend fun getSessionCount(userId: Long, type: String): Int = sessionDao.getSessionCount(userId, type)
    override suspend fun getAverageFocusDuration(userId: Long): Double = sessionDao.getAverageFocusDuration(userId)
    override suspend fun getTotalFocusSessions(userId: Long): Int = sessionDao.getTotalFocusSessions(userId)
}
