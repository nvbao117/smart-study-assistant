package hcmute.edu.vn.smartstudyassistant.data.local.dao

import androidx.room.*
import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.PomodoroSettingsEntity

@Dao
interface PomodoroSettingsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(settings: PomodoroSettingsEntity)

    @Query("SELECT * FROM pomodoro_settings WHERE userId = :userId LIMIT 1")
    suspend fun getByUserId(userId: Long): PomodoroSettingsEntity?
}
