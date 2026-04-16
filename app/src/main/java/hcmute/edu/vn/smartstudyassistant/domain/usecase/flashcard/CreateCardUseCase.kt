package hcmute.edu.vn.smartstudyassistant.domain.usecase.flashcard

import javax.inject.Inject

import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.FlashcardEntity
import hcmute.edu.vn.smartstudyassistant.domain.repository.FlashcardRepository

class CreateCardUseCase @Inject constructor(private val flashcardRepository: FlashcardRepository) {
    suspend operator fun invoke(card: FlashcardEntity): Result<Long> {
        if (card.front.isBlank() || card.back.isBlank())
            return Result.failure(IllegalArgumentException("Front and back cannot be empty"))
        return try { Result.success(flashcardRepository.createCard(card)) } catch (e: Exception) { Result.failure(e) }
    }
}
