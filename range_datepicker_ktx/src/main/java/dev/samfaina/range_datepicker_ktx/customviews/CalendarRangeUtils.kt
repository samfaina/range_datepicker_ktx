package dev.samfaina.range_datepicker_ktx.customviews

import java.util.*


internal object CalendarRangeUtils {
    /**
     * Resets date time to HH:mm:ss SSS = 00:00:00 000
     *
     * @param date [Calendar]
     */
    fun resetTime(date: Calendar) {
        date.set(Calendar.HOUR, 0)
        date.set(Calendar.MINUTE, 0)
        date.set(Calendar.SECOND, 0)
        date.set(Calendar.MILLISECOND, 0)
    }


    fun isDateSame(one: Calendar, second: Calendar): Boolean {
        return (one.get(Calendar.YEAR) == second.get(Calendar.YEAR)
                && one.get(Calendar.MONTH) == second.get(Calendar.MONTH)
                && one.get(Calendar.DATE) == second.get(Calendar.DATE))
    }
}