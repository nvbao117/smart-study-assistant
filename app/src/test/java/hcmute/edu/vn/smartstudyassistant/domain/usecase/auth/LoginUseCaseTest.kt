package hcmute.edu.vn.smartstudyassistant.domain.usecase.auth

import com.google.common.truth.Truth.assertThat
import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.UserEntity
import hcmute.edu.vn.smartstudyassistant.data.local.preferences.UserPreferences
import hcmute.edu.vn.smartstudyassistant.domain.repository.UserRepository
import hcmute.edu.vn.smartstudyassistant.util.HashUtil
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class LoginUseCaseTest {

    private val userRepository: UserRepository = mock()
    private val userPreferences: UserPreferences = mock()
    private lateinit var useCase: LoginUseCase

    @Before
    fun setup() {
        useCase = LoginUseCase(userRepository, userPreferences)
    }

    private fun buildUser(password: String): UserEntity {
        val salt = HashUtil.generateSalt()
        val hash = HashUtil.hashPassword(password, salt)
        return UserEntity(1L, "testuser", "test@example.com", hash, salt, "Test User", 0L, 0L)
    }

    @Test
    fun `happy path - correct credentials return user`() = runTest {
        val password = "correctPassword"
        val user = buildUser(password)
        whenever(userRepository.getByEmail("test@example.com")).thenReturn(user)

        val result = useCase("test@example.com", password)

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrNull()?.username).isEqualTo("testuser")
        verify(userPreferences).currentUserId = 1L
    }

    @Test
    fun `blank email returns failure`() = runTest {
        val result = useCase("", "password")
        assertThat(result.isFailure).isTrue()
    }

    @Test
    fun `user not found returns failure`() = runTest {
        whenever(userRepository.getByEmail(any())).thenReturn(null)
        val result = useCase("notfound@example.com", "password")
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()?.message).contains("not found")
    }

    @Test
    fun `wrong password returns failure`() = runTest {
        val user = buildUser("correctPassword")
        whenever(userRepository.getByEmail(any())).thenReturn(user)

        val result = useCase("test@example.com", "wrongPassword")

        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()?.message).contains("Invalid")
    }
}
