package hcmute.edu.vn.smartstudyassistant.domain.repository

import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.FlashcardEntity
import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.ReviewLogEntity
import hcmute.edu.vn.smartstudyassistant.domain.model.DeckStats
import kotlinx.coroutines.flow.Flow

interface FlashcardRepository {
    suspend fun createCard(card: FlashcardEntity): Long
    suspend fun updateCard(card: FlashcardEntity)
    suspend fun deleteCard(card: FlashcardEntity)
    suspend fun getCardById(id: Long): FlashcardEntity?
    fun getCardsByDeckId(deckId: Long): Flow<List<FlashcardEntity>>
    fun getDueCards(deckId: Long, today: Long): Flow<List<FlashcardEntity>>
    suspend fun getDueCardsList(deckId: Long, today: Long): List<FlashcardEntity>
    suspend fun insertReviewLog(log: ReviewLogEntity)
    suspend fun getReviewCountByDateRange(userId: Long, startMillis: Long, endMillis: Long): Int
    suspend fun getTotalReviewCount(userId: Long): Int
    suspend fun getDeckStats(deckId: Long, today: Long): DeckStats
    suspend fun getTotalCardCount(userId: Long): Int
}
