package hcmute.edu.vn.smartstudyassistant.data.repository

import javax.inject.Inject

import hcmute.edu.vn.smartstudyassistant.data.local.dao.DeckDao
import hcmute.edu.vn.smartstudyassistant.data.local.dao.DeckWithCardCount
import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.DeckEntity
import hcmute.edu.vn.smartstudyassistant.domain.repository.DeckRepository
import kotlinx.coroutines.flow.Flow

class DeckRepositoryImpl @Inject constructor(private val deckDao: DeckDao) : DeckRepository {
    override suspend fun createDeck(deck: DeckEntity): Long = deckDao.insert(deck)
    override suspend fun updateDeck(deck: DeckEntity) = deckDao.update(deck)
    override suspend fun deleteDeck(deck: DeckEntity) = deckDao.delete(deck)
    override suspend fun getDeckById(id: Long): DeckEntity? = deckDao.getById(id)
    override fun getDecksByUserId(userId: Long): Flow<List<DeckEntity>> = deckDao.getByUserId(userId)
    override fun getDecksWithCardCount(userId: Long, today: Long): Flow<List<DeckWithCardCount>> =
        deckDao.getWithCardCount(userId, today)
    override suspend fun updateLastStudied(deckId: Long, timestamp: Long) =
        deckDao.updateLastStudied(deckId, timestamp)
}
