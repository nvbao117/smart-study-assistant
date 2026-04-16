package hcmute.edu.vn.smartstudyassistant.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hcmute.edu.vn.smartstudyassistant.domain.model.DashboardData
import hcmute.edu.vn.smartstudyassistant.domain.usecase.auth.GetCurrentUserUseCase
import hcmute.edu.vn.smartstudyassistant.domain.usecase.dashboard.GetDashboardDataUseCase
import hcmute.edu.vn.smartstudyassistant.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getDashboardDataUseCase: GetDashboardDataUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    private val _dashboardState = MutableStateFlow<Resource<DashboardData>>(Resource.Loading)
    val dashboardState: StateFlow<Resource<DashboardData>> = _dashboardState

    init {
        loadDashboard()
    }

    fun loadDashboard() {
        viewModelScope.launch {
            _dashboardState.value = Resource.Loading
            try {
                val user = getCurrentUserUseCase()
                if (user != null) {
                    val data = getDashboardDataUseCase(user.id)
                    _dashboardState.value = Resource.Success(data)
                } else {
                    _dashboardState.value = Resource.Error("User not logged in")
                }
            } catch (e: Exception) {
                _dashboardState.value = Resource.Error(e.message ?: "Failed to load dashboard")
            }
        }
    }
}
