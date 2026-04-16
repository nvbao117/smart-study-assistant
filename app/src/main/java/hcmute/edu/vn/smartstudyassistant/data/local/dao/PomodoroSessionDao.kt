package hcmute.edu.vn.smartstudyassistant.data.local.dao

import androidx.room.*
import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.PomodoroSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PomodoroSessionDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(session: PomodoroSessionEntity): Long

    @Query("SELECT * FROM pomodoro_sessions WHERE userId = :userId ORDER BY startedAt DESC")
    fun getByUserId(userId: Long): Flow<List<PomodoroSessionEntity>>

    @Query("""
        SELECT * FROM pomodoro_sessions
        WHERE userId = :userId AND startedAt >= :startMillis AND startedAt < :endMillis
        ORDER BY startedAt DESC
    """)
    suspend fun getByDateRange(userId: Long, startMillis: Long, endMillis: Long): List<PomodoroSessionEntity>

    @Query("""
        SELECT COALESCE(SUM(durationSeconds) / 60, 0) FROM pomodoro_sessions
        WHERE userId = :userId AND type = 'FOCUS' AND status = 'COMPLETED'
        AND startedAt >= :startMillis AND startedAt < :endMillis
    """)
    suspend fun getFocusMinutesByDate(userId: Long, startMillis: Long, endMillis: Long): Int

    @Query("""
        SELECT CAST(strftime('%H', datetime(startedAt / 1000, 'unixepoch')) AS INTEGER) as hour
        FROM pomodoro_sessions
        WHERE userId = :userId AND status = 'COMPLETED' AND type = 'FOCUS'
        GROUP BY hour
        ORDER BY COUNT(*) DESC
        LIMIT 1
    """)
    suspend fun getMostProductiveHour(userId: Long): Int?

    @Query("SELECT COUNT(*) FROM pomodoro_sessions WHERE userId = :userId AND type = :type AND status = 'COMPLETED'")
    suspend fun getSessionCount(userId: Long, type: String): Int

    @Query("SELECT COALESCE(AVG(durationSeconds), 0) FROM pomodoro_sessions WHERE userId = :userId AND type = 'FOCUS' AND status = 'COMPLETED'")
    suspend fun getAverageFocusDuration(userId: Long): Double

    @Query("SELECT COUNT(*) FROM pomodoro_sessions WHERE userId = :userId AND status = 'COMPLETED' AND type = 'FOCUS'")
    suspend fun getTotalFocusSessions(userId: Long): Int
}
