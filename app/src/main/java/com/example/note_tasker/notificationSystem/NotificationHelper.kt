package com.example.note_tasker.notificationSystem

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.note_tasker.MainActivity
import com.example.note_tasker.R

class NotificationHelper(private val context: Context) {

    fun sendNotification(taskTitle: String, taskId: Int) {
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, taskId, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, NotificationChannelManager.CHANNEL_ID)
            .setSmallIcon(R.drawable.work_order)
            .setContentTitle("Recordatorio: $taskTitle")
            .setContentText("Tienes una tarea pendiente.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(taskId, notification)
    }
}
