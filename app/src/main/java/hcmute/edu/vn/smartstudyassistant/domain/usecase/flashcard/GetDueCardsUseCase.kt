package hcmute.edu.vn.smartstudyassistant.domain.usecase.flashcard

import javax.inject.Inject

import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.FlashcardEntity
import hcmute.edu.vn.smartstudyassistant.domain.repository.FlashcardRepository
import hcmute.edu.vn.smartstudyassistant.util.todayMillis
import kotlinx.coroutines.flow.Flow

class GetDueCardsUseCase @Inject constructor(private val flashcardRepository: FlashcardRepository) {
    operator fun invoke(deckId: Long): Flow<List<FlashcardEntity>> =
        flashcardRepository.getDueCards(deckId, todayMillis())
}
