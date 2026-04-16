package hcmute.edu.vn.smartstudyassistant.domain.usecase.auth

import javax.inject.Inject

import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.UserEntity
import hcmute.edu.vn.smartstudyassistant.data.local.preferences.UserPreferences
import hcmute.edu.vn.smartstudyassistant.domain.repository.UserRepository

class GetCurrentUserUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val userPreferences: UserPreferences
) {

    suspend operator fun invoke(): UserEntity? {
        val userId = userPreferences.currentUserId
        if (userId == -1L) return null
        return userRepository.getById(userId)
    }
}
