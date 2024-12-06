package com.example.note_tasker.notificationSystem
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null && intent != null) {

            val taskTitle = intent.getStringExtra("task_title") ?: "Sin título"  // Cambié el nombre del extra aquí
            val taskId = intent.getIntExtra("task_id", 0)

            NotificationHelper(context).sendNotification(taskTitle, taskId)
        }
    }
}
