package hcmute.edu.vn.smartstudyassistant.data.local.dao

import androidx.room.*
import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.AiSettingsEntity

@Dao
interface AiSettingsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(settings: AiSettingsEntity)

    @Query("SELECT * FROM ai_settings WHERE userId = :userId LIMIT 1")
    suspend fun getByUserId(userId: Long): AiSettingsEntity?
}
