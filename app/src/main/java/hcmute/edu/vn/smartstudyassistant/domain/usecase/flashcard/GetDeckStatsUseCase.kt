package hcmute.edu.vn.smartstudyassistant.domain.usecase.flashcard

import javax.inject.Inject

import hcmute.edu.vn.smartstudyassistant.domain.model.DeckStats
import hcmute.edu.vn.smartstudyassistant.domain.repository.FlashcardRepository
import hcmute.edu.vn.smartstudyassistant.util.todayMillis

class GetDeckStatsUseCase @Inject constructor(private val flashcardRepository: FlashcardRepository) {
    suspend operator fun invoke(deckId: Long): DeckStats =
        flashcardRepository.getDeckStats(deckId, todayMillis())
}
