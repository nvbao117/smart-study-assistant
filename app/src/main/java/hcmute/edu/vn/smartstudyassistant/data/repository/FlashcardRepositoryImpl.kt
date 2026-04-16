package hcmute.edu.vn.smartstudyassistant.data.repository

import javax.inject.Inject

import hcmute.edu.vn.smartstudyassistant.data.local.dao.FlashcardDao
import hcmute.edu.vn.smartstudyassistant.data.local.dao.ReviewLogDao
import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.FlashcardEntity
import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.ReviewLogEntity
import hcmute.edu.vn.smartstudyassistant.domain.model.DeckStats
import hcmute.edu.vn.smartstudyassistant.domain.repository.FlashcardRepository
import kotlinx.coroutines.flow.Flow

class FlashcardRepositoryImpl @Inject constructor(
    private val flashcardDao: FlashcardDao,
    private val reviewLogDao: ReviewLogDao
) : FlashcardRepository {

    override suspend fun createCard(card: FlashcardEntity): Long = flashcardDao.insert(card)
    override suspend fun updateCard(card: FlashcardEntity) = flashcardDao.update(card)
    override suspend fun deleteCard(card: FlashcardEntity) = flashcardDao.delete(card)
    override suspend fun getCardById(id: Long): FlashcardEntity? = flashcardDao.getById(id)
    override fun getCardsByDeckId(deckId: Long): Flow<List<FlashcardEntity>> = flashcardDao.getByDeckId(deckId)
    override fun getDueCards(deckId: Long, today: Long): Flow<List<FlashcardEntity>> = flashcardDao.getDueCards(deckId, today)
    override suspend fun getDueCardsList(deckId: Long, today: Long): List<FlashcardEntity> = flashcardDao.getDueCardsList(deckId, today)
    override suspend fun insertReviewLog(log: ReviewLogEntity) { reviewLogDao.insert(log) }
    override suspend fun getReviewCountByDateRange(userId: Long, startMillis: Long, endMillis: Long): Int =
        reviewLogDao.getReviewCountByDateRange(userId, startMillis, endMillis)
    override suspend fun getTotalReviewCount(userId: Long): Int = reviewLogDao.getTotalReviewCount(userId)
    override suspend fun getTotalCardCount(userId: Long): Int = flashcardDao.getTotalCardCount(userId)

    override suspend fun getDeckStats(deckId: Long, today: Long): DeckStats {
        val deck = flashcardDao.getById(deckId)
        return DeckStats(
            deckId = deckId,
            deckName = "",
            totalCards = flashcardDao.getCountByDeck(deckId),
            dueToday = flashcardDao.getDueCountByDeck(deckId, today),
            averageEaseFactor = flashcardDao.getAverageEaseFactor(deckId) ?: 2.5,
            averageQuality = reviewLogDao.getAverageQualityByDeck(deckId)
        )
    }
}
