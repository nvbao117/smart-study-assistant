package hcmute.edu.vn.smartstudyassistant.domain.usecase.task

import com.google.common.truth.Truth.assertThat
import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.Priority
import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.TaskEntity
import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.TaskStatus
import hcmute.edu.vn.smartstudyassistant.domain.repository.TaskRepository
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class CreateTaskUseCaseTest {

    private val taskRepository: TaskRepository = mock()
    private lateinit var useCase: CreateTaskUseCase

    @Before
    fun setup() {
        useCase = CreateTaskUseCase(taskRepository)
    }

    private fun buildTask(title: String = "Test Task") = TaskEntity(
        userId = 1L, title = title, priority = Priority.MEDIUM
    )

    @Test
    fun `happy path - valid task returns success with id`() = runTest {
        whenever(taskRepository.createTask(any(), any(), any(), any())).thenReturn(1L)
        val result = useCase(buildTask("Buy groceries"))
        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(1L)
    }

    @Test
    fun `blank title returns failure`() = runTest {
        val result = useCase(buildTask(""))
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()?.message).contains("title")
    }

    @Test
    fun `whitespace-only title returns failure`() = runTest {
        val result = useCase(buildTask("   "))
        assertThat(result.isFailure).isTrue()
    }
}
