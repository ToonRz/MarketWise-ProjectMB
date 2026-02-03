package com.example.marketwiseproject.ui.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import androidx.work.*
import com.example.marketwiseproject.R
import java.util.concurrent.TimeUnit

class QuickPulseWidget : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // Schedule periodic updates
        val workRequest = PeriodicWorkRequestBuilder<WidgetUpdateWorker>(
            15, TimeUnit.MINUTES
        ).build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "widget_update",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )

        // Update all widgets
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    companion object {
        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val views = RemoteViews(context.packageName, R.layout.widget_quick_pulse)

            // Update widget views
            views.setTextViewText(R.id.widget_symbol, "BTC/USDT")
            views.setTextViewText(R.id.widget_price, "$43,250.50")
            views.setTextViewText(R.id.widget_change, "↗️ +2.4%")
            views.setTextViewText(R.id.widget_update_time, "Updated: ${getCurrentTime()}")

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        private fun getCurrentTime(): String {
            val cal = java.util.Calendar.getInstance()
            return String.format("%02d:%02d", cal.get(java.util.Calendar.HOUR_OF_DAY), cal.get(java.util.Calendar.MINUTE))
        }
    }
}