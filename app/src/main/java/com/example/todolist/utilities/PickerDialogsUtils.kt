package com.example.todolist.utilities

import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.text.SimpleDateFormat
import java.util.*

fun Date.format(): String{
     return SimpleDateFormat("dd/MM/yy", Locale("pt", "BR")).format(this)
}

fun dateDialog(textInputLayout: TextInputLayout): MaterialDatePicker<Long> {
    val datePicker = MaterialDatePicker.Builder.datePicker().build()
    datePicker.addOnPositiveButtonClickListener {
        //adjust time zone
        val timeZone = TimeZone.getDefault()
        val offset = timeZone.getOffset(Date().time) * -1
        textInputLayout.text = Date(it+offset).format()
    }

    return datePicker
}

fun timeDialog(textInputLayout: TextInputLayout): MaterialTimePicker {
    val timePicker = MaterialTimePicker.Builder()
        .setTimeFormat(TimeFormat.CLOCK_24H)
        .build()

    timePicker.addOnPositiveButtonClickListener {

        val minutes = if(timePicker.minute in 0..9) "0${timePicker.minute}" else timePicker.minute.toString()
        val hours = if(timePicker.hour in 0..9) "0${timePicker.hour}" else timePicker.hour.toString()

        textInputLayout.text = "${hours}:${minutes}"
    }

    return timePicker
}