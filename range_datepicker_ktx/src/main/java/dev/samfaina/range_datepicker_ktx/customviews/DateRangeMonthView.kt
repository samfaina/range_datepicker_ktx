package dev.samfaina.range_datepicker_ktx.customviews

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.Typeface
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import dev.samfaina.range_datepicker_ktx.R
import dev.samfaina.range_datepicker_ktx.models.CalendarStyleAttributes
import dev.samfaina.range_datepicker_ktx.models.DayContainer
import dev.samfaina.range_datepicker_ktx.timepicker.AwesomeTimePickerDialog
import java.text.ParseException
import java.util.*


internal class DateRangeMonthView : LinearLayout {
    private var llDaysContainer: LinearLayout? = null
    private var llTitleWeekContainer: LinearLayout? = null

    private var currentCalendarMonth: Calendar? = null

    private var calendarStyleAttr: CalendarStyleAttributes? = null

    private var calendarListener: DateRangeCalendarViewApi.CalendarListener? = null

    private var dateRangeCalendarManager: DateRangeCalendarManager? = null


    private val dayClickListener = OnClickListener { view ->
        val selectedCal = getSelectedDayFromView(view)
        if (calendarStyleAttr!!.isEditable && !dateRangeCalendarManager!!.isDisabledDate(selectedCal)) {

            var minSelectedDate = dateRangeCalendarManager!!.minSelectedDate
            var maxSelectedDate = dateRangeCalendarManager!!.maxSelectedDate

            val pair = handleAvailableDayClick(minSelectedDate, maxSelectedDate, selectedCal)
            maxSelectedDate = pair.first
            minSelectedDate = pair.second

            if (calendarStyleAttr!!.isShouldEnabledTime) {
                handleShouldEnableTime(minSelectedDate, maxSelectedDate, selectedCal)
            } else {
                Log.i(LOG_TAG, "Time: " + selectedCal.time.toString())
                if (maxSelectedDate != null) {
                    minSelectedDate?.let {
                        calendarListener!!.onDateRangeSelected(
                            it,
                            maxSelectedDate
                        )
                    }
                } else {
                    minSelectedDate?.let { calendarListener!!.onFirstDateSelected(it) }
                }
            }
        } else if (calendarStyleAttr!!.isEditable && dateRangeCalendarManager!!.isDisabledDate(
                selectedCal
            )
        ) {
            calendarListener!!.onDisabledDateSelected(selectedCal)
        }
    }

    private fun handleAvailableDayClick(
        minSelectedDate: Calendar?,
        maxSelectedDate: Calendar?,
        selectedCal: Calendar
    ): Pair<Calendar?, Calendar?> {
        var minSelectedDate1 = minSelectedDate
        var maxSelectedDate1 = maxSelectedDate
        if (minSelectedDate1 != null && maxSelectedDate1 == null) {
            maxSelectedDate1 = selectedCal

            val startDateKey = DayContainer.getContainerKey(minSelectedDate1)
            val lastDateKey = DayContainer.getContainerKey(maxSelectedDate1)

            if (startDateKey == lastDateKey) {
                minSelectedDate1 = maxSelectedDate1
            } else if (startDateKey > lastDateKey) {
                val temp = minSelectedDate1.clone() as Calendar
                minSelectedDate1 = maxSelectedDate1
                maxSelectedDate1 = temp
            }
        } else if (maxSelectedDate1 == null) {
            //This will call one time only
            minSelectedDate1 = selectedCal
        } else {
            minSelectedDate1 = selectedCal
            maxSelectedDate1 = null
        }

        dateRangeCalendarManager!!.minSelectedDate = minSelectedDate1
        dateRangeCalendarManager!!.maxSelectedDate = maxSelectedDate1
        drawCalendarForMonth(currentCalendarMonth!!)
        return Pair(maxSelectedDate1, minSelectedDate1)
    }


    private fun getSelectedDayFromView(view: View): Calendar {
        val key = view.tag as Int
        val selectedCal = Calendar.getInstance()
        var date = Date()
        try {
            date = DateRangeCalendarManager.SIMPLE_DATE_FORMAT.parse(key.toString())
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        selectedCal.time = date

        return selectedCal
    }


    private fun handleShouldEnableTime(
        minSelectedDate: Calendar?,
        maxSelectedDate: Calendar?,
        selectedCal: Calendar
    ) {
        val awesomeTimePickerDialog = AwesomeTimePickerDialog(
            context,
            context.getString(R.string.select_time),
            object : AwesomeTimePickerDialog.TimePickerCallback {
                override fun onTimeSelected(hours: Int, mins: Int) {
                    selectedCal.set(Calendar.HOUR, hours)
                    selectedCal.set(Calendar.MINUTE, mins)

                    Log.i(LOG_TAG, "Time: " + selectedCal.time.toString())
                    if (calendarListener != null) {

                        if (maxSelectedDate != null) {
                            minSelectedDate?.let {
                                calendarListener!!.onDateRangeSelected(
                                    it,
                                    maxSelectedDate
                                )
                            }
                        } else {
                            minSelectedDate?.let {
                                calendarListener!!.onFirstDateSelected(
                                    it
                                )
                            }
                        }
                    }
                }

                override fun onCancel() {
                    this@DateRangeMonthView.resetAllSelectedViews()
                }
            })
        awesomeTimePickerDialog.showDialog()
    }


    fun setCalendarListener(calendarListener: DateRangeCalendarViewApi.CalendarListener) {
        this.calendarListener = calendarListener
    }

    constructor(context: Context) : super(context) {
        initView(context, null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initView(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView(context, attrs)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    ) {
        initView(context, attrs)
    }

    /**
     * To initialize child views
     *
     * @param context      - App context
     * @param attributeSet - Attr set
     */
    private fun initView(context: Context, attributeSet: AttributeSet?) {
        val layoutInflater = LayoutInflater.from(context)
        val mainView =
            layoutInflater.inflate(R.layout.layout_calendar_month, this, true) as LinearLayout
        llDaysContainer = mainView.findViewById(R.id.llDaysContainer)
        llTitleWeekContainer = mainView.findViewById(R.id.llTitleWeekContainer)

        setListeners()
    }

    /**
     * To set listeners.
     */
    private fun setListeners() {}

    /**
     * To draw calendar for the given month. Here calendar object should start from date of 1st.
     *
     * @param calendarStyleAttr        Calendar style attributes
     * @param month                    Month to be drawn
     * @param dateRangeCalendarManager Calendar data manager
     */
    fun drawCalendarForMonth(
        calendarStyleAttr: CalendarStyleAttributes,
        month: Calendar,
        dateRangeCalendarManager: DateRangeCalendarManager
    ) {
        this.calendarStyleAttr = calendarStyleAttr
        this.currentCalendarMonth = month.clone() as Calendar
        this.dateRangeCalendarManager = dateRangeCalendarManager
        setConfigs()
        setWeekTitleColor(calendarStyleAttr.weekColor)
        drawCalendarForMonth(currentCalendarMonth!!)
    }

    /**
     * To draw calendar for the given month. Here calendar object should start from date of 1st.
     *
     * @param month Calendar month
     */
    private fun drawCalendarForMonth(month: Calendar) {

        currentCalendarMonth = month.clone() as Calendar
        currentCalendarMonth!!.set(Calendar.DATE, 1)
        CalendarRangeUtils.resetTime(currentCalendarMonth!!)

        val weekTitle = context.resources.getStringArray(R.array.week_sun_sat)

        //To set week day title as per offset
        for (i in 0..6) {
            val textView = llTitleWeekContainer!!.getChildAt(i) as CustomTextView
            val weekStr = weekTitle[(i + calendarStyleAttr!!.weekOffset) % 7]
            textView.text = weekStr
        }

        var startDay = month.get(Calendar.DAY_OF_WEEK) - calendarStyleAttr!!.weekOffset

        //To ratate week day according to offset
        if (startDay < 1) {
            startDay += 7
        }
        month.add(Calendar.DATE, -startDay + 1)

        for (i in 0 until llDaysContainer!!.childCount) {
            val weekRow = llDaysContainer!!.getChildAt(i) as LinearLayout
            for (j in 0..6) {
                val rlDayContainer = weekRow.getChildAt(j) as RelativeLayout
                val container = DayContainer(rlDayContainer)
                container.tvDate.text = month.get(Calendar.DATE).toString()
                if (calendarStyleAttr!!.fonts != null) {
                    container.tvDate.typeface = calendarStyleAttr!!.fonts
                }
                drawDayContainer(container, month)
                month.add(Calendar.DATE, 1)
            }
        }
    }

    /**
     * To draw specific date container according to past date, today, selected or from range.
     *
     * @param container - Date container
     * @param calendar  - Calendar obj of specific date of the month.
     */
    private fun drawDayContainer(container: DayContainer, calendar: Calendar) {

        val date = calendar.get(Calendar.DATE)

        // reset views
        container.strike.visibility = View.GONE
        container.tvDate.alpha = 1f
        container.tvDate.setTypeface(container.tvDate.typeface, Typeface.NORMAL)

        if (currentCalendarMonth!!.get(Calendar.MONTH) != calendar.get(Calendar.MONTH)) {
            hideDayContainer(container)
        } else if (!dateRangeCalendarManager!!.isSelectableDate(calendar)) {
            disableDayContainer(container)
            container.tvDate.text = date.toString()
        } else if (dateRangeCalendarManager!!.isDisabledDate(calendar)) {
            makeAsDisabledDate(container)
        } else {
            @DateRangeCalendarManager.RangeType val type =
                dateRangeCalendarManager!!.checkDateRange(calendar)
            if (type == DateRangeCalendarManager.RangeType.START_DATE || type == DateRangeCalendarManager.RangeType.LAST_DATE) {
                makeAsSelectedDate(container, type)


            } else if (type == DateRangeCalendarManager.RangeType.MIDDLE_DATE) {
                when (dateRangeCalendarManager!!.isDisabledDate(calendar)) {
                    true -> makeAsDisabledDate(container)
                    false -> makeAsRangeDate(container)
                }

            } else {
                enabledDayContainer(container)
            }

            container.tvDate.text = date.toString()
            container.tvDate.setTextSize(
                TypedValue.COMPLEX_UNIT_PX,
                calendarStyleAttr!!.textSizeDate
            )
        }

        container.rootView.tag = DayContainer.getContainerKey(calendar)
    }

    /**
     * To draw date container as disabled date.
     *
     * @param container - DayContainer
     */
    private fun makeAsDisabledDate(container: DayContainer) {
        enabledDayContainer(container)
        container.tvDate.setTypeface(container.tvDate.typeface, Typeface.ITALIC)
        container.strike.visibility = View.VISIBLE
        container.strike.setBackgroundColor(calendarStyleAttr!!.disableDateStrikeColor)
        container.tvDate.alpha = 0.7f
    }

    /**
     * To hide date if date is from previous month.
     *
     * @param container - Container
     */
    private fun hideDayContainer(container: DayContainer) {
        container.tvDate.text = ""
        container.tvDate.setBackgroundColor(Color.TRANSPARENT)
        container.strip.setBackgroundColor(Color.TRANSPARENT)
        container.rootView.setBackgroundColor(Color.TRANSPARENT)
        container.rootView.visibility = View.INVISIBLE
        container.rootView.setOnClickListener(null)
    }

    /**
     * To disable past date. Click listener will be removed.
     *
     * @param container - Container
     */
    private fun disableDayContainer(container: DayContainer) {
        container.tvDate.setBackgroundColor(Color.TRANSPARENT)
        container.strip.setBackgroundColor(Color.TRANSPARENT)
        container.rootView.setBackgroundColor(Color.TRANSPARENT)
        container.tvDate.setTextColor(calendarStyleAttr!!.disableDateColor)
        container.rootView.visibility = View.VISIBLE
        container.rootView.setOnClickListener(null)
    }

    /**
     * To enable date by enabling click listeners.
     *
     * @param container - Container
     */
    private fun enabledDayContainer(container: DayContainer) {
        container.tvDate.setBackgroundColor(Color.TRANSPARENT)
        container.strip.setBackgroundColor(Color.TRANSPARENT)
        container.rootView.setBackgroundColor(Color.TRANSPARENT)
        container.tvDate.setTextColor(calendarStyleAttr!!.defaultDateColor)
        container.rootView.visibility = View.VISIBLE
        container.rootView.setOnClickListener(dayClickListener)
    }

    /**
     * To draw date container as selected as end selection or middle selection.
     *
     * @param container - Container
     * @param stripType - Right end date, Left end date or middle
     */
    private fun makeAsSelectedDate(container: DayContainer, @DateRangeCalendarManager.RangeType stripType: Int) {
        val layoutParams = container.strip.layoutParams as RelativeLayout.LayoutParams

        val minDate = dateRangeCalendarManager!!.minSelectedDate
        val maxDate = dateRangeCalendarManager!!.maxSelectedDate

        if (stripType == DateRangeCalendarManager.RangeType.START_DATE && maxDate != null &&
            minDate!!.compareTo(maxDate) != 0
        ) {
            val mDrawable = ContextCompat.getDrawable(context, R.drawable.range_bg_left)
            mDrawable!!.colorFilter =
                PorterDuffColorFilter(calendarStyleAttr!!.rangeStripColor, FILTER_MODE)

            container.strip.background = mDrawable
            layoutParams.setMargins(20, 0, 0, 0)
        } else if (stripType == DateRangeCalendarManager.RangeType.LAST_DATE) {
            val mDrawable = ContextCompat.getDrawable(context, R.drawable.range_bg_right)
            mDrawable!!.colorFilter =
                PorterDuffColorFilter(calendarStyleAttr!!.rangeStripColor, FILTER_MODE)
            container.strip.background = mDrawable
            layoutParams.setMargins(0, 0, 20, 0)
        } else {
            container.strip.setBackgroundColor(Color.TRANSPARENT)
            layoutParams.setMargins(0, 0, 0, 0)
        }
        container.strip.layoutParams = layoutParams
        val mDrawable = ContextCompat.getDrawable(context, R.drawable.green_circle)
        mDrawable!!.colorFilter =
            PorterDuffColorFilter(calendarStyleAttr!!.selectedDateCircleColor, FILTER_MODE)
        container.tvDate.background = mDrawable
        container.rootView.setBackgroundColor(Color.TRANSPARENT)
        container.tvDate.setTextColor(calendarStyleAttr!!.selectedDateColor)
        container.rootView.visibility = View.VISIBLE
        container.rootView.setOnClickListener(dayClickListener)
    }


    /**
     * To draw date as middle date
     *
     * @param container - Container
     */
    private fun makeAsRangeDate(container: DayContainer) {
        container.tvDate.setBackgroundColor(Color.TRANSPARENT)
        val mDrawable = ContextCompat.getDrawable(context, R.drawable.range_bg)
        mDrawable!!.colorFilter =
            PorterDuffColorFilter(calendarStyleAttr!!.rangeStripColor, FILTER_MODE)
        container.strip.background = mDrawable
        container.rootView.setBackgroundColor(Color.TRANSPARENT)
        container.tvDate.setTextColor(calendarStyleAttr!!.rangeDateColor)
        container.rootView.visibility = View.VISIBLE
        val layoutParams = container.strip.layoutParams as RelativeLayout.LayoutParams
        layoutParams.setMargins(0, 0, 0, 0)
        container.strip.layoutParams = layoutParams
        container.rootView.setOnClickListener(dayClickListener)
    }


    /**
     * To remove all selection and redraw current calendar
     */
    fun resetAllSelectedViews() {

        dateRangeCalendarManager!!.minSelectedDate = null
        dateRangeCalendarManager!!.maxSelectedDate = null
        dateRangeCalendarManager!!.disabledDates.clear()

        drawCalendarForMonth(currentCalendarMonth!!)

    }


    /**
     * To set week title color
     *
     * @param color - resource color value
     */
    fun setWeekTitleColor(@ColorInt color: Int) {
        for (i in 0 until llTitleWeekContainer!!.childCount) {
            val textView = llTitleWeekContainer!!.getChildAt(i) as CustomTextView
            textView.setTextColor(color)
        }
    }

    /**
     * To apply configs to all the text views
     */
    private fun setConfigs() {
        drawCalendarForMonth(currentCalendarMonth!!)
        for (i in 0 until llTitleWeekContainer!!.childCount) {
            val textView = llTitleWeekContainer!!.getChildAt(i) as CustomTextView
            textView.typeface = calendarStyleAttr!!.fonts
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, calendarStyleAttr!!.textSizeWeek)
        }
    }

    companion object {

        private val LOG_TAG = DateRangeMonthView::class.java.simpleName

        private val FILTER_MODE = PorterDuff.Mode.SRC_IN
    }
}