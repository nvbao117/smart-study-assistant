package hcmute.edu.vn.smartstudyassistant.domain.usecase.pomodoro

import javax.inject.Inject

import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.PomodoroSettingsEntity
import hcmute.edu.vn.smartstudyassistant.domain.repository.PomodoroRepository

class UpdatePomodoroSettingsUseCase @Inject constructor(private val pomodoroRepository: PomodoroRepository) {
    suspend operator fun invoke(settings: PomodoroSettingsEntity): Result<Unit> =
        try { pomodoroRepository.saveSettings(settings); Result.success(Unit) } catch (e: Exception) { Result.failure(e) }
}
