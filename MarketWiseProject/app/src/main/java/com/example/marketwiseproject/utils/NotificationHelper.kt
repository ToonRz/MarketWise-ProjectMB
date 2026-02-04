package com.example.marketwiseproject.utils

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.marketwiseproject.R

class NotificationHelper(private val context: Context) {

    companion object {
        private const val CHANNEL_ID = "marketwise_channel"
        private const val CHANNEL_NAME = "MarketWise Updates"
        private const val NOTIFICATION_ID = 1
    }

    init {
        createNotificationChannel()
    }

    /**
     * Creates a notification channel for Android 8.0 (Oreo) and above.
     * This needs to be called once when the app starts.
     */
    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is not in the Support Library.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = "Notifications for market data and alerts."
            }
            // Register the channel with the system.
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Builds and sends a notification.
     *
     * @param title The title of the notification.
     * @param content The main text content of the notification.
     */
    fun sendNotification(title: String, content: String) {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher_round) // Using the app'''s round icon
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true) // Dismiss the notification when the user taps on it

        with(NotificationManagerCompat.from(context)) {
            // Before calling notify(), you must check for POST_NOTIFICATIONS permission on Android 13+
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Here, you would typically request the permission.
                // For this helper, we just log or return if permission is not granted.
                // The actual permission request should be handled in the Activity or Fragment.
                return
            }
            notify(NOTIFICATION_ID, builder.build())
        }
    }
}
