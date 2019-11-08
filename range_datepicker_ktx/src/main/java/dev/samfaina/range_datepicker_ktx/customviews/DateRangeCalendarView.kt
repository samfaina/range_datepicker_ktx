package dev.samfaina.range_datepicker_ktx.customviews


import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.viewpager.widget.ViewPager
import dev.samfaina.range_datepicker_ktx.R
import dev.samfaina.range_datepicker_ktx.models.CalendarStyleAttrImpl
import dev.samfaina.range_datepicker_ktx.models.CalendarStyleAttributes
import java.text.DateFormatSymbols
import java.util.*


class DateRangeCalendarView : LinearLayout, DateRangeCalendarViewApi {


    private var tvYearTitle: CustomTextView? = null
    private var imgVNavLeft: AppCompatImageView? = null
    private var imgVNavRight: AppCompatImageView? = null
    private val monthDataList = mutableListOf<Calendar>()
    private var adapterEventCalendarMonths: AdapterEventCalendarMonths? = null
    private var locale: Locale? = null
    private var vpCalendar: ViewPager? = null
    private var calendarStyleAttr: CalendarStyleAttributes? = null
    private var mCalendarListener: DateRangeCalendarViewApi.CalendarListener? = null

    /**
     * To get start date.
     */
    override val startDate: Calendar
        get() = adapterEventCalendarMonths!!.minSelectedDate!!

    /**
     * To get end date.
     */
    override val endDate: Calendar
        get() = adapterEventCalendarMonths!!.maxSelectedDate!!

    /**
     * To get editable mode.
     */
    /**
     * To set editable mode. Default value will be true.
     *
     * @param isEditable true if you want user to select date range else false
     */
    override var isEditable: Boolean
        get() = adapterEventCalendarMonths!!.isEditable
        set(isEditable) {
            adapterEventCalendarMonths!!.isEditable = isEditable
        }

    constructor(context: Context) : super(context) {
        initViews(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initViews(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initViews(context, attrs)
    }

    private fun initViews(context: Context, attrs: AttributeSet?) {
        locale = context.resources.configuration.locale
        calendarStyleAttr = CalendarStyleAttrImpl(context, attrs!!)
        val layoutInflater = LayoutInflater.from(context)
        layoutInflater.inflate(R.layout.layout_calendar_container, this, true)
        val rlHeaderCalendar = findViewById<RelativeLayout>(R.id.rlHeaderCalendar)
        rlHeaderCalendar.background = calendarStyleAttr!!.headerBg
        tvYearTitle = findViewById(R.id.tvYearTitle)
        tvYearTitle!!.setTextSize(TypedValue.COMPLEX_UNIT_PX, calendarStyleAttr!!.textSizeTitle)
        imgVNavLeft = findViewById(R.id.imgVNavLeft)
        imgVNavRight = findViewById(R.id.imgVNavRight)
        vpCalendar = findViewById(R.id.vpCalendar)

        monthDataList.clear()
        val today = Calendar.getInstance().clone() as Calendar
        today.add(Calendar.MONTH, -TOTAL_ALLOWED_MONTHS)

        for (i in 0 until TOTAL_ALLOWED_MONTHS * 2) {
            monthDataList.add(today.clone() as Calendar)
            today.add(Calendar.MONTH, 1)
        }
        adapterEventCalendarMonths =
            AdapterEventCalendarMonths(context, monthDataList, calendarStyleAttr!!)
        vpCalendar!!.adapter = adapterEventCalendarMonths
        vpCalendar!!.offscreenPageLimit = 0
        vpCalendar!!.currentItem = TOTAL_ALLOWED_MONTHS
        setCalendarYearTitle(TOTAL_ALLOWED_MONTHS)
        setListeners()
    }

    private fun setListeners() {

        vpCalendar!!.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                setCalendarYearTitle(position)
                setNavigationHeader(position)
            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })

        imgVNavLeft!!.setOnClickListener {
            val newPosition = vpCalendar!!.currentItem - 1
            if (newPosition > -1) {
                vpCalendar!!.currentItem = newPosition
            }
        }

        imgVNavRight!!.setOnClickListener {
            val newPosition = vpCalendar!!.currentItem + 1
            if (newPosition < monthDataList.size) {
                vpCalendar!!.currentItem = newPosition
            }
        }
    }


    /**
     * To set navigation header ( Left-Right button )
     *
     * @param position New page position
     */
    private fun setNavigationHeader(position: Int) {
        imgVNavRight!!.visibility = View.VISIBLE
        imgVNavLeft!!.visibility = View.VISIBLE
        if (monthDataList.size == 1) {
            imgVNavLeft!!.visibility = View.INVISIBLE
            imgVNavRight!!.visibility = View.INVISIBLE
        } else if (position == 0) {
            imgVNavLeft!!.visibility = View.INVISIBLE
        } else if (position == monthDataList.size - 1) {
            imgVNavRight!!.visibility = View.INVISIBLE
        }
    }

    /**
     * To set calendar year title
     *
     * @param position data list position for getting date
     */
    private fun setCalendarYearTitle(position: Int) {
        val currentCalendarMonth = monthDataList.get(position)
        var dateText =
            DateFormatSymbols(locale).months[currentCalendarMonth.get(Calendar.MONTH)]
        dateText = dateText.substring(0, 1).toUpperCase() + dateText.subSequence(1, dateText.length)

        val yearTitle = dateText + " " + currentCalendarMonth.get(Calendar.YEAR)

        tvYearTitle!!.text = yearTitle
        tvYearTitle!!.setTextColor(calendarStyleAttr!!.titleColor)
    }

    /**
     * To set calendar listener
     *
     * @param calendarListener Listener
     */
    override fun setCalendarListener(calendarListener: DateRangeCalendarViewApi.CalendarListener) {
        mCalendarListener = calendarListener
        adapterEventCalendarMonths!!.setCalendarListener(mCalendarListener!!)
    }

    /**
     * To apply custom fonts to all the text views
     *
     * @param fonts - Typeface that you want to apply
     */
    override fun setFonts(fonts: Typeface) {
        tvYearTitle!!.typeface = fonts
        calendarStyleAttr!!.fonts = fonts
        adapterEventCalendarMonths!!.invalidateCalendar()
    }

    /**
     * To remove all selection and redraw current calendar
     */
    override fun resetAllSelectedViews() {
        adapterEventCalendarMonths!!.resetAllSelectedViews()
    }

    /**
     * To set week offset. To start week from any of the day. Default is 0 (Sunday).
     *
     * @param offset 0-Sun, 1-Mon, 2-Tue, 3-Wed, 4-Thu, 5-Fri, 6-Sat
     */
    override fun setWeekOffset(offset: Int) {
        calendarStyleAttr!!.weekOffset = offset
        adapterEventCalendarMonths!!.invalidateCalendar()
    }

    /**
     * To set left navigation ImageView drawable
     */
    override fun setNavLeftImage(leftDrawable: Drawable) {
        imgVNavLeft!!.setImageDrawable(leftDrawable)
    }

    /**
     * To set right navigation ImageView drawable
     */
    override fun setNavRightImage(rightDrawable: Drawable) {
        imgVNavRight!!.setImageDrawable(rightDrawable)
    }

    /**
     * Sets start and end date.<br></br>
     * <B>Note:</B><br></br>
     * You can not set null start date with valid end date.<br></br>
     * You can not set end date before start date.<br></br>
     * If you are setting custom month range than do not call this before calling (@method setVisibleMonthRange).<br></br>
     *
     * @param startDate Start date
     * @param endDate   End date
     */
    override fun setSelectedDateRange(startDate: Calendar, endDate: Calendar) {

        require(!endDate.before(startDate)) { "Start date can not be after end date." }

        adapterEventCalendarMonths!!.setSelectedDate(startDate, endDate)
    }

    /**
     * Sets start and end date.<br></br>
     * <B>Note:</B><br></br>
     * You can not set null start date with valid end date.<br></br>
     * You can not set end date before start date.<br></br>
     * If you are setting custom month range than do not call this before calling (@method setVisibleMonthRange).<br></br>
     *
     * @param startDate Start date
     * @param endDate   End date
     */
    override fun setDisabledDates(dates: MutableList<Calendar>) {
        adapterEventCalendarMonths!!.setDisabledDates(dates)
    }

    override fun deleteDisabledDate(date: Calendar) {
        adapterEventCalendarMonths!!.deleteDisabledDate(date)
    }

    /**
     * To provide month range to be shown to user. If start month is greater than end month than it will give [IllegalArgumentException].<br></br>
     * By default it will also make selectable date range as per visible month's dates. If you want to customize the selectable date range then
     * use [.setSelectableDateRange].<br></br><br></br>
     * **Note:** Do not call this method after calling date selection method [.setSelectableDateRange]
     * / [.setSelectedDateRange] as it will reset date selection.
     *
     * @param startMonth Start month of the calendar
     * @param endMonth   End month of the calendar
     */
    override fun setVisibleMonthRange(startMonth: Calendar, endMonth: Calendar) {

        val startMonthDate = startMonth.clone() as Calendar
        val endMonthDate = endMonth.clone() as Calendar

        startMonthDate.set(Calendar.DATE, 1)
        CalendarRangeUtils.resetTime(startMonthDate)

        endMonthDate.set(Calendar.DATE, 1)
        CalendarRangeUtils.resetTime(endMonthDate)

        require(!startMonthDate.after(endMonthDate)) { "Start month1(" + startMonthDate.time.toString() + ") can not be later than end month(" + endMonth.time.toString() + ")." }
        monthDataList.clear()

        while (!isDateSame(startMonthDate, endMonthDate)) {
            monthDataList.add(startMonthDate.clone() as Calendar)
            startMonthDate.add(Calendar.MONTH, 1)
        }
        monthDataList.add(startMonthDate.clone() as Calendar)

        adapterEventCalendarMonths =
            AdapterEventCalendarMonths(context, monthDataList, calendarStyleAttr!!)
        vpCalendar!!.adapter = adapterEventCalendarMonths
        vpCalendar!!.offscreenPageLimit = 0
        vpCalendar!!.currentItem = 0
        setCalendarYearTitle(0)
        setNavigationHeader(0)
        adapterEventCalendarMonths!!.setCalendarListener(mCalendarListener!!)
    }

    /**
     * To set current visible month.
     *
     * @param calendar Month to be set as current
     */
    override fun setCurrentMonth(calendar: Calendar) {
        for (i in monthDataList.indices) {
            val month = monthDataList[i]
            if (month.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)) {
                if (month.get(Calendar.MONTH) == calendar.get(Calendar.MONTH)) {
                    vpCalendar!!.currentItem = i
                    break
                }
            }
        }
    }


    override fun setSelectableDateRange(startDate: Calendar, endDate: Calendar) {
        require(!endDate.before(startDate)) { "Start date(" + startDate.time.toString() + ") can not be after end date(" + endDate.time.toString() + ")." }
        adapterEventCalendarMonths!!.setSelectableDateRange(startDate, endDate)
    }

    private fun isDateSame(one: Calendar, second: Calendar): Boolean {
        return (one.get(Calendar.YEAR) == second.get(Calendar.YEAR)
                && one.get(Calendar.MONTH) == second.get(Calendar.MONTH)
                && one.get(Calendar.DATE) == second.get(Calendar.DATE))
    }

    companion object {

        private val TOTAL_ALLOWED_MONTHS = 30
    }
}