package dev.samfaina.range_datepicker_ktx.customviews

import android.content.Context
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import dev.samfaina.range_datepicker_ktx.R
import dev.samfaina.range_datepicker_ktx.models.CalendarStyleAttributes
import java.util.*


internal class AdapterEventCalendarMonths(
    private val mContext: Context,
    private val dataList: List<Calendar>,
    private val calendarStyleAttr: CalendarStyleAttributes
) : PagerAdapter() {
    private var calendarListener: DateRangeCalendarViewApi.CalendarListener? = null
    private val dateRangeCalendarManager: DateRangeCalendarManager
    private val mHandler: Handler = Handler()


    private val calendarAdapterListener = object : DateRangeCalendarViewApi.CalendarListener {
        override fun onFirstDateSelected(startDate: Calendar) {

            mHandler.postDelayed({ notifyDataSetChanged() }, 50)

            if (calendarListener != null) {
                calendarListener!!.onFirstDateSelected(startDate)
            }
        }

        override fun onDateRangeSelected(startDate: Calendar, endDate: Calendar) {
            mHandler.postDelayed({ notifyDataSetChanged() }, 50)
            if (calendarListener != null) {
                calendarListener!!.onDateRangeSelected(startDate, endDate)
            }
        }


        override fun onDisabledDateSelected(disabledDate: Calendar) {
            mHandler.postDelayed({ notifyDataSetChanged() }, 50)
            if (calendarListener != null) {
                calendarListener!!.onDisabledDateSelected(disabledDate)
            }
        }
    }


    val minSelectedDate: Calendar?
        get() = dateRangeCalendarManager.minSelectedDate

    val maxSelectedDate: Calendar?
        get() = dateRangeCalendarManager.maxSelectedDate

    /**
     * To get editable mode.
     */
    /**
     * To set editable mode. Set true if you want user to select date range else false. Default value will be true.
     */
    var isEditable: Boolean
        get() = calendarStyleAttr.isEditable
        set(isEditable) {
            calendarStyleAttr.isEditable = isEditable
            notifyDataSetChanged()
        }

    init {
        // Get start month and set start date of that month
        val startSelectableDate = dataList[0].clone() as Calendar
        startSelectableDate.set(Calendar.DAY_OF_MONTH, 1)
        // Get end month and set end date of that month
        val endSelectableDate = dataList[dataList.size - 1].clone() as Calendar
        endSelectableDate.set(
            Calendar.DAY_OF_MONTH,
            endSelectableDate.getActualMaximum(Calendar.DAY_OF_MONTH)
        )
        dateRangeCalendarManager = DateRangeCalendarManager(startSelectableDate, endSelectableDate)

    }

    override fun getCount(): Int {
        return dataList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {

        val modelObject = dataList[position]
        val inflater = LayoutInflater.from(mContext)
        val layout = inflater.inflate(R.layout.layout_pager_month, container, false) as ViewGroup

        val dateRangeMonthView = layout.findViewById<DateRangeMonthView>(R.id.cvEventCalendarView)
        dateRangeMonthView.drawCalendarForMonth(
            calendarStyleAttr,
            getCurrentMonth(modelObject),
            dateRangeCalendarManager
        )
        dateRangeMonthView.setCalendarListener(calendarAdapterListener)

        container.addView(layout)
        return layout
    }

    /**
     * To clone calendar obj and get current month calendar starting from 1st date.
     *
     * @param calendar - Calendar
     * @return - Modified calendar obj of month of 1st date.
     */
    private fun getCurrentMonth(calendar: Calendar): Calendar {
        val current = calendar.clone() as Calendar
        current.set(Calendar.DAY_OF_MONTH, 1)
        return current
    }

    override fun destroyItem(collection: ViewGroup, position: Int, view: Any) {
        collection.removeView(view as View)
    }

    override fun getItemPosition(`object`: Any): Int {
        return POSITION_NONE
    }

    fun setCalendarListener(calendarListener: DateRangeCalendarViewApi.CalendarListener) {
        this.calendarListener = calendarListener
    }

    /**
     * To redraw calendar.
     */
    fun invalidateCalendar() {
        mHandler.postDelayed({ notifyDataSetChanged() }, 50)
    }

    /**
     * To remove all selection and redraw current calendar
     */
    fun resetAllSelectedViews() {
        dateRangeCalendarManager.minSelectedDate = null
        dateRangeCalendarManager.maxSelectedDate = null
        notifyDataSetChanged()
    }

    fun setSelectedDate(minSelectedDate: Calendar, maxSelectedDate: Calendar) {
        dateRangeCalendarManager.minSelectedDate = minSelectedDate
        dateRangeCalendarManager.maxSelectedDate = maxSelectedDate
        notifyDataSetChanged()
    }

    fun setDisabledDates(dates: MutableList<Calendar>) {
        dateRangeCalendarManager.disabledDates = dates
        notifyDataSetChanged()
    }

    fun setSelectableDateRange(startDate: Calendar, endDate: Calendar) {
        dateRangeCalendarManager.setSelectableDateRange(startDate, endDate)
        notifyDataSetChanged()
    }

    fun deleteDisabledDate(date: Calendar) {
        dateRangeCalendarManager.disabledDates.remove(date)
        notifyDataSetChanged()
    }
}