package hcmute.edu.vn.smartstudyassistant.presentation.pomodoro

import android.os.CountDownTimer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.PomodoroSessionEntity
import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.PomodoroSessionStatus
import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.PomodoroSessionType
import hcmute.edu.vn.smartstudyassistant.domain.model.PomodoroStats
import hcmute.edu.vn.smartstudyassistant.domain.usecase.auth.GetCurrentUserUseCase
import hcmute.edu.vn.smartstudyassistant.domain.usecase.pomodoro.GetPomodoroStatsUseCase
import hcmute.edu.vn.smartstudyassistant.domain.usecase.pomodoro.SavePomodoroSessionUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class TimerState { IDLE, RUNNING, PAUSED }

data class PomodoroUiState(
    val timeLeftSeconds: Int = 25 * 60,
    val totalSeconds: Int = 25 * 60,
    val sessionType: PomodoroSessionType = PomodoroSessionType.FOCUS,
    val timerState: TimerState = TimerState.IDLE,
    val completedSessions: Int = 0,
    val stats: PomodoroStats? = null
)

@HiltViewModel
class PomodoroViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val savePomodoroSessionUseCase: SavePomodoroSessionUseCase,
    private val getPomodoroStatsUseCase: GetPomodoroStatsUseCase
) : ViewModel() {

    companion object {
        const val FOCUS_DURATION = 25 * 60
        const val SHORT_BREAK = 5 * 60
        const val LONG_BREAK = 15 * 60
        const val SESSIONS_BEFORE_LONG_BREAK = 4
    }

    private val _uiState = MutableStateFlow(PomodoroUiState())
    val uiState: StateFlow<PomodoroUiState> = _uiState

    private var countDownTimer: CountDownTimer? = null
    private var sessionStartTime: Long = 0L
    private var interruptionCount = 0

    init {
        loadStats()
    }

    fun selectSessionType(type: PomodoroSessionType) {
        if (_uiState.value.timerState == TimerState.RUNNING) return
        val duration = durationFor(type)
        _uiState.value = _uiState.value.copy(
            sessionType = type,
            timeLeftSeconds = duration,
            totalSeconds = duration,
            timerState = TimerState.IDLE
        )
    }

    fun startPause() {
        when (_uiState.value.timerState) {
            TimerState.IDLE, TimerState.PAUSED -> start()
            TimerState.RUNNING -> pause()
        }
    }

    private fun start() {
        if (_uiState.value.timerState == TimerState.IDLE) {
            sessionStartTime = System.currentTimeMillis()
            interruptionCount = 0
        }
        val timeLeft = _uiState.value.timeLeftSeconds.toLong() * 1000
        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(timeLeft, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                _uiState.value = _uiState.value.copy(
                    timeLeftSeconds = (millisUntilFinished / 1000).toInt()
                )
            }
            override fun onFinish() {
                _uiState.value = _uiState.value.copy(
                    timeLeftSeconds = 0,
                    timerState = TimerState.IDLE
                )
                onSessionComplete()
            }
        }.start()
        _uiState.value = _uiState.value.copy(timerState = TimerState.RUNNING)
    }

    private fun pause() {
        countDownTimer?.cancel()
        interruptionCount++
        _uiState.value = _uiState.value.copy(timerState = TimerState.PAUSED)
    }

    fun reset() {
        countDownTimer?.cancel()
        val duration = durationFor(_uiState.value.sessionType)
        _uiState.value = _uiState.value.copy(
            timeLeftSeconds = duration,
            totalSeconds = duration,
            timerState = TimerState.IDLE
        )
    }

    fun skip() {
        countDownTimer?.cancel()
        onSessionComplete()
    }

    private fun onSessionComplete() {
        val type = _uiState.value.sessionType
        val completed = _uiState.value.completedSessions + if (type == PomodoroSessionType.FOCUS) 1 else 0
        _uiState.value = _uiState.value.copy(completedSessions = completed)

        saveSession(type)

        // Auto-advance: after FOCUS -> SHORT_BREAK or LONG_BREAK
        if (type == PomodoroSessionType.FOCUS) {
            val nextType = if (completed % SESSIONS_BEFORE_LONG_BREAK == 0)
                PomodoroSessionType.LONG_BREAK else PomodoroSessionType.SHORT_BREAK
            selectSessionType(nextType)
        } else {
            selectSessionType(PomodoroSessionType.FOCUS)
        }
        loadStats()
    }

    private fun saveSession(type: PomodoroSessionType) {
        viewModelScope.launch {
            val user = getCurrentUserUseCase() ?: return@launch
            val endTime = System.currentTimeMillis()
            val duration = ((endTime - sessionStartTime) / 1000).toInt()
            val session = PomodoroSessionEntity(
                userId = user.id,
                startedAt = sessionStartTime,
                endedAt = endTime,
                durationSeconds = duration,
                type = type,
                status = PomodoroSessionStatus.COMPLETED,
                interruptionCount = interruptionCount
            )
            savePomodoroSessionUseCase(session)
        }
    }

    fun loadStats() {
        viewModelScope.launch {
            val user = getCurrentUserUseCase() ?: return@launch
            try {
                val stats = getPomodoroStatsUseCase(user.id)
                _uiState.value = _uiState.value.copy(stats = stats)
            } catch (e: Exception) {
                // Stats fail silently
            }
        }
    }

    private fun durationFor(type: PomodoroSessionType) = when (type) {
        PomodoroSessionType.FOCUS -> FOCUS_DURATION
        PomodoroSessionType.SHORT_BREAK -> SHORT_BREAK
        PomodoroSessionType.LONG_BREAK -> LONG_BREAK
    }

    override fun onCleared() {
        super.onCleared()
        countDownTimer?.cancel()
    }
}
