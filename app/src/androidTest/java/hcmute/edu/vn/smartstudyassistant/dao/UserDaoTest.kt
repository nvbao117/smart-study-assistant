package hcmute.edu.vn.smartstudyassistant.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import hcmute.edu.vn.smartstudyassistant.data.local.db.AppDatabase
import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.UserEntity
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UserDaoTest {

    private lateinit var db: AppDatabase

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
    }

    @After
    fun teardown() {
        db.close()
    }

    private fun buildUser(username: String = "testuser", email: String = "test@test.com") = UserEntity(
        username = username, email = email, passwordHash = "hash",
        salt = "salt", displayName = "Test", createdAt = 0L, lastLoginAt = 0L
    )

    @Test
    fun insertAndGetById() = runTest {
        val id = db.userDao().insert(buildUser())
        val fetched = db.userDao().getById(id)
        assertThat(fetched).isNotNull()
        assertThat(fetched?.username).isEqualTo("testuser")
    }

    @Test
    fun getByEmail() = runTest {
        db.userDao().insert(buildUser(email = "specific@test.com"))
        val fetched = db.userDao().getByEmail("specific@test.com")
        assertThat(fetched).isNotNull()
    }

    @Test
    fun getByUsernameReturnsNull_whenNotExists() = runTest {
        val fetched = db.userDao().getByUsername("nobody")
        assertThat(fetched).isNull()
    }

    @Test
    fun duplicateEmail_throwsException() = runTest {
        db.userDao().insert(buildUser(email = "dup@test.com"))
        var threw = false
        try {
            db.userDao().insert(buildUser(username = "other", email = "dup@test.com"))
        } catch (e: Exception) {
            threw = true
        }
        assertThat(threw).isTrue()
    }

    @Test
    fun updateLastLogin() = runTest {
        val id = db.userDao().insert(buildUser())
        db.userDao().updateLastLogin(id, 12345L)
        val fetched = db.userDao().getById(id)
        assertThat(fetched?.lastLoginAt).isEqualTo(12345L)
    }

    @Test
    fun deleteUser() = runTest {
        val user = buildUser()
        val id = db.userDao().insert(user)
        db.userDao().delete(user.copy(id = id))
        assertThat(db.userDao().getById(id)).isNull()
    }
}
