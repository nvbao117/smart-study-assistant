package hcmute.edu.vn.smartstudyassistant.domain.usecase.task

import com.google.common.truth.Truth.assertThat
import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.TaskStatus
import hcmute.edu.vn.smartstudyassistant.domain.repository.TaskRepository
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class CompleteTaskUseCaseTest {

    private val taskRepository: TaskRepository = mock()
    private lateinit var useCase: CompleteTaskUseCase

    @Before
    fun setup() {
        useCase = CompleteTaskUseCase(taskRepository)
    }

    @Test
    fun `invoke updates status to DONE`() = runTest {
        val result = useCase(taskId = 42L)
        assertThat(result.isSuccess).isTrue()
        verify(taskRepository).updateTaskStatus(
            id = 42L,
            status = TaskStatus.DONE,
            completedAt = org.mockito.kotlin.any()
        )
    }

    @Test
    fun `invoke completes all subtasks`() = runTest {
        useCase(taskId = 42L)
        verify(taskRepository).completeAllSubtasks(42L)
    }
}
