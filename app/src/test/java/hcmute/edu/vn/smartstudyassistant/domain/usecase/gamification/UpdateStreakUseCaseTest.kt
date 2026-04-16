package hcmute.edu.vn.smartstudyassistant.domain.usecase.gamification

import com.google.common.truth.Truth.assertThat
import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.StreakHistoryEntity
import hcmute.edu.vn.smartstudyassistant.domain.model.LevelInfo
import hcmute.edu.vn.smartstudyassistant.domain.repository.GamificationRepository
import hcmute.edu.vn.smartstudyassistant.util.todayMillis
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class UpdateStreakUseCaseTest {

    private val gamificationRepository: GamificationRepository = mock()
    private val awardXpUseCase: AwardXpUseCase = mock()
    private lateinit var useCase: UpdateStreakUseCase

    @Before
    fun setup() {
        useCase = UpdateStreakUseCase(gamificationRepository, awardXpUseCase)
    }

    @Test
    fun `streak increments when active yesterday`() = runTest {
        val today = todayMillis()
        val yesterday = today - 24 * 60 * 60 * 1000L
        val todayEntry = StreakHistoryEntity(userId = 1L, date = today, hasActivity = false, currentStreak = 0, longestStreak = 5)
        val latestEntry = StreakHistoryEntity(userId = 1L, date = yesterday, hasActivity = true, currentStreak = 5, longestStreak = 5)

        whenever(gamificationRepository.getOrCreateTodayStreak(1L)).thenReturn(todayEntry)
        whenever(gamificationRepository.getLatestStreak(1L)).thenReturn(latestEntry)
        whenever(awardXpUseCase.invoke(any(), any(), any(), any())).thenReturn(LevelInfo(1, 50, 50, false))

        useCase(userId = 1L)

        verify(gamificationRepository).updateStreak(
            org.mockito.kotlin.argThat { currentStreak == 6 && hasActivity && longestStreak == 6 }
        )
    }

    @Test
    fun `streak resets to 1 when no activity yesterday`() = runTest {
        val today = todayMillis()
        val twoDaysAgo = today - 48 * 60 * 60 * 1000L
        val todayEntry = StreakHistoryEntity(userId = 1L, date = today, hasActivity = false)
        val latestEntry = StreakHistoryEntity(userId = 1L, date = twoDaysAgo, hasActivity = true, currentStreak = 4, longestStreak = 10)

        whenever(gamificationRepository.getOrCreateTodayStreak(1L)).thenReturn(todayEntry)
        whenever(gamificationRepository.getLatestStreak(1L)).thenReturn(latestEntry)
        whenever(awardXpUseCase.invoke(any(), any(), any(), any())).thenReturn(LevelInfo(1, 5, 95, false))

        useCase(userId = 1L)

        verify(gamificationRepository).updateStreak(
            org.mockito.kotlin.argThat { currentStreak == 1 && longestStreak == 10 }
        )
    }

    @Test
    fun `skips processing if already active today`() = runTest {
        val today = todayMillis()
        val todayEntry = StreakHistoryEntity(userId = 1L, date = today, hasActivity = true, currentStreak = 3)
        whenever(gamificationRepository.getOrCreateTodayStreak(1L)).thenReturn(todayEntry)

        useCase(userId = 1L)

        verify(gamificationRepository, org.mockito.kotlin.never()).updateStreak(any())
    }
}
