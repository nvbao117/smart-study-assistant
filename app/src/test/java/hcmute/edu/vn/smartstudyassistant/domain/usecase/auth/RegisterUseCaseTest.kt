package hcmute.edu.vn.smartstudyassistant.domain.usecase.auth

import com.google.common.truth.Truth.assertThat
import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.UserEntity
import hcmute.edu.vn.smartstudyassistant.domain.repository.UserRepository
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class RegisterUseCaseTest {

    private val userRepository: UserRepository = mock()
    private lateinit var useCase: RegisterUseCase

    @Before
    fun setup() {
        useCase = RegisterUseCase(userRepository)
    }

    @Test
    fun `happy path - valid input returns success with id`() = runTest {
        whenever(userRepository.getByEmail(any())).thenReturn(null)
        whenever(userRepository.getByUsername(any())).thenReturn(null)
        whenever(userRepository.insert(any())).thenReturn(1L)

        val result = useCase("testuser", "test@example.com", "password123", "Test User")

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()).isEqualTo(1L)
    }

    @Test
    fun `blank username returns failure`() = runTest {
        val result = useCase("", "test@example.com", "password123", "Test")
        assertThat(result.isFailure).isTrue()
    }

    @Test
    fun `blank password returns failure`() = runTest {
        val result = useCase("user", "test@example.com", "", "Test")
        assertThat(result.isFailure).isTrue()
    }

    @Test
    fun `invalid email returns failure`() = runTest {
        val result = useCase("user", "notanemail", "password123", "Test")
        assertThat(result.isFailure).isTrue()
    }

    @Test
    fun `password shorter than 6 chars returns failure`() = runTest {
        val result = useCase("user", "test@example.com", "123", "Test")
        assertThat(result.isFailure).isTrue()
    }

    @Test
    fun `duplicate email returns failure`() = runTest {
        whenever(userRepository.getByEmail(any())).thenReturn(
            UserEntity(1L, "existinguser", "test@example.com", "hash", "salt", "Existing", 0L, 0L)
        )
        val result = useCase("newuser", "test@example.com", "password123", "New User")
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()?.message).contains("email")
    }

    @Test
    fun `duplicate username returns failure`() = runTest {
        whenever(userRepository.getByEmail(any())).thenReturn(null)
        whenever(userRepository.getByUsername(any())).thenReturn(
            UserEntity(1L, "testuser", "other@example.com", "hash", "salt", "Other", 0L, 0L)
        )
        val result = useCase("testuser", "new@example.com", "password123", "New User")
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()?.message).contains("Username")
    }
}
