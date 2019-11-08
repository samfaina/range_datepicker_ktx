package dev.samfaina.rangedatepickersample

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import dev.samfaina.range_datepicker_ktx.customviews.DateRangeCalendarView
import dev.samfaina.range_datepicker_ktx.customviews.DateRangeCalendarViewApi
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import java.util.*

class MainActivity : AppCompatActivity() {

    lateinit var calendar: DateRangeCalendarView
    lateinit var button: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        calendar = findViewById(R.id.calendar)
        button = findViewById(R.id.button)


        calendar.setDisabledDates(
            mutableListOf(
                GregorianCalendar(2019, 10, 1) as Calendar,
                GregorianCalendar(2019, 10, 2) as Calendar,
                GregorianCalendar(2019, 10, 12) as Calendar
            )
        )
        calendar.setCalendarListener(object : DateRangeCalendarViewApi.CalendarListener {
            override fun onFirstDateSelected(startDate: Calendar) {

            }

            override fun onDateRangeSelected(startDate: Calendar, endDate: Calendar) {
                Log.d(
                    MainActivity::class.java.simpleName,
                    LocalDate.of(
                        startDate.get(Calendar.YEAR),
                        startDate.get(Calendar.MONTH),
                        startDate.get(Calendar.DAY_OF_MONTH)
                    ).format(
                        DateTimeFormatter.ofPattern("yyyy-MM-dd")
                    )
                )

                Log.d(
                    MainActivity::class.java.simpleName,
                    LocalDate.of(
                        endDate.get(Calendar.YEAR),
                        endDate.get(Calendar.MONTH),
                        endDate.get(Calendar.DAY_OF_MONTH)
                    ).format(
                        DateTimeFormatter.ofPattern("yyyy-MM-dd")
                    )
                )
            }

            override fun onDisabledDateSelected(disabledDate: Calendar) {
                val builder = AlertDialog.Builder(this@MainActivity)
                builder.apply {
                    setMessage("Do you want to delete this holiday day?")
                    setTitle("Watch out!!!")
                    setPositiveButton(
                        "DELETE"
                    ) { _, _ ->
                        calendar.deleteDisabledDate(disabledDate)
                    }
                    setNegativeButton(
                        "CANCEL"
                    ) { dialog, id ->
                        // User cancelled the dialog
                    }
                }

                val dialog = builder.create()


                dialog.show()
            }
        })

        button.setOnClickListener {
            val list = calendar.getSelectedDateList()
            Toast.makeText(this, "${list.size} days selected", Toast.LENGTH_LONG).show()
            Log.d(MainActivity::class.java.simpleName, list.toString())
        }

    }
}
