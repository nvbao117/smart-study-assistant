package hcmute.edu.vn.smartstudyassistant.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hcmute.edu.vn.smartstudyassistant.domain.usecase.auth.LoginUseCase
import hcmute.edu.vn.smartstudyassistant.domain.usecase.auth.RegisterUseCase
import hcmute.edu.vn.smartstudyassistant.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    private val _loginState = MutableStateFlow<Resource<Unit>>(Resource.Success(Unit))
    val loginState: StateFlow<Resource<Unit>> = _loginState

    private val _registerState = MutableStateFlow<Resource<Unit>>(Resource.Success(Unit))
    val registerState: StateFlow<Resource<Unit>> = _registerState

    fun login(username: String, pass: String) {
        viewModelScope.launch {
            _loginState.value = Resource.Loading
            val result = loginUseCase(username, pass)
            if (result.isSuccess) {
                _loginState.value = Resource.Success(Unit)
            } else {
                _loginState.value = Resource.Error(result.exceptionOrNull()?.message ?: "Login failed")
            }
        }
    }

    fun register(username: String, pass: String, email: String, displayName: String = username) {
        viewModelScope.launch {
            _registerState.value = Resource.Loading
            val result = registerUseCase(username, email, pass, displayName)
            if (result.isSuccess) {
                _registerState.value = Resource.Success(Unit)
            } else {
                _registerState.value = Resource.Error(result.exceptionOrNull()?.message ?: "Registration failed")
            }
        }
    }
}
