package com.haura.uialarmapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.haura.uialarmapp.databinding.ActivityMainBinding
import com.haura.uialarmapp.room.Alarm
import com.haura.uialarmapp.room.AlarmDB
import kotlinx.android.synthetic.main.activity_one_time_alarm.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.MonthDay
import java.util.*

class OneTimeAlarmActivity : AppCompatActivity(),View.OnClickListener, DatePickerFragment.DialogDateListener, TimePickerFragment.DialogTimeListener{

    private var binding: ActivityMainBinding? = null
    private lateinit var alarmReceiver: AlarmReceiver
    val db by lazy {AlarmDB (this)}


    companion object {
        private const val DATE_PICKER_TAG = "DatePicker"
        private const val TIME_PICKER_ONCE_TAG = "TimePickerOnce"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_one_time_alarm)

        btn_set_date_one_time.setOnClickListener(this)
        btn_set_time_one_time.setOnClickListener(this)
        btn_add_set_one_time_alarm.setOnClickListener(this)
        btn_cancel_set_one_time_alarm.setOnClickListener(this)

        alarmReceiver = AlarmReceiver()
    }

    override fun onClick(v: View) {
        when(v.id){
            R.id.btn_set_date_one_time -> {
                val datePickerFragment = DatePickerFragment()
                datePickerFragment.show(supportFragmentManager, DATE_PICKER_TAG)
            }

            R.id.btn_set_time_one_time -> {
                val timePickerFragmentOneTime = TimePickerFragment()
                timePickerFragmentOneTime.show(supportFragmentManager, TIME_PICKER_ONCE_TAG)
            }

            R.id.btn_add_set_one_time_alarm -> {
                val onceDate = tv_once_date.text.toString()
                val onceTime = tv_once_time.text.toString()
                val onceMessage = et_note_one_time.text.toString()

                CoroutineScope(Dispatchers.IO).launch {
                    db.alarmDao().addAlarm(
                        Alarm(0, onceTime, onceDate, onceMessage)
                    )

                    finish()
                }

                alarmReceiver.setOneTimeAlarm(this, AlarmReceiver.TYPE_ONE_TIME,
                    onceDate,
                    onceTime,
                    onceMessage)


            }

            R.id.btn_cancel_set_one_time_alarm -> {
                alarmReceiver.cancelAlarmOneTime(this, AlarmReceiver.TYPE_ONE_TIME)
            }
        }
    }


    override fun onDialogDateSet(tag: String?, year: Int, month: Int, dayOfMonth: Int) {
        // Siapkan date formatter-nya terlebih dahulu
        val calendar = Calendar.getInstance()
        calendar.set(year, month, dayOfMonth)
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

        // Set text dari textview once
        tv_once_date.text = dateFormat.format(calendar.time)
    }

    override fun onDialogTimeSet(tag: String?, hourOfDay: Int, minute: Int) {
        // Siapkan time formatter-nya terlebih dahulu
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        calendar.set(Calendar.MINUTE, minute)

        val dateFormatOneTime = SimpleDateFormat("HH:mm", Locale.getDefault())

        // Set text dari textview berdasarkan tag
        when (tag) {
            TIME_PICKER_ONCE_TAG -> tv_once_time.text = dateFormatOneTime.format(calendar.time)
            else -> {
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        binding = null
    }

}