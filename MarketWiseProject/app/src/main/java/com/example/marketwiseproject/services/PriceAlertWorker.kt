package com.example.marketwiseproject.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.marketwiseproject.R
import com.example.marketwiseproject.data.db.AppDatabase
import com.example.marketwiseproject.data.repository.CryptoRepository
import com.example.marketwiseproject.data.repository.StockRepository
import kotlinx.coroutines.flow.first

class PriceAlertWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    private val cryptoRepository = CryptoRepository()
    private val stockRepository = StockRepository() // Assumes default constructor works or I'll fix it
    private val database = AppDatabase.getDatabase(context)

    override suspend fun doWork(): Result {
        return try {
            val alerts = database.priceAlertDao().getAllAlerts().first()
            
            for (alert in alerts) {
                if (!alert.isEnabled) continue

                // Check Crypto
                val cryptos = cryptoRepository.getTopCryptos()
                val crypto = cryptos.find { it.symbol.equals(alert.symbol, ignoreCase = true) }
                
                if (crypto != null) {
                    checkAndNotify(alert.symbol, crypto.price, alert.targetPrice, alert.isAbove)
                } else {
                    // Check Stock
                    val stockDetails = stockRepository.getStockDetails(alert.symbol)
                    stockDetails.onSuccess { details ->
                        checkAndNotify(alert.symbol, details.currentPrice, alert.targetPrice, alert.isAbove)
                    }
                }
            }

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    private fun checkAndNotify(symbol: String, currentPrice: Double, targetPrice: Double, isAbove: Boolean) {
        val triggered = if (isAbove) {
            currentPrice >= targetPrice
        } else {
            currentPrice <= targetPrice
        }

        if (triggered) {
            sendNotification(
                "Price Alert: $symbol",
                "Current price: $$currentPrice reached target: $$targetPrice"
            )
            // Optional: disable alert after trigger? 
            // database.priceAlertDao().toggleAlert(alert.id, false)
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
            .setSmallIcon(R.mipmap.ic_launcher) // Fallback icon
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}