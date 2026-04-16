package hcmute.edu.vn.smartstudyassistant.domain.usecase.flashcard

import javax.inject.Inject

import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.FlashcardEntity
import hcmute.edu.vn.smartstudyassistant.domain.repository.FlashcardRepository

class DeleteCardUseCase @Inject constructor(private val flashcardRepository: FlashcardRepository) {
    suspend operator fun invoke(card: FlashcardEntity): Result<Unit> =
        try { flashcardRepository.deleteCard(card); Result.success(Unit) } catch (e: Exception) { Result.failure(e) }
}
