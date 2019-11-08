package dev.samfaina.range_datepicker_ktx.timepicker


import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.Window
import android.view.WindowManager
import android.widget.TimePicker
import dev.samfaina.range_datepicker_ktx.R
import dev.samfaina.range_datepicker_ktx.customviews.CustomTextView
import java.util.*


class AwesomeTimePickerDialog(
    context: Context,
    private val mTitle: String,
    private val onTimeChangedListener: TimePickerCallback?
) : Dialog(context) {

    private var tvHeaderTitle: CustomTextView? = null
    private var tvDialogDone: CustomTextView? = null
    private var tvDialogCancel: CustomTextView? = null
    private var hours: Int = 0
    private var minutes: Int = 0

    private var timePicker: TimePicker? = null

    interface TimePickerCallback {
        fun onTimeSelected(hours: Int, mins: Int)

        fun onCancel()
    }

    init {

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
        window?.setGravity(Gravity.BOTTOM)
        setCanceledOnTouchOutside(false)
        initView()
        setListeners()

        //Grab the window of the dialog, and change the width
        val lp = WindowManager.LayoutParams()
        val window = this.window
        lp.copyFrom(window.attributes)
        //This makes the dialog take up the full width
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        window?.attributes = lp
    }

    private fun initView() {

        setContentView(R.layout.dialog_time_picker)

        tvHeaderTitle = findViewById<CustomTextView>(R.id.tvHeaderTitle)
        tvDialogDone = findViewById<CustomTextView>(R.id.tvHeaderDone)
        tvDialogCancel = findViewById<CustomTextView>(R.id.tvHeaderCancel)

        timePicker = findViewById<TimePicker>(R.id.timePicker)
        timePicker!!.setOnTimeChangedListener { _, i, i1 ->
            hours = i
            minutes = i1
        }

        tvHeaderTitle!!.text = mTitle

    }

    private fun setListeners() {

        tvDialogCancel!!.setOnClickListener {
            onTimeChangedListener?.onCancel()
            this@AwesomeTimePickerDialog.dismiss()
        }

        tvDialogDone!!.setOnClickListener {
            onTimeChangedListener?.onTimeSelected(hours, minutes)
            this@AwesomeTimePickerDialog.dismiss()
        }

    }

    fun showDialog() {

        hours = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        minutes = Calendar.getInstance().get(Calendar.MINUTE)
        this.show()
    }

    companion object {

        private val LOG_TAG = AwesomeTimePickerDialog::class.java.simpleName
    }

}