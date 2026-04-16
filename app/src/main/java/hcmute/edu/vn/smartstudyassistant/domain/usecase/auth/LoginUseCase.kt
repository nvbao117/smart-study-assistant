package hcmute.edu.vn.smartstudyassistant.domain.usecase.auth

import javax.inject.Inject

import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.UserEntity
import hcmute.edu.vn.smartstudyassistant.data.local.preferences.UserPreferences
import hcmute.edu.vn.smartstudyassistant.domain.repository.UserRepository
import hcmute.edu.vn.smartstudyassistant.util.HashUtil

class LoginUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val userPreferences: UserPreferences
) {

    suspend operator fun invoke(email: String, password: String): Result<UserEntity> {
        if (email.isBlank() || password.isBlank()) {
            return Result.failure(IllegalArgumentException("Email and password are required"))
        }

        val user = userRepository.getByEmail(email.trim().lowercase())
            ?: return Result.failure(IllegalStateException("User not found"))

        if (!HashUtil.verifyPassword(password, user.salt, user.passwordHash)) {
            return Result.failure(IllegalStateException("Invalid password"))
        }

        val now = System.currentTimeMillis()
        userRepository.updateLastLogin(user.id, now)
        userPreferences.currentUserId = user.id

        return Result.success(user.copy(lastLoginAt = now))
    }
}
