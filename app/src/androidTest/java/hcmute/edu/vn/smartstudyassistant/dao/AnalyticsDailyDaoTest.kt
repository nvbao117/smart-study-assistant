package hcmute.edu.vn.smartstudyassistant.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import hcmute.edu.vn.smartstudyassistant.data.local.db.AppDatabase
import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.*
import hcmute.edu.vn.smartstudyassistant.util.todayMillis
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AnalyticsDailyDaoTest {

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

    private suspend fun insertUser(): Long =
        db.userDao().insert(UserEntity(username = "u", email = "e@e.com", passwordHash = "h", salt = "s", displayName = "U", createdAt = 0L, lastLoginAt = 0L))

    @Test
    fun insertAndGetByDate() = runTest {
        val userId = insertUser()
        val today = todayMillis()
        db.analyticsDailyDao().insert(AnalyticsDailyEntity(userId, today, tasksCompleted = 3))
        val entry = db.analyticsDailyDao().getByDate(userId, today)
        assertThat(entry?.tasksCompleted).isEqualTo(3)
    }

    @Test
    fun incrementTasksCompleted() = runTest {
        val userId = insertUser()
        val today = todayMillis()
        db.analyticsDailyDao().insert(AnalyticsDailyEntity(userId, today))
        db.analyticsDailyDao().incrementTasksCompleted(userId, today)
        db.analyticsDailyDao().incrementTasksCompleted(userId, today)
        val entry = db.analyticsDailyDao().getByDate(userId, today)
        assertThat(entry?.tasksCompleted).isEqualTo(2)
    }

    @Test
    fun addFocusMinutes() = runTest {
        val userId = insertUser()
        val today = todayMillis()
        db.analyticsDailyDao().insert(AnalyticsDailyEntity(userId, today))
        db.analyticsDailyDao().addFocusMinutes(userId, today, 25)
        db.analyticsDailyDao().addFocusMinutes(userId, today, 25)
        val entry = db.analyticsDailyDao().getByDate(userId, today)
        assertThat(entry?.focusMinutes).isEqualTo(50)
    }

    @Test
    fun getByDateRange_returnsCorrectEntries() = runTest {
        val userId = insertUser()
        val day1 = 1000L
        val day2 = 2000L
        val day3 = 3000L
        db.analyticsDailyDao().insert(AnalyticsDailyEntity(userId, day1))
        db.analyticsDailyDao().insert(AnalyticsDailyEntity(userId, day2))
        db.analyticsDailyDao().insert(AnalyticsDailyEntity(userId, day3))
        val range = db.analyticsDailyDao().getByDateRange(userId, day1, day3)
        assertThat(range).hasSize(2)
    }
}
