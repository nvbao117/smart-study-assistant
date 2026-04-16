package hcmute.edu.vn.smartstudyassistant.data.local.dao

import androidx.room.*
import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.CardTagEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CardTagDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(tag: CardTagEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(tags: List<CardTagEntity>)

    @Delete
    suspend fun delete(tag: CardTagEntity)

    @Query("SELECT * FROM card_tags WHERE cardId = :cardId")
    suspend fun getByCardId(cardId: Long): List<CardTagEntity>

    @Query("""
        SELECT DISTINCT tagName FROM card_tags
        WHERE cardId IN (SELECT id FROM flashcards WHERE deckId IN (SELECT id FROM decks WHERE userId = :userId))
        ORDER BY tagName ASC
    """)
    fun getAllTags(userId: Long): Flow<List<String>>

    @Query("DELETE FROM card_tags WHERE cardId = :cardId")
    suspend fun deleteByCardId(cardId: Long)
}
