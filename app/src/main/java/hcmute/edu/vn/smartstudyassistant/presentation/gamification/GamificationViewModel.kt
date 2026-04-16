package hcmute.edu.vn.smartstudyassistant.presentation.gamification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.AchievementEntity
import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.UserEntity
import hcmute.edu.vn.smartstudyassistant.domain.model.Analytics
import hcmute.edu.vn.smartstudyassistant.domain.usecase.auth.GetCurrentUserUseCase
import hcmute.edu.vn.smartstudyassistant.domain.usecase.gamification.GetAnalyticsUseCase
import hcmute.edu.vn.smartstudyassistant.domain.repository.GamificationRepository
import hcmute.edu.vn.smartstudyassistant.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val user: UserEntity? = null,
    val currentXp: Int = 0,
    val level: Int = 1,
    val xpToNextLevel: Int = 1000,
    val streak: Int = 0,
    val achievements: List<AchievementEntity> = emptyList(),
    val unlockedAchievementIds: Set<Long> = emptySet()
)

@HiltViewModel
class GamificationViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val gamificationRepository: GamificationRepository,
    private val getAnalyticsUseCase: GetAnalyticsUseCase
) : ViewModel() {

    private val _profileState = MutableStateFlow<Resource<ProfileUiState>>(Resource.Loading)
    val profileState: StateFlow<Resource<ProfileUiState>> = _profileState

    private val _analyticsState = MutableStateFlow<Resource<Analytics>>(Resource.Loading)
    val analyticsState: StateFlow<Resource<Analytics>> = _analyticsState

    init {
        loadProfile()
        loadAnalytics()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _profileState.value = Resource.Loading
            val user = getCurrentUserUseCase() ?: run {
                _profileState.value = Resource.Error("Not logged in")
                return@launch
            }
            try {
                val levelInfo = gamificationRepository.getUserLevel(user.id)
                val streak = gamificationRepository.getLatestStreak(user.id)?.currentStreak ?: 0
                val achievements = gamificationRepository.getAllAchievements().first()
                val progress = gamificationRepository.getUserAchievementProgress(user.id).first()
                val unlockedIds = progress.filter { it.isUnlocked }.map { it.achievementId }.toSet()

                _profileState.value = Resource.Success(
                    ProfileUiState(
                        user = user,
                        currentXp = levelInfo?.totalXp ?: 0,
                        level = levelInfo?.currentLevel ?: 1,
                        xpToNextLevel = levelInfo?.xpToNextLevel ?: 1000,
                        streak = streak,
                        achievements = achievements,
                        unlockedAchievementIds = unlockedIds
                    )
                )
            } catch (e: Exception) {
                _profileState.value = Resource.Error(e.message ?: "Failed to load profile")
            }
        }
    }

    fun loadAnalytics() {
        viewModelScope.launch {
            _analyticsState.value = Resource.Loading
            val user = getCurrentUserUseCase() ?: run {
                _analyticsState.value = Resource.Error("Not logged in")
                return@launch
            }
            try {
                val analytics = getAnalyticsUseCase(user.id)
                _analyticsState.value = Resource.Success(analytics)
            } catch (e: Exception) {
                _analyticsState.value = Resource.Error(e.message ?: "Failed to load analytics")
            }
        }
    }
}
