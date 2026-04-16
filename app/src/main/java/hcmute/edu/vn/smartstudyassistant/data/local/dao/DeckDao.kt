package hcmute.edu.vn.smartstudyassistant.data.local.dao

import androidx.room.*
import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.DeckEntity
import kotlinx.coroutines.flow.Flow

data class DeckWithCardCount(
    val id: Long,
    val userId: Long,
    val subjectId: Long?,
    val name: String,
    val description: String,
    val lastStudiedAt: Long?,
    val createdAt: Long,
    val totalCards: Int,
    val dueCards: Int
)

@Dao
interface DeckDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(deck: DeckEntity): Long

    @Update
    suspend fun update(deck: DeckEntity)

    @Delete
    suspend fun delete(deck: DeckEntity)

    @Query("SELECT * FROM decks WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): DeckEntity?

    @Query("SELECT * FROM decks WHERE userId = :userId ORDER BY name ASC")
    fun getByUserId(userId: Long): Flow<List<DeckEntity>>

    @Query("""
        SELECT d.id, d.userId, d.subjectId, d.name, d.description, d.lastStudiedAt, d.createdAt,
               COUNT(f.id) as totalCards,
               SUM(CASE WHEN f.nextReviewDate <= :today THEN 1 ELSE 0 END) as dueCards
        FROM decks d
        LEFT JOIN flashcards f ON f.deckId = d.id
        WHERE d.userId = :userId
        GROUP BY d.id
        ORDER BY d.name ASC
    """)
    fun getWithCardCount(userId: Long, today: Long): Flow<List<DeckWithCardCount>>

    @Query("UPDATE decks SET lastStudiedAt = :timestamp WHERE id = :id")
    suspend fun updateLastStudied(id: Long, timestamp: Long)

    @Query("SELECT COUNT(*) FROM decks WHERE userId = :userId")
    suspend fun getDeckCount(userId: Long): Int
}
