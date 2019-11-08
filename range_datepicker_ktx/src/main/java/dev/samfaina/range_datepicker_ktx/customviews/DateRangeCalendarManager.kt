package dev.samfaina.range_datepicker_ktx.customviews

import androidx.annotation.IntDef
import dev.samfaina.range_datepicker_ktx.customviews.DateRangeCalendarManager.RangeType.Companion.LAST_DATE
import dev.samfaina.range_datepicker_ktx.customviews.DateRangeCalendarManager.RangeType.Companion.MIDDLE_DATE
import dev.samfaina.range_datepicker_ktx.customviews.DateRangeCalendarManager.RangeType.Companion.NOT_IN_RANGE
import dev.samfaina.range_datepicker_ktx.customviews.DateRangeCalendarManager.RangeType.Companion.START_DATE
import java.text.SimpleDateFormat
import java.util.*


internal class DateRangeCalendarManager(
    startSelectableDate: Calendar,
    endSelectableDate: Calendar
) {

    var minSelectedDate: Calendar? = null
        set(minSelectedDate) {
            field = (minSelectedDate?.clone()) as Calendar?
        }
    var maxSelectedDate: Calendar? = null
        set(maxSelectedDate) {
            field = (maxSelectedDate?.clone()) as Calendar?
        }

    var disabledDates = mutableListOf<Calendar>()
    private var mStartSelectableDate: Calendar? = null
    private var mEndSelectableDate: Calendar? = null

    @Retention(AnnotationRetention.SOURCE)
    @IntDef(NOT_IN_RANGE, START_DATE, MIDDLE_DATE, LAST_DATE)
    annotation class RangeType {
        companion object {
            const val NOT_IN_RANGE = 0
            const val START_DATE = 1
            const val MIDDLE_DATE = 2
            const val LAST_DATE = 3
        }
    }

    init {
        setSelectableDateRange(startSelectableDate, endSelectableDate)
    }

    fun setSelectableDateRange(startDate: Calendar, endDate: Calendar) {
        mStartSelectableDate = startDate.clone() as Calendar
        CalendarRangeUtils.resetTime(mStartSelectableDate!!)
        mEndSelectableDate = endDate.clone() as Calendar
        CalendarRangeUtils.resetTime(mEndSelectableDate!!)
    }

    /**
     * To check whether date belongs to range or not
     *
     * @return Date type
     */
    @RangeType
    fun checkDateRange(selectedDate: Calendar): Int {

        val dateStr = SIMPLE_DATE_FORMAT.format(selectedDate.time)

        if (this.minSelectedDate != null && this.maxSelectedDate == null) {
            val minDateStr = SIMPLE_DATE_FORMAT.format(this.minSelectedDate!!.time)
            return if (dateStr.equals(minDateStr, ignoreCase = true)) {
                START_DATE
            } else {
                NOT_IN_RANGE
            }
        } else if (this.minSelectedDate != null) {
            //Min date and Max date are selected
            val selectedDateVal = java.lang.Long.valueOf(dateStr)

            val minDateStr = SIMPLE_DATE_FORMAT.format(this.minSelectedDate!!.time)
            val maxDateStr = SIMPLE_DATE_FORMAT.format(this.maxSelectedDate!!.time)

            val minDateVal = java.lang.Long.valueOf(minDateStr)
            val maxDateVal = java.lang.Long.valueOf(maxDateStr)

            return when (selectedDateVal) {
                minDateVal -> START_DATE
                maxDateVal -> LAST_DATE
                //            } else if (selectedDateVal > minDateVal && selectedDateVal < maxDateVal) {
                in (minDateVal + 1) until maxDateVal -> MIDDLE_DATE
                else -> NOT_IN_RANGE
            }
        } else {
            return NOT_IN_RANGE
        }
    }

    fun isAvailableDate(): Boolean =
        minSelectedDate != null && !isDisabledDate(minSelectedDate) && maxSelectedDate != null && !isDisabledDate(
            maxSelectedDate
        )

    fun isSelectableDate(date: Calendar): Boolean {
        // It would work even if date is exactly equal to one of the end cases
        val isSelectable = !(date.before(mStartSelectableDate) || date.after(mEndSelectableDate))
        require(!(!isSelectable && checkDateRange(date) != NOT_IN_RANGE)) { "Selected date can not be out of Selectable Date range." }
        return isSelectable
    }

    fun isDisabledDate(date: Calendar?): Boolean {
        if (date == null) return false

        return disabledDates.find {
            it.get(Calendar.YEAR) == date.get(Calendar.YEAR) &&
                    it.get(Calendar.MONTH) == date.get(Calendar.MONTH) &&
                    it.get(Calendar.DAY_OF_MONTH) == date.get(Calendar.DAY_OF_MONTH)
        } != null

    }


    companion object {
        private const val DATE_FORMAT = "yyyyMMdd"
        var SIMPLE_DATE_FORMAT = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
    }
}