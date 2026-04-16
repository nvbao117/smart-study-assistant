package hcmute.edu.vn.smartstudyassistant.domain.usecase.pomodoro

import javax.inject.Inject

import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.PomodoroSettingsEntity
import hcmute.edu.vn.smartstudyassistant.domain.repository.PomodoroRepository
import hcmute.edu.vn.smartstudyassistant.util.Constants

class GetPomodoroSettingsUseCase @Inject constructor(private val pomodoroRepository: PomodoroRepository) {
    suspend operator fun invoke(userId: Long): PomodoroSettingsEntity =
        pomodoroRepository.getSettings(userId) ?: PomodoroSettingsEntity(userId = userId)
}
