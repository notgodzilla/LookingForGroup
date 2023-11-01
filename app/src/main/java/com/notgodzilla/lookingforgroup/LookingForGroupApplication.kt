package com.notgodzilla.lookingforgroup

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build.*
import com.notgodzilla.lookingforgroup.preferences.PreferencesRepository


const val NOTIFICATION_CHANNEL_ID = "PF_poll"

class LookingForGroupApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        PreferencesRepository.initialize(this)
        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            val name = getString(R.string.notification_channel_name)
            val importance = NotificationManager.IMPORTANCE_DEFAULT

            val channel =
                NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance)
            val notificationManager: NotificationManager =
                getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
}