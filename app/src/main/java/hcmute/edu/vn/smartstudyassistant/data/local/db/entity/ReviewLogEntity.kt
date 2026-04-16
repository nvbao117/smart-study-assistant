package hcmute.edu.vn.smartstudyassistant.data.local.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "review_logs",
    foreignKeys = [
        ForeignKey(entity = FlashcardEntity::class, parentColumns = ["id"], childColumns = ["cardId"], onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index("cardId")]
)
data class ReviewLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val cardId: Long,
    val reviewedAt: Long = System.currentTimeMillis(),
    val quality: Int,         // 1-5
    val previousInterval: Int,
    val newInterval: Int
)
