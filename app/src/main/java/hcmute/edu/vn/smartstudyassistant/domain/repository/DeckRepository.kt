package hcmute.edu.vn.smartstudyassistant.domain.repository

import hcmute.edu.vn.smartstudyassistant.data.local.dao.DeckWithCardCount
import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.DeckEntity
import kotlinx.coroutines.flow.Flow

interface DeckRepository {
    suspend fun createDeck(deck: DeckEntity): Long
    suspend fun updateDeck(deck: DeckEntity)
    suspend fun deleteDeck(deck: DeckEntity)
    suspend fun getDeckById(id: Long): DeckEntity?
    fun getDecksByUserId(userId: Long): Flow<List<DeckEntity>>
    fun getDecksWithCardCount(userId: Long, today: Long): Flow<List<DeckWithCardCount>>
    suspend fun updateLastStudied(deckId: Long, timestamp: Long)
}
