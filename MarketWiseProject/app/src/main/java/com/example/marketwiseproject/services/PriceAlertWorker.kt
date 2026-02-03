package com.example.marketwiseproject.services


import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.marketwiseproject.R
import com.example.marketwiseproject.data.repository.CryptoRepository

class PriceAlertWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    private val repository = CryptoRepository()

    override suspend fun doWork(): Result {
        return try {
            val symbol = inputData.getString("symbol") ?: return Result.failure()
            val targetPrice = inputData.getDouble("targetPrice", 0.0)

            val prices = repository.getTopCryptos()
            val crypto = prices.find { it.symbol.equals(symbol, ignoreCase = true) }

            crypto?.let {
                if (it.price >= targetPrice) {
                    sendNotification(
                        "Price Alert: ${it.symbol}",
                        "Current price: $${it.price} reached target: $${targetPrice}"
                    )
                }
            }

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    private fun sendNotification(title: String, message: String) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "price_alerts",
                "Price Alerts",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, "price_alerts")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}