package com.poo.bee

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        // Llama a la función para mostrar la notificación
        showNotification(context)
    }

    private fun showNotification(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Configura el canal de notificación para Android 8.0 y superiores
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("channel_id", "Notificaciones", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, "channel_id")
            .setContentTitle("Recordatorio")
            .setContentText("Es hora de hacer tu tarea programada")
            .setSmallIcon(R.drawable.ic_message)  // Asegúrate de tener un ícono de notificación
            .build()

        notificationManager.notify(1, notification)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "default_channel"
            val channelName = "Default Notifications"
            val channelDescription = "Channel for important notifications"

            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH).apply {
                description = channelDescription
            }

            // Crear el canal si no existe
            notificationManager.createNotificationChannel(channel)

            Log.d("Notification", "Notification channel created")
        }

    }
}
