package dev.samfaina.range_datepicker_ktx.models

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import dev.samfaina.range_datepicker_ktx.R


class CalendarStyleAttrImpl : CalendarStyleAttributes {

    override var fonts: Typeface? = null
    override var headerBg: Drawable? = null

    override var titleColor: Int = 0

    override var weekColor: Int = 0

    override var rangeStripColor: Int = 0

    override var selectedDateCircleColor: Int = 0

    override var selectedDateColor: Int = 0

    override var defaultDateColor: Int = 0

    override var disableDateColor: Int = 0

    override var disableDateStrikeColor: Int = 0

    override var rangeDateColor: Int = 0

    override var textSizeTitle: Float = 0f

    override var textSizeWeek: Float = 0f

    override var textSizeDate: Float = 0f

    override var isShouldEnabledTime = false
    /**
     * To set week offset
     *
     * @param weekOffset
     */
    override var weekOffset = 0
        set(weekOffset) {
            require(!(weekOffset < 0 || weekOffset > 6)) { "Week offset can only be between 0 to 6. " + "0->Sun, 1->Mon, 2->Tue, 3->Wed, 4->Thu, 5->Fri, 6->Sat" }
            field = weekOffset
        }
    override var isEditable = true

    private constructor(context: Context) {
        setDefAttributes(context)
    }

    constructor(context: Context, attributeSet: AttributeSet) {
        setDefAttributes(context)
        setAttributes(context, attributeSet)
    }

    /**
     * To parse attributes from xml layout to configure calendar views.
     */
    private fun setDefAttributes(context: Context) {

        textSizeTitle = context.resources.getDimension(R.dimen.text_size_title)
        textSizeWeek = context.resources.getDimension(R.dimen.text_size_week)
        textSizeDate = context.resources.getDimension(R.dimen.text_size_date)

        titleColor = ContextCompat.getColor(context, R.color.title_color)
        weekColor = ContextCompat.getColor(context, R.color.week_color)
        rangeStripColor = ContextCompat.getColor(context, R.color.range_bg_color)
        selectedDateCircleColor =
            ContextCompat.getColor(context, R.color.selected_date_circle_color)
        selectedDateColor = ContextCompat.getColor(context, R.color.selected_date_color)
        defaultDateColor = ContextCompat.getColor(context, R.color.default_date_color)
        rangeDateColor = ContextCompat.getColor(context, R.color.range_date_color)
        disableDateColor = ContextCompat.getColor(context, R.color.disable_date_color)
        disableDateStrikeColor = ContextCompat.getColor(context, R.color.disable_strike_color)
    }

    private fun setAttributes(context: Context, attributeSet: AttributeSet?) {
        if (attributeSet != null) {
            val ta =
                context.obtainStyledAttributes(attributeSet, R.styleable.DateRangeMonthView, 0, 0)
            try {
                titleColor = ta.getColor(R.styleable.DateRangeMonthView_title_color, titleColor)
                headerBg = ta.getDrawable(R.styleable.DateRangeMonthView_header_bg)
                weekColor = ta.getColor(R.styleable.DateRangeMonthView_week_color, weekColor)
                rangeStripColor =
                    ta.getColor(R.styleable.DateRangeMonthView_range_color, rangeStripColor)
                selectedDateCircleColor = ta.getColor(
                    R.styleable.DateRangeMonthView_selected_date_circle_color,
                    selectedDateCircleColor
                )
                isShouldEnabledTime =
                    ta.getBoolean(R.styleable.DateRangeMonthView_enable_time_selection, false)
                isEditable = ta.getBoolean(R.styleable.DateRangeMonthView_editable, true)

                textSizeTitle =
                    ta.getDimension(R.styleable.DateRangeMonthView_text_size_title, textSizeTitle)
                textSizeWeek =
                    ta.getDimension(R.styleable.DateRangeMonthView_text_size_week, textSizeWeek)
                textSizeDate =
                    ta.getDimension(R.styleable.DateRangeMonthView_text_size_date, textSizeDate)

                selectedDateColor = ta.getColor(
                    R.styleable.DateRangeMonthView_selected_date_color,
                    selectedDateColor
                )
                defaultDateColor =
                    ta.getColor(R.styleable.DateRangeMonthView_default_date_color, defaultDateColor)
                rangeDateColor =
                    ta.getColor(R.styleable.DateRangeMonthView_range_date_color, rangeDateColor)
                disableDateColor =
                    ta.getColor(R.styleable.DateRangeMonthView_disable_date_color, disableDateColor)

                disableDateStrikeColor = ta.getColor(
                    R.styleable.DateRangeMonthView_disable_strike_color,
                    disableDateStrikeColor
                )
                weekOffset = ta.getColor(R.styleable.DateRangeMonthView_week_offset, 0)


            } finally {
                ta.recycle()
            }
        }
    }

    companion object {

        /**
         * To parse attributes from xml layout to configure calendar views.
         */
        fun getDefAttributes(context: Context): CalendarStyleAttrImpl {

            val calendarStyleAttr = CalendarStyleAttrImpl(context)
            calendarStyleAttr.textSizeTitle =
                context.resources.getDimension(R.dimen.text_size_title)
            calendarStyleAttr.textSizeWeek =
                context.resources.getDimension(R.dimen.text_size_week)
            calendarStyleAttr.textSizeDate =
                context.resources.getDimension(R.dimen.text_size_date)
            calendarStyleAttr.weekColor = ContextCompat.getColor(context, R.color.week_color)
            calendarStyleAttr.rangeStripColor =
                ContextCompat.getColor(context, R.color.range_bg_color)
            calendarStyleAttr.selectedDateCircleColor =
                ContextCompat.getColor(context, R.color.selected_date_circle_color)
            calendarStyleAttr.selectedDateColor =
                ContextCompat.getColor(context, R.color.selected_date_color)
            calendarStyleAttr.defaultDateColor =
                ContextCompat.getColor(context, R.color.default_date_color)
            calendarStyleAttr.rangeDateColor =
                ContextCompat.getColor(context, R.color.range_date_color)
            calendarStyleAttr.disableDateColor =
                ContextCompat.getColor(context, R.color.disable_date_color)

            calendarStyleAttr.disableDateStrikeColor =
                ContextCompat.getColor(context, R.color.disable_strike_color)
            return calendarStyleAttr
        }
    }
}