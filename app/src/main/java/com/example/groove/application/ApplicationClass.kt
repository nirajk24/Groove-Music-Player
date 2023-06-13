package com.example.groove.application

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class ApplicationClass : Application() {

    companion object{
        const val CHANNEL_ID_1 = "channel1"
        const val CHANNEL_ID_2 = "channel2"
        const val PLAY = "play"
        const val PAUSE = "pause"
        const val NEXT = "next"
        const val PREVIOUS = "previous"
        const val EXIT = "exit"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()

    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val notificationChannel1 = NotificationChannel(CHANNEL_ID_1, "Now Playing Song", NotificationManager.IMPORTANCE_HIGH)
            notificationChannel1.description = "This is a important channel for showing song!!"
            //for lockscreen -> test this and let me know.
            notificationChannel1.importance = NotificationManager.IMPORTANCE_HIGH
            notificationChannel1.lockscreenVisibility = Notification.VISIBILITY_PUBLIC

            val notificationChannel2 = NotificationChannel(CHANNEL_ID_2, "Channel2", NotificationManager.IMPORTANCE_HIGH)
            notificationChannel2.description = "Channel 2 Desc..."

            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel1)
            notificationManager.createNotificationChannel(notificationChannel2)
        }
    }
}