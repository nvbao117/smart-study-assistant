package hcmute.edu.vn.smartstudyassistant.data.local.preferences

import android.content.Context
import android.content.SharedPreferences

class UserPreferences(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "smart_study_prefs"
        private const val KEY_USER_ID = "current_user_id"
    }

    var currentUserId: Long
        get() = prefs.getLong(KEY_USER_ID, -1L)
        set(value) = prefs.edit().putLong(KEY_USER_ID, value).apply()

    fun isLoggedIn(): Boolean = currentUserId != -1L

    fun logout() = prefs.edit().remove(KEY_USER_ID).apply()
}
