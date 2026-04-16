package hcmute.edu.vn.smartstudyassistant.data.local.dao

import androidx.room.*
import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.RecurringRuleEntity

@Dao
interface RecurringRuleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(rule: RecurringRuleEntity): Long

    @Update
    suspend fun update(rule: RecurringRuleEntity)

    @Delete
    suspend fun delete(rule: RecurringRuleEntity)

    @Query("SELECT * FROM recurring_rules WHERE taskId = :taskId LIMIT 1")
    suspend fun getByTaskId(taskId: Long): RecurringRuleEntity?

    @Query("SELECT * FROM recurring_rules WHERE nextOccurrence <= :now")
    suspend fun getDueRecurring(now: Long): List<RecurringRuleEntity>

    @Query("UPDATE recurring_rules SET nextOccurrence = :next WHERE id = :id")
    suspend fun updateNextOccurrence(id: Long, next: Long)
}
