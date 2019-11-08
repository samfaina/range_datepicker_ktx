package dev.samfaina.range_datepicker_ktx.models

import android.graphics.Typeface
import android.graphics.drawable.Drawable


interface CalendarStyleAttributes {

    var fonts: Typeface?

    val titleColor: Int

    val headerBg: Drawable?

    val weekColor: Int

    val rangeStripColor: Int

    val selectedDateCircleColor: Int

    val selectedDateColor: Int

    val defaultDateColor: Int

    val disableDateColor: Int

    val disableDateStrikeColor: Int

    val rangeDateColor: Int

    val textSizeTitle: Float

    val textSizeWeek: Float

    val textSizeDate: Float

    val isShouldEnabledTime: Boolean

    var weekOffset: Int

    var isEditable: Boolean
}