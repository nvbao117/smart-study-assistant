package hcmute.edu.vn.smartstudyassistant.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import hcmute.edu.vn.smartstudyassistant.data.local.dao.*
import hcmute.edu.vn.smartstudyassistant.data.local.db.converter.Converters
import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        SubjectEntity::class,
        TaskEntity::class,
        SubtaskEntity::class,
        RecurringRuleEntity::class,
        ReminderEntity::class,
        DeckEntity::class,
        FlashcardEntity::class,
        CardTagEntity::class,
        ReviewLogEntity::class,
        PomodoroSettingsEntity::class,
        PomodoroSessionEntity::class,
        DailyTipEntity::class,
        XpLogEntity::class,
        UserLevelEntity::class,
        AchievementEntity::class,
        AchievementProgressEntity::class,
        StreakHistoryEntity::class,
        StudyGoalEntity::class,
        AnalyticsDailyEntity::class,
        ChatSessionEntity::class,
        ChatMessageEntity::class,
        AiSettingsEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    // Phase 1
    abstract fun userDao(): UserDao

    // Phase 2
    abstract fun subjectDao(): SubjectDao
    abstract fun taskDao(): TaskDao
    abstract fun subtaskDao(): SubtaskDao
    abstract fun recurringRuleDao(): RecurringRuleDao
    abstract fun reminderDao(): ReminderDao

    // Phase 3
    abstract fun deckDao(): DeckDao
    abstract fun flashcardDao(): FlashcardDao
    abstract fun cardTagDao(): CardTagDao
    abstract fun reviewLogDao(): ReviewLogDao

    // Phase 4
    abstract fun pomodoroSettingsDao(): PomodoroSettingsDao
    abstract fun pomodoroSessionDao(): PomodoroSessionDao

    // Phase 5
    abstract fun dailyTipDao(): DailyTipDao

    // Phase 6
    abstract fun xpLogDao(): XpLogDao
    abstract fun userLevelDao(): UserLevelDao
    abstract fun achievementDao(): AchievementDao
    abstract fun achievementProgressDao(): AchievementProgressDao
    abstract fun streakHistoryDao(): StreakHistoryDao
    abstract fun studyGoalDao(): StudyGoalDao
    abstract fun analyticsDailyDao(): AnalyticsDailyDao

    // Phase 7
    abstract fun chatSessionDao(): ChatSessionDao
    abstract fun chatMessageDao(): ChatMessageDao
    abstract fun aiSettingsDao(): AiSettingsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "smart_study_assistant.db"
                )
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            CoroutineScope(Dispatchers.IO).launch {
                INSTANCE?.achievementDao()?.insertAll(predefinedAchievements())
            }
        }
    }
}

private fun predefinedAchievements(): List<AchievementEntity> = listOf(
    AchievementEntity(name = "First Step",       description = "Complete your first task",             xpReward = 50,  requirementType = "TASKS_COMPLETED",   requirementValue = 1),
    AchievementEntity(name = "Task Master",      description = "Complete 50 tasks",                    xpReward = 200, requirementType = "TASKS_COMPLETED",   requirementValue = 50),
    AchievementEntity(name = "Study Machine",    description = "Complete 100 Pomodoro sessions",       xpReward = 300, requirementType = "POMODORO_SESSIONS", requirementValue = 100),
    AchievementEntity(name = "Card Collector",   description = "Create 100 flashcards",                xpReward = 150, requirementType = "CARDS_CREATED",     requirementValue = 100),
    AchievementEntity(name = "Review Pro",       description = "Review 500 cards",                     xpReward = 250, requirementType = "CARDS_REVIEWED",    requirementValue = 500),
    AchievementEntity(name = "Week Warrior",     description = "Achieve a 7-day streak",               xpReward = 100, requirementType = "STREAK_DAYS",       requirementValue = 7),
    AchievementEntity(name = "Month Champion",   description = "Achieve a 30-day streak",              xpReward = 500, requirementType = "STREAK_DAYS",       requirementValue = 30),
    AchievementEntity(name = "Focus King",       description = "Accumulate 1000 total focus minutes",  xpReward = 200, requirementType = "FOCUS_MINUTES",     requirementValue = 1000)
)
