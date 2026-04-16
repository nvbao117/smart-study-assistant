package hcmute.edu.vn.smartstudyassistant.data.local.db.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import hcmute.edu.vn.smartstudyassistant.data.local.db.entity.*

class Converters {

    private val gson = Gson()

    // ── Collections ──────────────────────────────────────────────────────────

    @TypeConverter
    fun fromStringList(value: String?): List<String> {
        if (value == null) return emptyList()
        val type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, type) ?: emptyList()
    }

    @TypeConverter
    fun toStringList(list: List<String>?): String = gson.toJson(list ?: emptyList<String>())

    @TypeConverter
    fun fromIntList(value: String?): List<Int> {
        if (value == null) return emptyList()
        val type = object : TypeToken<List<Int>>() {}.type
        return gson.fromJson(value, type) ?: emptyList()
    }

    @TypeConverter
    fun toIntList(list: List<Int>?): String = gson.toJson(list ?: emptyList<Int>())

    // ── Enums ────────────────────────────────────────────────────────────────

    @TypeConverter fun fromPriority(v: Priority?): String? = v?.name
    @TypeConverter fun toPriority(v: String?): Priority? = v?.let { Priority.valueOf(it) }

    @TypeConverter fun fromTaskStatus(v: TaskStatus?): String? = v?.name
    @TypeConverter fun toTaskStatus(v: String?): TaskStatus? = v?.let { TaskStatus.valueOf(it) }

    @TypeConverter fun fromRecurringType(v: RecurringType?): String? = v?.name
    @TypeConverter fun toRecurringType(v: String?): RecurringType? = v?.let { RecurringType.valueOf(it) }

    @TypeConverter fun fromCardDifficulty(v: CardDifficulty?): String? = v?.name
    @TypeConverter fun toCardDifficulty(v: String?): CardDifficulty? = v?.let { CardDifficulty.valueOf(it) }

    @TypeConverter fun fromPomodoroSessionType(v: PomodoroSessionType?): String? = v?.name
    @TypeConverter fun toPomodoroSessionType(v: String?): PomodoroSessionType? = v?.let { PomodoroSessionType.valueOf(it) }

    @TypeConverter fun fromPomodoroSessionStatus(v: PomodoroSessionStatus?): String? = v?.name
    @TypeConverter fun toPomodoroSessionStatus(v: String?): PomodoroSessionStatus? = v?.let { PomodoroSessionStatus.valueOf(it) }

    @TypeConverter fun fromXpActionType(v: XpActionType?): String? = v?.name
    @TypeConverter fun toXpActionType(v: String?): XpActionType? = v?.let { XpActionType.valueOf(it) }

    @TypeConverter fun fromGoalType(v: GoalType?): String? = v?.name
    @TypeConverter fun toGoalType(v: String?): GoalType? = v?.let { GoalType.valueOf(it) }

    @TypeConverter fun fromMessageRole(v: MessageRole?): String? = v?.name
    @TypeConverter fun toMessageRole(v: String?): MessageRole? = v?.let { MessageRole.valueOf(it) }

    @TypeConverter fun fromAiProviderType(v: AiProviderType?): String? = v?.name
    @TypeConverter fun toAiProviderType(v: String?): AiProviderType? = v?.let { AiProviderType.valueOf(it) }
}
