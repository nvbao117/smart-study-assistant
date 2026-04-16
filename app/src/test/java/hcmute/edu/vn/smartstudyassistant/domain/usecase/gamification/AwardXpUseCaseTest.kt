package hcmute.edu.vn.smartstudyassistant.domain.usecase.gamification

import com.google.common.truth.Truth.assertThat
import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.UserLevelEntity
import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.XpActionType
import hcmute.edu.vn.smartstudyassistant.domain.model.LevelInfo
import hcmute.edu.vn.smartstudyassistant.domain.repository.GamificationRepository
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class AwardXpUseCaseTest {

    private val gamificationRepository: GamificationRepository = mock()
    private lateinit var useCase: AwardXpUseCase

    @Before
    fun setup() {
        useCase = AwardXpUseCase(gamificationRepository)
    }

    @Test
    fun `awarding XP returns level info`() = runTest {
        whenever(gamificationRepository.awardXp(any())).thenReturn(
            LevelInfo(currentLevel = 1, totalXp = 10, xpToNextLevel = 90, didLevelUp = false)
        )
        val result = useCase(1L, XpActionType.TASK_COMPLETE, 10)
        assertThat(result.totalXp).isEqualTo(10)
        assertThat(result.didLevelUp).isFalse()
    }

    @Test
    fun `level up detected when new level greater than previous`() = runTest {
        whenever(gamificationRepository.awardXp(any())).thenReturn(
            LevelInfo(currentLevel = 2, totalXp = 100, xpToNextLevel = 300, didLevelUp = true)
        )
        val result = useCase(1L, XpActionType.POMODORO_COMPLETE, 90)
        assertThat(result.didLevelUp).isTrue()
        assertThat(result.currentLevel).isEqualTo(2)
    }
}
