package hcmute.edu.vn.smartstudyassistant.data.local.dao

import androidx.room.*
import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.ReviewLogEntity

@Dao
interface ReviewLogDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(log: ReviewLogEntity): Long

    @Query("SELECT * FROM review_logs WHERE cardId = :cardId ORDER BY reviewedAt DESC")
    suspend fun getByCardId(cardId: Long): List<ReviewLogEntity>

    @Query("""
        SELECT COUNT(*) FROM review_logs
        WHERE cardId IN (
            SELECT id FROM flashcards
            WHERE deckId IN (SELECT id FROM decks WHERE userId = :userId)
        )
        AND reviewedAt >= :startMillis AND reviewedAt < :endMillis
    """)
    suspend fun getReviewCountByDateRange(userId: Long, startMillis: Long, endMillis: Long): Int

    @Query("""
        SELECT AVG(quality) FROM review_logs
        WHERE cardId IN (SELECT id FROM flashcards WHERE deckId = :deckId)
    """)
    suspend fun getAverageQualityByDeck(deckId: Long): Double?

    @Query("""
        SELECT COUNT(*) FROM review_logs
        WHERE cardId IN (
            SELECT id FROM flashcards
            WHERE deckId IN (SELECT id FROM decks WHERE userId = :userId)
        )
    """)
    suspend fun getTotalReviewCount(userId: Long): Int
}
