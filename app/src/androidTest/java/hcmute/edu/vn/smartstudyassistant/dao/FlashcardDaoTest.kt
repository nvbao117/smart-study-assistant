package hcmute.edu.vn.smartstudyassistant.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import hcmute.edu.vn.smartstudyassistant.data.local.db.AppDatabase
import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.*
import hcmute.edu.vn.smartstudyassistant.util.todayMillis
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FlashcardDaoTest {

    private lateinit var db: AppDatabase

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
    }

    @After
    fun teardown() { db.close() }

    private suspend fun insertUserAndDeck(): Long {
        val userId = db.userDao().insert(UserEntity(username = "u", email = "e@e.com", passwordHash = "h", salt = "s", displayName = "U", createdAt = 0L, lastLoginAt = 0L))
        return db.deckDao().insert(DeckEntity(userId = userId, name = "Test Deck"))
    }

    @Test
    fun getDueCards_returnsOnlyDueCards() = runTest {
        val deckId = insertUserAndDeck()
        val now = System.currentTimeMillis()
        val pastDate = now - 24 * 60 * 60 * 1000L
        val futureDate = now + 24 * 60 * 60 * 1000L

        db.flashcardDao().insert(FlashcardEntity(deckId = deckId, front = "Due", back = "B", nextReviewDate = pastDate))
        db.flashcardDao().insert(FlashcardEntity(deckId = deckId, front = "NotDue", back = "B", nextReviewDate = futureDate))

        val dueCards = db.flashcardDao().getDueCards(deckId, todayMillis()).first()
        assertThat(dueCards).hasSize(1)
        assertThat(dueCards[0].front).isEqualTo("Due")
    }

    @Test
    fun insertAndGetById() = runTest {
        val deckId = insertUserAndDeck()
        val id = db.flashcardDao().insert(FlashcardEntity(deckId = deckId, front = "Q", back = "A"))
        val card = db.flashcardDao().getById(id)
        assertThat(card?.front).isEqualTo("Q")
    }

    @Test
    fun deleteCard_removesFromDb() = runTest {
        val deckId = insertUserAndDeck()
        val id = db.flashcardDao().insert(FlashcardEntity(deckId = deckId, front = "Q", back = "A"))
        val card = db.flashcardDao().getById(id)!!
        db.flashcardDao().delete(card)
        assertThat(db.flashcardDao().getById(id)).isNull()
    }
}
