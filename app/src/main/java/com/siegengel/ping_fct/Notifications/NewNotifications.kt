package com.siegengel.ping_fct.Notifications

import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.net.Uri


class NewNotifications(base: Context?) : ContextWrapper(base) {
    private var notificationManager: NotificationManager? = null

    init {
        createChannel()
    }

    private fun createChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.enableLights(false)
        channel.enableVibration(true)
        channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE

        manager!!.createNotificationChannel(channel)
    }

    val manager: NotificationManager?
        get() {
            if (notificationManager == null) {
                notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            }

            return notificationManager
        }

    fun getNotification(
        title: String?, body: String?,
        pendingIntent: PendingIntent?, soundUri: Uri?, icon: String
    ): Notification.Builder {
        return Notification.Builder(applicationContext, CHANNEL_ID)
            .setContentIntent(pendingIntent)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(icon.toInt())
            .setSound(soundUri)
            .setAutoCancel(true)
    }
    companion object {
        private const val CHANNEL_ID = "com.ping.oliv"
        private const val CHANNEL_NAME = "oliv"
    }
}