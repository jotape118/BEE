package com.poo.bee

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import android.Manifest
import android.app.NotificationManager
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


class MainActivity : AppCompatActivity() {
    private var selectedDate: Calendar? = null
    private val REQUEST_CODE_PERMISSION = 1001

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Verifica y solicita el permiso para establecer alarmas exactas
        requestExactAlarmPermission()

        //Pruebas
        val intent = Intent(this, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        // Forzar el disparo del PendingIntent manualmente para probar la notificación
        pendingIntent.send()

        // Log para saber que se ejecutó
        Log.d("PendingIntent", "PendingIntent executed")



        //Seleccionar fecha
        val etDate=findViewById<EditText>(R.id.etDate)
        etDate.setOnClickListener{showDatePickerDialog()}

        //Seleccionar horas
        val etTimeStart=findViewById<EditText>(R.id.etTimeStart)
        etTimeStart.setOnClickListener(){showTimePickerDialog(etTimeStart.id)}
        val etTimeEnd=findViewById<EditText>(R.id.etTimeEnd)
        etTimeEnd.setOnClickListener(){showTimePickerDialog(etTimeEnd.id)}

        //Crear notificaciones
        val btnNotifier=findViewById<Button>(R.id.btnNotifier)
        btnNotifier.setOnClickListener{
            if (etDate.text.isNullOrEmpty()) {
                Toast.makeText(this, "Por favor, selecciona una fecha válida.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val timeInMillis = getTimeInMillis(etTimeStart.text.toString(),etDate.text.toString())
            if (timeInMillis !=   null) {
                scheduleNotification(timeInMillis)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    if (notificationManager.areNotificationsEnabled()) {
                        Log.d("Notification", "Notifications are enabled")
                    } else {
                        Log.d("Notification", "Notifications are disabled")
                        // Informar al usuario para que habilite las notificaciones
                        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                            putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                        }
                        startActivity(intent)
                    }
                }

            } else {
                Toast.makeText(this, "Por favor, ingresa una hora válida.", Toast.LENGTH_SHORT).show()
            }
        }
    }
    //Solicitar el permiso de notificaciones
    @RequiresApi(Build.VERSION_CODES.S)
    private fun requestExactAlarmPermission() {

        val permissionStatus = ContextCompat.checkSelfPermission(
            this, Manifest.permission.SCHEDULE_EXACT_ALARM
        )

        if (permissionStatus != PackageManager.PERMISSION_GRANTED) {
            // Si no se ha concedido el permiso, solicítalo
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.SCHEDULE_EXACT_ALARM),
                REQUEST_CODE_PERMISSION
            )
        }
    }

    //Verificar el permiso de notificaciones
    private fun isExactAlarmPermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            checkSelfPermission(Manifest.permission.SCHEDULE_EXACT_ALARM) == PackageManager.PERMISSION_GRANTED
        } else {
            true // En versiones anteriores, no es necesario el permiso
        }
    }


    //Mostrar seleccionar fecha
    private fun showDatePickerDialog() {
        val datePicker= DatePickerFragment { day, month, year -> onDaySelecter(day, month, year) }
        datePicker.show(supportFragmentManager,"datePicker")

    }
    //Cambio de texto de EditText en fecha
    fun onDaySelecter(day:Int,month:Int,year:Int){
        selectedDate = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, day)
            set(Calendar.MONTH, month)
            set(Calendar.YEAR, year)
        }
        val etDate=findViewById<EditText>(R.id.etDate)
        etDate.setText("$day/${month+1}/$year")
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
    private fun getTimeInMillis(time: String, selectedDate: String): Long? {
        return try {
            // Parsear la hora
            val sdfTime = SimpleDateFormat("HH:mm", Locale.getDefault())
            val date = sdfTime.parse(time)

            // Parsear la fecha
            val sdfDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val dateSelected = sdfDate.parse(selectedDate)

            if (date != null && dateSelected != null) {
                // Combinar la fecha y la hora seleccionada
                val calendar = Calendar.getInstance()
                calendar.time = dateSelected
                calendar.set(Calendar.HOUR_OF_DAY, date.hours)
                calendar.set(Calendar.MINUTE, date.minutes)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)

                // Retornar el tiempo en milisegundos
                calendar.timeInMillis
            } else {
                null
            }
        } catch (e: Exception) { e.printStackTrace()
            null
        }
    }
    //Programar notificación con tiempo en milisegundos
    @SuppressLint("ScheduleExactAlarm")
    private fun scheduleNotification(timeInMillis: Long) {
        val intent = Intent(this, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Establece la alarma exacta
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeInMillis+5000, pendingIntent)
        Toast.makeText(this, "Notificación programada", Toast.LENGTH_SHORT).show()

        //Logs de verificacion
        Log.d("Alarm", "Current time: ${System.currentTimeMillis()}")
        Log.d("Alarm", "Scheduled time: $timeInMillis")
    }

}