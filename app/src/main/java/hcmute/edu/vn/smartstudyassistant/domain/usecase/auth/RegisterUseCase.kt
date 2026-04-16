package hcmute.edu.vn.smartstudyassistant.domain.usecase.auth

import javax.inject.Inject

import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.UserEntity
import hcmute.edu.vn.smartstudyassistant.domain.repository.UserRepository
import hcmute.edu.vn.smartstudyassistant.util.HashUtil

class RegisterUseCase @Inject constructor(private val userRepository: UserRepository) {

    suspend operator fun invoke(
        username: String,
        email: String,
        password: String,
        displayName: String
    ): Result<Long> {
        // Validation
        if (username.isBlank() || email.isBlank() || password.isBlank()) {
            return Result.failure(IllegalArgumentException("All fields are required"))
        }
        if (!email.contains("@")) {
            return Result.failure(IllegalArgumentException("Invalid email format"))
        }
        if (password.length < 6) {
            return Result.failure(IllegalArgumentException("Password must be at least 6 characters"))
        }

        // Duplicate checks
        if (userRepository.getByEmail(email.trim().lowercase()) != null) {
            return Result.failure(IllegalStateException("Email already registered"))
        }
        if (userRepository.getByUsername(username.trim()) != null) {
            return Result.failure(IllegalStateException("Username already taken"))
        }

        // Hash password
        val salt = HashUtil.generateSalt()
        val hash = HashUtil.hashPassword(password, salt)
        val now = System.currentTimeMillis()

        val user = UserEntity(
            username = username.trim(),
            email = email.trim().lowercase(),
            passwordHash = hash,
            salt = salt,
            displayName = displayName.trim().ifBlank { username.trim() },
            createdAt = now,
            lastLoginAt = now
        )

        return try {
            Result.success(userRepository.insert(user))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
