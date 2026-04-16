package hcmute.edu.vn.smartstudyassistant.domain.usecase.flashcard

import javax.inject.Inject

import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.FlashcardEntity
import hcmute.edu.vn.smartstudyassistant.domain.repository.FlashcardRepository

class UpdateCardUseCase @Inject constructor(private val flashcardRepository: FlashcardRepository) {
    suspend operator fun invoke(card: FlashcardEntity): Result<Unit> {
        if (card.front.isBlank() || card.back.isBlank())
            return Result.failure(IllegalArgumentException("Front and back cannot be empty"))
        return try { flashcardRepository.updateCard(card); Result.success(Unit) } catch (e: Exception) { Result.failure(e) }
    }
}
