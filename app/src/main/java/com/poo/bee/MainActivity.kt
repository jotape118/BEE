package com.poo.bee

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //Seleccionar fecha
        val etDate=findViewById<EditText>(R.id.etDate)
        etDate.setOnClickListener{showDatePickerDialog()}

        //Seleccionar horas
        val etTimeStart=findViewById<EditText>(R.id.etTimeStart)
        etTimeStart.setOnClickListener(){showTimePickerDialog(resources.getIdentifier(etTimeStart.id.toString(),"id",packageName))}
        val etTimeEnd=findViewById<EditText>(R.id.etTimeEnd)
        etTimeEnd.setOnClickListener(){showTimePickerDialog(resources.getIdentifier(etTimeEnd.id.toString(),"id",packageName))}

        //Crear notificaciones
        val btnNotifier=findViewById<Button>(R.id.btnNotifier)
        btnNotifier.setOnClickListener{
            val timeInMillis = getTimeInMillis(etTimeStart.text.toString())
            if (timeInMillis !=   null) {
                scheduleNotification(timeInMillis)
            } else {
                Toast.makeText(this, "Por favor, ingresa una hora válida.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    //Mostrar seleccionar fecha
    private fun showDatePickerDialog() {
        val datePicker= DatePickerFragment { day, month, year -> onDaySelecter(day, month, year) }
        datePicker.show(supportFragmentManager,"datePicker")

    }
    //Cambio de texto de EditText en fecha
    fun onDaySelecter(day:Int,month:Int,Year:Int){
        val etDate=findViewById<EditText>(R.id.etDate)
        etDate.setText("$day/${month+1}/$Year")
    }
    //Mosstrar seleccionar hora
    private fun showTimePickerDialog(id: Int) {
        val timePicker=TimePickerFragment {time -> onTimeSelected(time,id) }
        timePicker.show(supportFragmentManager,"time")
    }

    //Cabio de texto de EditText en hora
    private fun onTimeSelected(time: String, id: Int) {
        val etTime=findViewById<EditText>(id)
        etTime.setText(time)
    }
    // Convertir el texto de hora en milisegundos
    private fun getTimeInMillis(time: String): Long? {
        return try {
            val calendar = Calendar.getInstance()
            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            val date = sdf.parse(time)

            if (date != null) {
                // Configura el calendario con la hora seleccionada
                calendar.time = date
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                calendar.timeInMillis
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    //Programar notificación con tiempo en milisegundos
    @SuppressLint("ScheduleExactAlarm")
    private fun scheduleNotification(timeInMillis: Long) {
        val intent = Intent(this, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)

        Toast.makeText(this, "Notificación programada", Toast.LENGTH_SHORT).show()
    }

}