package ru.alexkubrick.android.radioproject

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager

class RadioApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }
    private fun createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                RadioConstants.NOTIFICATION_CHANNEL_ID,
                "Radio Player Channel",
                NotificationManager.IMPORTANCE_LOW
            )
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
}