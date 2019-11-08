package dev.samfaina.range_datepicker_ktx.models

import android.view.View
import android.widget.RelativeLayout
import dev.samfaina.range_datepicker_ktx.customviews.CustomTextView
import java.text.SimpleDateFormat
import java.util.*


class DayContainer(var rootView: RelativeLayout) {
    var tvDate: CustomTextView = rootView.getChildAt(1) as CustomTextView
    var strip: View = rootView.getChildAt(0)
    var strike: View = rootView.getChildAt(2)

    companion object {

        private val simpleDateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())

        fun getContainerKey(cal: Calendar): Int {

            val str = simpleDateFormat.format(cal.time)
            return Integer.valueOf(str)
        }
    }
}