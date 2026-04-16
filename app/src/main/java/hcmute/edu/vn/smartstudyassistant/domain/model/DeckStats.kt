package hcmute.edu.vn.smartstudyassistant.domain.model

data class DeckStats(
    val deckId: Long,
    val deckName: String,
    val totalCards: Int,
    val dueToday: Int,
    val averageEaseFactor: Double,
    val averageQuality: Double?
)
