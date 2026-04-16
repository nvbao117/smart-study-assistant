package hcmute.edu.vn.smartstudyassistant.domain.usecase.flashcard

import javax.inject.Inject

import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.DeckEntity
import hcmute.edu.vn.smartstudyassistant.domain.repository.DeckRepository

class CreateDeckUseCase @Inject constructor(private val deckRepository: DeckRepository) {
    suspend operator fun invoke(deck: DeckEntity): Result<Long> {
        if (deck.name.isBlank()) return Result.failure(IllegalArgumentException("Deck name cannot be empty"))
        return try { Result.success(deckRepository.createDeck(deck)) } catch (e: Exception) { Result.failure(e) }
    }
}
