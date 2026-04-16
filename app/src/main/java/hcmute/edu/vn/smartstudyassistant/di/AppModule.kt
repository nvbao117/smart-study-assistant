package hcmute.edu.vn.smartstudyassistant.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import hcmute.edu.vn.smartstudyassistant.data.local.db.AppDatabase
import hcmute.edu.vn.smartstudyassistant.data.local.dao.*
import hcmute.edu.vn.smartstudyassistant.data.local.preferences.UserPreferences
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        AppDatabase.getInstance(context)

    // ── SharedPrefs ──────────────────────────────────────────────────────────
    @Provides
    @Singleton
    fun provideSharedPrefs(@ApplicationContext context: Context) =
        context.getSharedPreferences("smart_study_prefs", Context.MODE_PRIVATE)

    @Provides
    @Singleton
    fun provideUserPreferences(@ApplicationContext context: Context): UserPreferences =
        UserPreferences(context)

    // ── Auth / User ──────────────────────────────────────────────────────────
    @Provides fun provideUserDao(db: AppDatabase): UserDao = db.userDao()

    // ── Task ─────────────────────────────────────────────────────────────────
    @Provides fun provideSubjectDao(db: AppDatabase): SubjectDao = db.subjectDao()
    @Provides fun provideTaskDao(db: AppDatabase): TaskDao = db.taskDao()
    @Provides fun provideSubtaskDao(db: AppDatabase): SubtaskDao = db.subtaskDao()
    @Provides fun provideRecurringRuleDao(db: AppDatabase): RecurringRuleDao = db.recurringRuleDao()
    @Provides fun provideReminderDao(db: AppDatabase): ReminderDao = db.reminderDao()

    // ── Flashcard ────────────────────────────────────────────────────────────
    @Provides fun provideDeckDao(db: AppDatabase): DeckDao = db.deckDao()
    @Provides fun provideFlashcardDao(db: AppDatabase): FlashcardDao = db.flashcardDao()
    @Provides fun provideCardTagDao(db: AppDatabase): CardTagDao = db.cardTagDao()
    @Provides fun provideReviewLogDao(db: AppDatabase): ReviewLogDao = db.reviewLogDao()

    // ── Pomodoro ─────────────────────────────────────────────────────────────
    @Provides fun providePomodoroSettingsDao(db: AppDatabase): PomodoroSettingsDao = db.pomodoroSettingsDao()
    @Provides fun providePomodoroSessionDao(db: AppDatabase): PomodoroSessionDao = db.pomodoroSessionDao()

    // ── Dashboard ────────────────────────────────────────────────────────────
    @Provides fun provideDailyTipDao(db: AppDatabase): DailyTipDao = db.dailyTipDao()

    // ── Gamification ─────────────────────────────────────────────────────────
    @Provides fun provideXpLogDao(db: AppDatabase): XpLogDao = db.xpLogDao()
    @Provides fun provideUserLevelDao(db: AppDatabase): UserLevelDao = db.userLevelDao()
    @Provides fun provideAchievementDao(db: AppDatabase): AchievementDao = db.achievementDao()
    @Provides fun provideAchievementProgressDao(db: AppDatabase): AchievementProgressDao = db.achievementProgressDao()
    @Provides fun provideStreakHistoryDao(db: AppDatabase): StreakHistoryDao = db.streakHistoryDao()
    @Provides fun provideStudyGoalDao(db: AppDatabase): StudyGoalDao = db.studyGoalDao()
    @Provides fun provideAnalyticsDailyDao(db: AppDatabase): AnalyticsDailyDao = db.analyticsDailyDao()

    // ── AI Chatbot ───────────────────────────────────────────────────────────
    @Provides fun provideChatSessionDao(db: AppDatabase): ChatSessionDao = db.chatSessionDao()
    @Provides fun provideChatMessageDao(db: AppDatabase): ChatMessageDao = db.chatMessageDao()
    @Provides fun provideAiSettingsDao(db: AppDatabase): AiSettingsDao = db.aiSettingsDao()
}
