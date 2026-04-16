package hcmute.edu.vn.smartstudyassistant.domain.usecase.flashcard

import javax.inject.Inject

import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.DeckEntity
import hcmute.edu.vn.smartstudyassistant.domain.repository.DeckRepository

class DeleteDeckUseCase @Inject constructor(private val deckRepository: DeckRepository) {
    suspend operator fun invoke(deck: DeckEntity): Result<Unit> =
        try { deckRepository.deleteDeck(deck); Result.success(Unit) } catch (e: Exception) { Result.failure(e) }
}
