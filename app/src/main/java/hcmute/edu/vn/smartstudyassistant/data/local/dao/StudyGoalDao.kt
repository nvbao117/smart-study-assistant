package hcmute.edu.vn.smartstudyassistant.data.local.dao

import androidx.room.*
import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.StudyGoalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StudyGoalDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(goal: StudyGoalEntity): Long

    @Update
    suspend fun update(goal: StudyGoalEntity)

    @Delete
    suspend fun delete(goal: StudyGoalEntity)

    @Query("SELECT * FROM study_goals WHERE userId = :userId AND isActive = 1")
    fun getActiveByUserId(userId: Long): Flow<List<StudyGoalEntity>>

    @Query("SELECT * FROM study_goals WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): StudyGoalEntity?
}
