package hcmute.edu.vn.smartstudyassistant.domain.usecase.pomodoro

import javax.inject.Inject

import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.PomodoroSessionEntity
import hcmute.edu.vn.smartstudyassistant.domain.repository.PomodoroRepository

class SavePomodoroSessionUseCase @Inject constructor(private val pomodoroRepository: PomodoroRepository) {
    suspend operator fun invoke(session: PomodoroSessionEntity): Result<Long> =
        try { Result.success(pomodoroRepository.saveSession(session)) } catch (e: Exception) { Result.failure(e) }
}
