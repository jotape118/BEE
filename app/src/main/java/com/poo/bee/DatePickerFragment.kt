package com.poo.bee

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.icu.util.Calendar
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import com.google.android.material.animation.AnimatableView.Listener
import kotlin.time.Duration.Companion.days

class DatePickerFragment(val Listener: (day:Int,month:Int,year:Int) -> Unit):DialogFragment(),DatePickerDialog.OnDateSetListener {

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, day: Int) {
        Listener(day,month,year)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c= Calendar.getInstance()
        val day:Int =c.get(Calendar.DAY_OF_MONTH)
        val month:Int =c.get(Calendar.MONTH)
        val year:Int =c.get(Calendar.YEAR)

        val picker= DatePickerDialog(activity as Context,this,year,month,day)
        return  picker
    }

}