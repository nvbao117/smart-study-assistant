package hcmute.edu.vn.smartstudyassistant.data.local.dao

import androidx.room.*
import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.SubjectEntity
import kotlinx.coroutines.flow.Flow

data class SubjectWithTaskCount(
    val id: Long,
    val userId: Long,
    val name: String,
    val color: String,
    val icon: String,
    val description: String,
    val createdAt: Long,
    val taskCount: Int
)

@Dao
interface SubjectDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(subject: SubjectEntity): Long

    @Update
    suspend fun update(subject: SubjectEntity)

    @Delete
    suspend fun delete(subject: SubjectEntity)

    @Query("SELECT * FROM subjects WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): SubjectEntity?

    @Query("SELECT * FROM subjects WHERE userId = :userId ORDER BY name ASC")
    fun getByUserId(userId: Long): Flow<List<SubjectEntity>>

    @Query("""
        SELECT s.id, s.userId, s.name, s.color, s.icon, s.description, s.createdAt,
               COUNT(t.id) as taskCount
        FROM subjects s
        LEFT JOIN tasks t ON t.subjectId = s.id AND t.status != 'DONE'
        WHERE s.userId = :userId
        GROUP BY s.id
        ORDER BY s.name ASC
    """)
    fun getWithTaskCount(userId: Long): Flow<List<SubjectWithTaskCount>>
}
