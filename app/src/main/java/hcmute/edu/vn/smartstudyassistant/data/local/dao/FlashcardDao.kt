package hcmute.edu.vn.smartstudyassistant.data.local.dao

import androidx.room.*
import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.FlashcardEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FlashcardDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(card: FlashcardEntity): Long

    @Update
    suspend fun update(card: FlashcardEntity)

    @Delete
    suspend fun delete(card: FlashcardEntity)

    @Query("SELECT * FROM flashcards WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): FlashcardEntity?

    @Query("SELECT * FROM flashcards WHERE deckId = :deckId ORDER BY nextReviewDate ASC")
    fun getByDeckId(deckId: Long): Flow<List<FlashcardEntity>>

    @Query("SELECT * FROM flashcards WHERE deckId = :deckId AND nextReviewDate <= :today ORDER BY nextReviewDate ASC")
    fun getDueCards(deckId: Long, today: Long): Flow<List<FlashcardEntity>>

    @Query("SELECT * FROM flashcards WHERE deckId = :deckId AND nextReviewDate <= :today")
    suspend fun getDueCardsList(deckId: Long, today: Long): List<FlashcardEntity>

    @Query("""
        SELECT f.* FROM flashcards f
        INNER JOIN card_tags ct ON ct.cardId = f.id
        WHERE ct.tagName = :tag
        AND f.deckId IN (SELECT id FROM decks WHERE userId = :userId)
    """)
    fun getByTag(userId: Long, tag: String): Flow<List<FlashcardEntity>>

    @Query("""
        SELECT * FROM flashcards
        WHERE deckId IN (SELECT id FROM decks WHERE userId = :userId)
        AND (front LIKE '%' || :query || '%' OR back LIKE '%' || :query || '%')
    """)
    fun search(userId: Long, query: String): Flow<List<FlashcardEntity>>

    @Query("SELECT COUNT(*) FROM flashcards WHERE deckId = :deckId")
    suspend fun getCountByDeck(deckId: Long): Int

    @Query("SELECT COUNT(*) FROM flashcards WHERE deckId = :deckId AND nextReviewDate <= :today")
    suspend fun getDueCountByDeck(deckId: Long, today: Long): Int

    @Query("SELECT AVG(easeFactor) FROM flashcards WHERE deckId = :deckId")
    suspend fun getAverageEaseFactor(deckId: Long): Double?

    @Query("SELECT COUNT(*) FROM flashcards WHERE deckId IN (SELECT id FROM decks WHERE userId = :userId)")
    suspend fun getTotalCardCount(userId: Long): Int
}
