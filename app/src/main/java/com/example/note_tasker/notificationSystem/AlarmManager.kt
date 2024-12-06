package com.example.note_tasker.notificationSystem

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent

object NotificationScheduler {

    fun scheduleNotification(context: Context, taskTitle: String, taskId: Int, triggerTime: Long) {
        val intent = Intent(context, NotificationReceiver::class
            .java).apply {
            putExtra("task_title", taskTitle)
            putExtra("task_id", taskId)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            taskId,
            intent,
            PendingIntent.FLAG_MUTABLE  or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
    }
}
