package hcmute.edu.vn.smartstudyassistant.domain.usecase.flashcard

import com.google.common.truth.Truth.assertThat
import hcmute.edu.vn.smartstudyassistant.util.Constants
import org.junit.Test

class SM2AlgorithmTest {

    // ── Quality 1 & 2 — Reset ────────────────────────────────────────────────

    @Test
    fun `quality 1 resets repetition count to 0`() {
        val result = SM2Algorithm.calculate(1, repetitionCount = 3, intervalDays = 10, easeFactor = 2.5)
        assertThat(result.newRepCount).isEqualTo(0)
    }

    @Test
    fun `quality 1 resets interval to 1 day`() {
        val result = SM2Algorithm.calculate(1, repetitionCount = 3, intervalDays = 10, easeFactor = 2.5)
        assertThat(result.newIntervalDays).isEqualTo(1)
    }

    @Test
    fun `quality 2 resets repetition count to 0`() {
        val result = SM2Algorithm.calculate(2, repetitionCount = 5, intervalDays = 20, easeFactor = 2.5)
        assertThat(result.newRepCount).isEqualTo(0)
        assertThat(result.newIntervalDays).isEqualTo(1)
    }

    // ── Quality 3 — Good ─────────────────────────────────────────────────────

    @Test
    fun `quality 3 increments rep count`() {
        val result = SM2Algorithm.calculate(3, repetitionCount = 0, intervalDays = 1, easeFactor = 2.5)
        assertThat(result.newRepCount).isEqualTo(1)
    }

    @Test
    fun `quality 3 first review gives interval 1`() {
        val result = SM2Algorithm.calculate(3, repetitionCount = 0, intervalDays = 1, easeFactor = 2.5)
        assertThat(result.newIntervalDays).isEqualTo(Constants.SM2_FIRST_REVIEW_INTERVAL)
    }

    @Test
    fun `quality 3 second review gives interval 6`() {
        val r1 = SM2Algorithm.calculate(3, 0, 1, 2.5)
        val r2 = SM2Algorithm.calculate(3, r1.newRepCount, r1.newIntervalDays, r1.newEaseFactor)
        assertThat(r2.newIntervalDays).isEqualTo(Constants.SM2_SECOND_REVIEW_INTERVAL)
    }

    // ── Quality 4 & 5 — Easy/Perfect ─────────────────────────────────────────

    @Test
    fun `quality 4 applies EF multiplier 1_3 for subsequent reviews`() {
        val r1 = SM2Algorithm.calculate(4, 0, 1, 2.5)
        val r2 = SM2Algorithm.calculate(4, r1.newRepCount, r1.newIntervalDays, r1.newEaseFactor)
        val r3 = SM2Algorithm.calculate(4, r2.newRepCount, r2.newIntervalDays, r2.newEaseFactor)
        // After 3rd review, interval should be > 6
        assertThat(r3.newIntervalDays).isGreaterThan(6)
    }

    @Test
    fun `quality 5 produces longer interval than quality 3`() {
        val r3 = SM2Algorithm.calculate(3, 2, 6, 2.5)
        val r5 = SM2Algorithm.calculate(5, 2, 6, 2.5)
        assertThat(r5.newIntervalDays).isAtLeast(r3.newIntervalDays)
    }

    // ── Ease Factor ───────────────────────────────────────────────────────────

    @Test
    fun `ease factor never drops below minimum 1_3`() {
        var ef = 2.5
        var rep = 0
        var interval = 1
        repeat(20) {
            val result = SM2Algorithm.calculate(1, rep, interval, ef)
            ef = result.newEaseFactor
            rep = result.newRepCount
            interval = result.newIntervalDays
        }
        assertThat(ef).isAtLeast(Constants.SM2_MIN_EASE_FACTOR)
    }

    @Test
    fun `quality 5 increases ease factor`() {
        val initialEf = 2.5
        val result = SM2Algorithm.calculate(5, 0, 1, initialEf)
        assertThat(result.newEaseFactor).isGreaterThan(initialEf)
    }

    @Test
    fun `quality 1 decreases ease factor`() {
        val initialEf = 2.5
        val result = SM2Algorithm.calculate(1, 3, 10, initialEf)
        assertThat(result.newEaseFactor).isLessThan(initialEf)
    }

    // ── nextReviewDate ────────────────────────────────────────────────────────

    @Test
    fun `nextReviewDate is in the future`() {
        val result = SM2Algorithm.calculate(3, 0, 1, 2.5)
        assertThat(result.nextReviewDate).isGreaterThan(System.currentTimeMillis())
    }
}
