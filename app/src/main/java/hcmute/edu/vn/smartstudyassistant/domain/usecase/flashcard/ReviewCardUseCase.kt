package hcmute.edu.vn.smartstudyassistant.domain.usecase.flashcard

import javax.inject.Inject

import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.ReviewLogEntity
import hcmute.edu.vn.smartstudyassistant.domain.repository.DeckRepository
import hcmute.edu.vn.smartstudyassistant.domain.repository.FlashcardRepository

class ReviewCardUseCase @Inject constructor(
    private val flashcardRepository: FlashcardRepository,
    private val deckRepository: DeckRepository
) {
    suspend operator fun invoke(cardId: Long, quality: Int): Result<Unit> {
        if (quality !in 1..5) return Result.failure(IllegalArgumentException("Quality must be 1-5"))

        val card = flashcardRepository.getCardById(cardId)
            ?: return Result.failure(IllegalStateException("Card not found"))

        val result = SM2Algorithm.calculate(
            quality = quality,
            repetitionCount = card.repetitionCount,
            intervalDays = card.intervalDays,
            easeFactor = card.easeFactor
        )

        val updatedCard = card.copy(
            repetitionCount = result.newRepCount,
            intervalDays = result.newIntervalDays,
            easeFactor = result.newEaseFactor,
            nextReviewDate = result.nextReviewDate
        )

        val log = ReviewLogEntity(
            cardId = cardId,
            quality = quality,
            previousInterval = card.intervalDays,
            newInterval = result.newIntervalDays
        )

        return try {
            flashcardRepository.updateCard(updatedCard)
            flashcardRepository.insertReviewLog(log)
            deckRepository.updateLastStudied(card.deckId, System.currentTimeMillis())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
