package hcmute.edu.vn.smartstudyassistant.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun Long.toDate(): Date = Date(this)

fun Date.toMillis(): Long = this.time

fun Date.startOfDay(): Date {
    val cal = Calendar.getInstance()
    cal.time = this
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)
    return cal.time
}

fun Date.endOfDay(): Date {
    val cal = Calendar.getInstance()
    cal.time = this
    cal.set(Calendar.HOUR_OF_DAY, 23)
    cal.set(Calendar.MINUTE, 59)
    cal.set(Calendar.SECOND, 59)
    cal.set(Calendar.MILLISECOND, 999)
    return cal.time
}

fun Date.formatDisplay(): String =
    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(this)

fun Date.formatDateTime(): String =
    SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(this)

fun Long.startOfDay(): Long = Date(this).startOfDay().time

fun Long.endOfDay(): Long = Date(this).endOfDay().time

fun todayMillis(): Long = Date().startOfDay().time

fun daysFromNow(days: Int): Long {
    val cal = Calendar.getInstance()
    cal.add(Calendar.DAY_OF_YEAR, days)
    return cal.time.time
}

fun isToday(millis: Long): Boolean {
    val today = Date().startOfDay()
    val date = Date(millis).startOfDay()
    return today == date
}

fun daysBetween(startMillis: Long, endMillis: Long): Int {
    val diff = endMillis - startMillis
    return (diff / (1000 * 60 * 60 * 24)).toInt()
}
