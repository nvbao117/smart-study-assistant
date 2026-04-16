package hcmute.edu.vn.smartstudyassistant.presentation.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.TaskEntity
import hcmute.edu.vn.smartstudyassistant.domain.usecase.auth.GetCurrentUserUseCase
import hcmute.edu.vn.smartstudyassistant.domain.usecase.task.CompleteTaskUseCase
import hcmute.edu.vn.smartstudyassistant.domain.usecase.task.GetTasksByDateUseCase
import hcmute.edu.vn.smartstudyassistant.util.Resource
import hcmute.edu.vn.smartstudyassistant.util.todayMillis
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val getTasksByDateUseCase: GetTasksByDateUseCase,
    private val completeTaskUseCase: CompleteTaskUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    private val _tasksState = MutableStateFlow<Resource<List<TaskEntity>>>(Resource.Loading)
    val tasksState: StateFlow<Resource<List<TaskEntity>>> = _tasksState

    init {
        loadTasksForToday()
    }

    fun loadTasksForToday() {
        viewModelScope.launch {
            _tasksState.value = Resource.Loading
            try {
                val user = getCurrentUserUseCase() ?: run {
                    _tasksState.value = Resource.Error("User not logged in")
                    return@launch
                }
                getTasksByDateUseCase(user.id, todayMillis()).collect { tasks ->
                    _tasksState.value = Resource.Success(tasks)
                }
            } catch (e: Exception) {
                _tasksState.value = Resource.Error(e.message ?: "Failed to load tasks")
            }
        }
    }

    fun completeTask(task: TaskEntity) {
        viewModelScope.launch {
            try {
                completeTaskUseCase(task.id)
                // Reload list
                loadTasksForToday()
            } catch (e: Exception) {
                // handle error silently or via effect
            }
        }
    }
}
