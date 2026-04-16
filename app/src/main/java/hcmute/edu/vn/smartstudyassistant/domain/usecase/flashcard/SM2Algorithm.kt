package hcmute.edu.vn.smartstudyassistant.domain.usecase.flashcard

import hcmute.edu.vn.smartstudyassistant.util.Constants
import kotlin.math.roundToInt

object SM2Algorithm {

    /**
     * Calculates new SM-2 parameters after a card review.
     *
     * @param quality  Review quality 1-5 (1=Again, 2=Hard, 3=Good, 4=Easy, 5=Perfect)
     * @param repetitionCount  Current repetition count
     * @param intervalDays  Current interval in days
     * @param easeFactor  Current ease factor (default 2.5, min 1.3)
     */
    fun calculate(
        quality: Int,
        repetitionCount: Int,
        intervalDays: Int,
        easeFactor: Double
    ): SM2Result {
        require(quality in 1..5) { "Quality must be between 1 and 5" }

        // Update ease factor using SM-2 formula, clamp to minimum
        val rawEf = easeFactor + (0.1 - (5 - quality) * (0.08 + (5 - quality) * 0.02))
        val newEaseFactor = maxOf(Constants.SM2_MIN_EASE_FACTOR, rawEf)

        val newRepCount: Int
        val newIntervalDays: Int

        if (quality < 3) {
            // Again (1) or Hard (2) — reset
            newRepCount = 0
            newIntervalDays = Constants.SM2_FIRST_REVIEW_INTERVAL
        } else {
            newRepCount = repetitionCount + 1
            newIntervalDays = when (newRepCount) {
                1 -> Constants.SM2_FIRST_REVIEW_INTERVAL
                2 -> Constants.SM2_SECOND_REVIEW_INTERVAL
                else -> {
                    val multiplier = if (quality >= 4) newEaseFactor * 1.3 else newEaseFactor
                    (intervalDays * multiplier).roundToInt().coerceAtLeast(1)
                }
            }
        }

        val nextReviewDate = System.currentTimeMillis() +
                newIntervalDays * 24L * 60 * 60 * 1000

        return SM2Result(
            newRepCount = newRepCount,
            newIntervalDays = newIntervalDays,
            newEaseFactor = newEaseFactor,
            nextReviewDate = nextReviewDate
        )
    }
}

data class SM2Result(
    val newRepCount: Int,
    val newIntervalDays: Int,
    val newEaseFactor: Double,
    val nextReviewDate: Long
)
