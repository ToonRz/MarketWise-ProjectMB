package com.example.marketwiseproject.ui.widget


import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.marketwiseproject.data.repository.CryptoRepository

class WidgetUpdateWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val repository = CryptoRepository()
            val prices = repository.getTopCryptos()

            // Update widget with latest price
            val appWidgetManager = AppWidgetManager.getInstance(applicationContext)
            val widgetComponent = ComponentName(applicationContext, QuickPulseWidget::class.java)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(widgetComponent)

            for (appWidgetId in appWidgetIds) {
                QuickPulseWidget.updateAppWidget(
                    applicationContext,
                    appWidgetManager,
                    appWidgetId
                )
            }

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}