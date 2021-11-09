package com.sample.collathon_practice

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import android.widget.TextView
import android.widget.TimePicker
import androidx.appcompat.app.ActionBar
import kotlinx.android.synthetic.main.activity_add.*
import java.text.SimpleDateFormat
import java.util.*

class AddActivity : AppCompatActivity() {

    var textview_date: TextView? = null
    var cal = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        // action bar hide
        var actionBar : ActionBar?

        actionBar = supportActionBar;
        actionBar?.hide();

        // calendar
        textview_date = this.date
        val year = cal.get(Calendar.YEAR).toString()
        val mon = (cal.get(Calendar.MONTH) + 1).toString()
        val day = cal.get(Calendar.DAY_OF_MONTH).toString()
        val hour = cal.get(Calendar.HOUR_OF_DAY).toString()
        val min = cal.get(Calendar.MINUTE).toString()
        textview_date!!.text = year + "/" + mon + "/" + day + "\t" + hour + ":" + min

        // create an OnDateSetListener
        val dateSetListener = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int,
                                   dayOfMonth: Int) {
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.YEAR, year)
                updateDateInView()
            }
        }

        val timeSetListener = object : TimePickerDialog.OnTimeSetListener {
            override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
                cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
                cal.set(Calendar.MINUTE, minute)
                updateDateInView()
            }
        }

        // when you click on the button, show DatePickerDialog that is set with OnDateSetListener
        textview_date!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                DatePickerDialog(this@AddActivity,
                    dateSetListener,
                    // set DatePickerDialog to point to today's date when it loads up
                    cal.get(Calendar.DAY_OF_MONTH),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.YEAR)).apply{
                    datePicker.minDate = System.currentTimeMillis()
                }.show()
                TimePickerDialog(this@AddActivity,
                    timeSetListener,
                    cal.get(Calendar.HOUR_OF_DAY),
                    cal.get(Calendar.MINUTE), true).show()
            }
        })
    }

    private fun updateDateInView() {
        val myFormat = "yyyy/MM/dd \t hh/mm" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        textview_date!!.text = sdf.format(cal.getTime())
    }
}