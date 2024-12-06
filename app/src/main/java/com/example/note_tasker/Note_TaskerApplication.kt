package com.example.note_tasker

import android.app.Application
import android.util.Log
import androidx.room.Room
import com.example.note_tasker.data.AppContainer
import com.example.note_tasker.data.AppContainerImpl
import com.example.note_tasker.notificationSystem.NotificationChannelManager
import com.example.note_tasker.notificationSystem.NotificationScheduler

class Note_TaskerApplication : Application(){
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainerImpl(this)
        NotificationChannelManager.createNotificationChannel(this)

        // Prueba para programar una notificación (5 segundos después)
        val triggerTime = System.currentTimeMillis() + 5000
        NotificationScheduler.scheduleNotification(this, "Tarea de prueba", 1, triggerTime)
    }
}

