package hcmute.edu.vn.smartstudyassistant.data.local.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import hcmute.edu.vn.smartstudyassistant.util.Constants

@Entity(
    tableName = "flashcards",
    foreignKeys = [
        ForeignKey(entity = DeckEntity::class, parentColumns = ["id"], childColumns = ["deckId"], onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index("deckId")]
)
data class FlashcardEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val deckId: Long,
    val front: String,
    val back: String,
    val difficulty: CardDifficulty = CardDifficulty.MEDIUM,
    // SM-2 fields
    val nextReviewDate: Long = System.currentTimeMillis(),
    val repetitionCount: Int = 0,
    val intervalDays: Int = Constants.SM2_FIRST_REVIEW_INTERVAL,
    val easeFactor: Double = Constants.SM2_DEFAULT_EASE_FACTOR,
    val createdAt: Long = System.currentTimeMillis()
)
