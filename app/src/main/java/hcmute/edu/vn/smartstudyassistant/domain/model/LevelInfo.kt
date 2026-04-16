package hcmute.edu.vn.smartstudyassistant.domain.model

data class LevelInfo(
    val currentLevel: Int,
    val totalXp: Int,
    val xpToNextLevel: Int,
    val didLevelUp: Boolean
)
