# Smart Study Assistant - Backend Implementation Plan

## Tổng quan
App học tập offline-first, dữ liệu 100% local bằng Room (SQLite).  
**Kiến trúc**: MVVM + Clean Architecture (UseCase → Repository → Room DAO)  
**Ngôn ngữ**: Kotlin | **Database**: Room | **Scope**: Chỉ Backend + Tests (không UI)  
**AI Chatbot**: 3 providers (OpenAI, Gemini, Anthropic) - user chọn trong settings

---

## Phase 0: Project Foundation

### Mục tiêu
Cấu hình Gradle, tạo package structure, các utility dùng chung.

### Gradle Changes
**`gradle/libs.versions.toml`** - thêm:
```toml
[versions]
ksp = "2.1.21-2.0.1"
room = "2.7.1"
coroutines = "1.10.2"
lifecycle = "2.9.0"
okhttp = "4.12.0"
gson = "2.12.1"
mockitoKotlin = "5.4.0"
truth = "1.4.4"
turbine = "1.2.0"
robolectric = "4.14.1"
archCoreTesting = "2.2.0"
```

**`app/build.gradle.kts`** - thêm:
- Plugin: `alias(libs.plugins.ksp)`
- `kotlinOptions { jvmTarget = "11" }`
- `buildFeatures { viewBinding = true }`
- Dependencies: Room, Coroutines, Lifecycle, OkHttp, Gson
- Test deps: Mockito-Kotlin, Truth, Turbine, Robolectric, arch-core-testing

> **Lưu ý AGP 9.0.1**: Đã bundle Kotlin compiler, KHÔNG cần `kotlin-android` plugin riêng.

### Package Structure
```
app/src/main/java/hcmute/edu/vn/smartstudyassistant/
├── data/
│   ├── local/
│   │   ├── db/
│   │   │   ├── entity/        # Room @Entity classes
│   │   │   └── converter/     # TypeConverters
│   │   ├── dao/               # Room @Dao interfaces
│   │   └── preferences/       # SharedPreferences wrapper
│   ├── remote/
│   │   └── ai/                # AI provider implementations
│   └── repository/            # Repository implementations
├── domain/
│   ├── model/                 # Domain data classes
│   ├── repository/            # Repository interfaces
│   └── usecase/               # Use cases theo module
│       ├── auth/
│       ├── task/
│       ├── flashcard/
│       ├── pomodoro/
│       ├── dashboard/
│       ├── gamification/
│       └── chatbot/
└── util/                      # Extensions, constants, helpers
```

### Utility Files
| File | Nội dung |
|------|----------|
| `util/Resource.kt` | `sealed class Resource<T> { Loading, Success(data), Error(message) }` |
| `util/DateUtil.kt` | Extensions: `Long.toDate()`, `Date.startOfDay()`, `Date.formatDisplay()` |
| `util/HashUtil.kt` | `generateSalt()`, `hashPassword()`, `verifyPassword()` - dùng SHA-256 |
| `util/EncryptionUtil.kt` | AES encrypt/decrypt cho API keys (Android KeyStore) |
| `util/Constants.kt` | XP values, default Pomodoro times, SM-2 defaults |

### Verification
```bash
./gradlew assembleDebug   # must pass
```

---

## Phase 1: Auth & Database Setup

### Entities
**`UserEntity`** (table: `users`)
```
id: Long (PK, autoGenerate)
username: String (unique)
email: String (unique)
passwordHash: String
salt: String
displayName: String
createdAt: Long
lastLoginAt: Long
```

### DAO - `UserDao`
- `insert(user): Long`
- `getByEmail(email): UserEntity?`
- `getByUsername(username): UserEntity?`
- `getById(id): UserEntity?`
- `updateLastLogin(id, timestamp)`
- `delete(user)`

### `AppDatabase`
- Singleton RoomDatabase với TypeConverters
- TypeConverters: Date↔Long, List<String>↔String, Enum↔String
- Ban đầu chỉ có UserEntity, thêm dần qua các Phase

### Use Cases
| UseCase | Logic |
|---------|-------|
| `RegisterUseCase` | validate input → check duplicate email/username → hash password → insert |
| `LoginUseCase` | find by email → verify hash → update lastLogin → return user |
| `GetCurrentUserUseCase` | SharedPreferences → Room lookup |

### Tests
- `UserDaoTest` (instrumented): CRUD, duplicate constraint
- `RegisterUseCaseTest`: happy path, duplicate email, empty fields
- `LoginUseCaseTest`: happy path, wrong password, user not found
- `HashUtilTest`: hash consistency, salt uniqueness, verify correct/wrong

### Verification
```bash
./gradlew assembleDebug && ./gradlew test
```

---

## Phase 2: M03 - Task & Schedule Backend

### Entities (5 tables)
| Entity | Các cột quan trọng |
|--------|-------------------|
| `SubjectEntity` | id, userId, name, color(hex), icon, description |
| `TaskEntity` | id, userId, subjectId?, title, priority(enum), status(enum), dueDate?, estimatedMinutes, completedAt? |
| `SubtaskEntity` | id, taskId(FK), title, isCompleted, sortOrder |
| `RecurringRuleEntity` | id, taskId(FK), type(DAILY/WEEKLY/MONTHLY), daysOfWeek?, interval, nextOccurrence |
| `ReminderEntity` | id, taskId(FK), remindAt, isSent |

**Enums**: `Priority(LOW/MEDIUM/HIGH/URGENT)`, `TaskStatus(TODO/IN_PROGRESS/DONE)`

### DAOs (5 files)
| DAO | Queries đặc biệt |
|-----|----------------|
| `SubjectDao` | getWithTaskCount (JOIN) |
| `TaskDao` | getByDateRange, getOverdue, getUpcoming7Days, getByPriority, search(LIKE) |
| `SubtaskDao` | toggleComplete(id, isCompleted) |
| `RecurringRuleDao` | getDueRecurring(now) |
| `ReminderDao` | getUpcoming(now, limit), markSent(id) |

### Repository
- `TaskRepository` interface + `TaskRepositoryImpl`
- `SubjectRepository` interface + impl
- `createTask()` dùng `@Transaction` để insert Task + Subtasks + RecurringRule + Reminder cùng lúc

### Use Cases (8 files)
| UseCase | Mô tả |
|---------|-------|
| `CreateTaskUseCase` | validate → repo.createTask (transaction) |
| `UpdateTaskUseCase` | validate → repo.updateTask |
| `DeleteTaskUseCase` | cascade subtasks/reminders |
| `CompleteTaskUseCase` | set DONE, completedAt=now, mark all subtasks done |
| `GetTasksByDateUseCase` | filter by date range |
| `GetOverdueTasksUseCase` | tasks WHERE dueDate < now AND status != DONE |
| `GetUpcomingDeadlinesUseCase` | 7 ngày tới |
| `ProcessRecurringTasksUseCase` | check due recurring → tạo task mới → update nextOccurrence |

### Tests
- `TaskDaoTest`: CRUD, getByDateRange, getOverdue, search
- `CreateTaskUseCaseTest`: happy path, empty title, past due date
- `CompleteTaskUseCaseTest`: status change, subtasks cascade
- `ProcessRecurringTasksUseCaseTest`: daily/weekly/monthly generation

---

## Phase 3: M02 - Flashcard Backend

### Entities (4 tables)
| Entity | Các cột quan trọng |
|--------|-------------------|
| `DeckEntity` | id, userId, subjectId?, name, lastStudiedAt? |
| `FlashcardEntity` | id, deckId(FK), front, back, difficulty(enum), **nextReviewDate**, **repetitionCount**, **intervalDays**, **easeFactor(2.5)** |
| `CardTagEntity` | id, cardId(FK), tagName |
| `ReviewLogEntity` | id, cardId(FK), reviewedAt, quality(1-5), previousInterval, newInterval |

### SM-2 Algorithm
**File**: `domain/usecase/flashcard/SM2Algorithm.kt`

```kotlin
object SM2Algorithm {
    fun calculate(quality: Int, repetitionCount: Int, 
                  intervalDays: Int, easeFactor: Double): SM2Result
}

data class SM2Result(newRepCount: Int, newIntervalDays: Int, 
                     newEaseFactor: Double, nextReviewDate: Long)
```

| Quality | Tương đương | Logic |
|---------|-------------|-------|
| 1 | Again | rep=0, interval=1 |
| 2 | Hard | rep=0, interval=1 |
| 3 | Good | rep++, interval tăng theo EF |
| 4 | Easy | rep++, interval tăng theo EF*1.3 |
| 5 | Perfect | rep++, interval tăng theo EF*1.3 |

**EF formula**: `newEF = max(1.3, EF + (0.1 - (5-q)*(0.08+(5-q)*0.02)))`  
**Interval**: rep=1→1 day, rep=2→6 days, rep≥3→round(oldInterval×EF)

### DAOs (3 files)
- `DeckDao`: getWithCardCount, getWithDueCardCount(today)
- `FlashcardDao`: getDueCards(deckId, today), getByTag, search
- `ReviewLogDao`: getReviewCountByDateRange, getAverageQualityByDeck

### Use Cases
- `CreateDeckUseCase`, `DeleteDeckUseCase`
- `CreateCardUseCase`, `UpdateCardUseCase`, `DeleteCardUseCase`
- `GetDueCardsUseCase`: WHERE nextReviewDate <= today
- `ReviewCardUseCase`: apply SM2 → update card fields → insert ReviewLog → update deck.lastStudiedAt
- `GetDeckStatsUseCase`: total cards, due today, avg ease, review history

### Tests
- `SM2AlgorithmTest`: ALL quality levels (1-5), first review, subsequent, min EF boundary
- `FlashcardDaoTest`: getDueCards accuracy
- `ReviewCardUseCaseTest`: SM-2 applied correctly, log created

---

## Phase 4: M04 - Pomodoro Backend

### Entities (2 tables)
**`PomodoroSettingsEntity`**: focusMinutes(25), shortBreakMinutes(5), longBreakMinutes(15), sessionsBeforeLongBreak(4)  
**`PomodoroSessionEntity`**: startedAt, endedAt, durationSeconds, type(FOCUS/SHORT_BREAK/LONG_BREAK), status(COMPLETED/CANCELLED/INTERRUPTED), interruptionCount

### DAOs (2 files)
- `PomodoroSettingsDao`: insertOrUpdate (`@Insert onConflict=REPLACE`)
- `PomodoroSessionDao`: getByDateRange, getFocusMinutesByDate, getMostProductiveHour (GROUP BY hour), getSessionCount(type)

### Use Cases
- `SavePomodoroSessionUseCase`, `GetPomodoroSettingsUseCase`, `UpdatePomodoroSettingsUseCase`
- `GetPomodoroStatsUseCase`: returns `PomodoroStats(focusToday, focusWeek, totalSessions, avgDuration, mostProductiveHour)`

### Tests
- `PomodoroSessionDaoTest`: date range, aggregation queries
- `GetPomodoroStatsUseCaseTest`: verify aggregation logic

---

## Phase 5: M01 - Dashboard Backend

### Entity (1 table)
**`DailyTipEntity`**: id, userId, tipText, tipCategory, date(Long - day only)

### Use Cases
**`GetDashboardDataUseCase`** - gom dữ liệu từ tất cả module:
```kotlin
data class DashboardData(
    val userName: String,
    val todayTasks: List<TaskEntity>,       // top 3 by priority
    val currentStreak: Int,
    val upcomingDeadlines: List<TaskEntity>, // 7 ngày tới
    val subjectProgress: List<SubjectProgress>,
    val focusMinutesToday: Int,
    val cardsDueToday: Int,
    val dailyTip: String?
)
```

**`GenerateDailyTipUseCase`** - rule-based:
| Điều kiện | Tip |
|-----------|-----|
| overdueCount > 3 | "Bạn có {n} task quá hạn. Hãy ưu tiên xử lý chúng trước!" |
| cardsDue > 20 | "Có {n} thẻ cần ôn tập. Spaced repetition giúp nhớ lâu hơn 50%!" |
| focusYesterday < 30min | "Hãy thử 1 phiên Pomodoro 25 phút để bắt đầu ngày mới!" |
| streak > 7 | "Tuyệt vời! Chuỗi {n} ngày liên tục. Tiếp tục phát huy!" |
| default | Pool 10 general study tips (hardcoded) |

---

## Phase 6: M06 - Gamification Backend

### Entities (7 tables)
| Entity | Mô tả |
|--------|-------|
| `XpLogEntity` | actionType(enum), xpAmount, description |
| `UserLevelEntity` | totalXp, currentLevel, xpToNextLevel |
| `AchievementEntity` | name, xpReward, requirementType, requirementValue |
| `AchievementProgressEntity` | userId+achievementId, currentValue, isUnlocked |
| `StreakHistoryEntity` | date, hasActivity, currentStreak, longestStreak |
| `StudyGoalEntity` | type(DAILY_FOCUS/DAILY_TASKS/WEEKLY_REVIEWS), targetValue |
| `AnalyticsDailyEntity` | date, tasksCompleted, focusMinutes, cardsReviewed, xpEarned |

### XP & Level System
**XP per action**:
- Task COMPLETE: +10 XP (HIGH: +15, URGENT: +20)
- Pomodoro COMPLETE: +15 XP
- Flashcard REVIEW: +5 XP/card
- Streak BONUS: +streak × 5 XP

**Level formula**:
```
level = floor(sqrt(totalXp / 100.0)) + 1
xpToNextLevel = level² × 100 - totalXp
```

### Predefined Achievements (seed khi onCreate)
| Achievement | Điều kiện | XP Reward |
|-------------|-----------|-----------|
| "First Step" | Complete 1 task | 50 XP |
| "Task Master" | Complete 50 tasks | 200 XP |
| "Study Machine" | 100 Pomodoro sessions | 300 XP |
| "Card Collector" | Create 100 flashcards | 150 XP |
| "Review Pro" | Review 500 cards | 250 XP |
| "Week Warrior" | 7-day streak | 100 XP |
| "Month Champion" | 30-day streak | 500 XP |
| "Focus King" | 1000 total focus minutes | 200 XP |

### Use Cases (6 files)
- `AwardXpUseCase`: insert XpLog → update UserLevel → check level up
- `CheckAchievementUseCase`: check requirements → unlock → award bonus XP
- `UpdateStreakUseCase`: check today activity → update streak count
- `GetAnalyticsUseCase`: daily/weekly/monthly stats
- `ManageGoalsUseCase`: CRUD goals + check completion
- `UpdateDailyAnalyticsUseCase`: increment counters sau mỗi action

### Tests
- `AwardXpUseCaseTest`: mỗi action type, level up trigger
- `CheckAchievementUseCaseTest`: unlock conditions, skip if already unlocked
- `UpdateStreakUseCaseTest`: consecutive, broken, longest streak
- `AnalyticsDailyDaoTest`: aggregation accuracy

---

## Phase 7: M05 - AI Chatbot Backend

### Entities (3 tables)
| Entity | Mô tả |
|--------|-------|
| `ChatSessionEntity` | userId, subjectId?, title, aiProvider(enum) |
| `ChatMessageEntity` | sessionId(FK), role(USER/ASSISTANT/SYSTEM), content |
| `AiSettingsEntity` | selectedProvider, openaiApiKeyEncrypted, geminiApiKeyEncrypted, anthropicApiKeyEncrypted, temperature(0.7), maxTokens(1024) |

### AI Provider Architecture
```kotlin
// Interface chung
interface AiChatProvider {
    suspend fun sendMessage(
        messages: List<ChatMessageEntity>,
        apiKey: String,
        temperature: Double,
        maxTokens: Int
    ): Result<String>
}

// 3 Implementations
class OpenAiProvider     // POST api.openai.com/v1/chat/completions
class GeminiProvider     // POST generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent
class AnthropicProvider  // POST api.anthropic.com/v1/messages

// Factory
class AiProviderFactory {
    fun getProvider(type: AiProviderType): AiChatProvider
}
```

Mỗi provider: build JSON request → OkHttp call → parse response JSON → `Result<String>`  
API keys: **encrypted** với AES + Android KeyStore trước khi lưu vào Room

### Use Cases
- `SendChatMessageUseCase`:
  1. Insert user message → DB
  2. Load recent messages (context)
  3. Get AI settings (provider, key, config)
  4. Decrypt API key
  5. Call `AiProviderFactory.getProvider(type).sendMessage(...)`
  6. Insert assistant response → DB
- `GetChatHistoryUseCase`, `CreateChatSessionUseCase`, `DeleteChatSessionUseCase`
- `ManageAiSettingsUseCase`: save/load settings, encrypt/decrypt keys
- `ExportChatUseCase`: session → formatted plain text String

### Tests
- `OpenAiProviderTest`: mock OkHttp, verify request JSON format
- `GeminiProviderTest`: mock OkHttp, verify Gemini-specific format
- `AnthropicProviderTest`: mock OkHttp, verify Anthropic-specific format
- `SendChatMessageUseCaseTest`: happy path, API error, no API key
- `EncryptionUtilTest`: encrypt → decrypt roundtrip

---

## Summary

| Phase | Entities | DAOs | UseCases | Tests | Files |
|-------|----------|------|----------|-------|-------|
| 0: Foundation | - | - | - | 1 | ~6 |
| 1: Auth | 1 | 1 | 3 | 4 | ~11 |
| 2: Task/Schedule | 5 | 5 | 8 | 5 | ~27 |
| 3: Flashcard | 4 | 3 | 7 | 4 | ~20 |
| 4: Pomodoro | 2 | 2 | 4 | 3 | ~13 |
| 5: Dashboard | 1 | 1 | 2 | 2 | ~6 |
| 6: Gamification | 7 | 7 | 6 | 5 | ~27 |
| 7: AI Chatbot | 3 | 3 | 5 | 7 | ~20 |
| **Total** | **23** | **22** | **35** | **31** | **~130** |

## Thứ tự thực hiện (tối ưu dependencies)
```
Phase 0 (Foundation)
    ↓
Phase 1 (Auth + DB skeleton)
    ↓
Phase 2 (Task/Schedule) ← Dashboard cần dữ liệu này
    ↓
Phase 3 (Flashcard) ← Dashboard cần dữ liệu này
    ↓
Phase 4 (Pomodoro) ← Dashboard cần dữ liệu này
    ↓
Phase 5 (Dashboard) ← Tổng hợp từ Phase 2-4
    ↓
Phase 6 (Gamification) ← Cần data từ tất cả module
    ↓
Phase 7 (AI Chatbot) ← Độc lập, làm cuối
```

## Verification (sau mỗi Phase)
```bash
./gradlew assembleDebug            # Build OK
./gradlew test                     # Unit tests pass
./gradlew connectedAndroidTest     # DAO tests pass (cần emulator)
```
