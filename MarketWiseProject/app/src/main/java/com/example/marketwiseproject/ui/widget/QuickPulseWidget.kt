package com.example.marketwiseproject.ui.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import androidx.core.content.ContextCompat
import androidx.work.*
import com.example.marketwiseproject.R
import com.example.marketwiseproject.data.models.CryptoPrice
import java.text.NumberFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

class QuickPulseWidget : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // 1. Schedule periodic updates
        val workRequest = PeriodicWorkRequestBuilder<WidgetUpdateWorker>(
            15, TimeUnit.MINUTES
        ).build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "widget_update",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )

        // 2. Trigger an immediate update (OneTimeWorkRequest)
        val immediateRequest = OneTimeWorkRequestBuilder<WidgetUpdateWorker>()
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .build()
        WorkManager.getInstance(context).enqueue(immediateRequest)

        // 3. Update all widgets with loading state initially
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, null)
        }
    }

    companion object {
        private val currencyFormat = NumberFormat.getCurrencyInstance(Locale.US)

        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int,
            crypto: CryptoPrice?
        ) {
            val views = RemoteViews(context.packageName, R.layout.widget_quick_pulse)

            if (crypto != null) {
                // Update widget with real data
                views.setTextViewText(R.id.widget_symbol, "${crypto.symbol}/USDT")
                views.setTextViewText(R.id.widget_price, currencyFormat.format(crypto.price))

                val isPositive = crypto.changePercent24h >= 0
                val changeText = String.format(
                    "%s%.2f%%",
                    if (isPositive) "+" else "",
                    crypto.changePercent24h
                )
                views.setTextViewText(R.id.widget_change, changeText)
                views.setTextColor(
                    R.id.widget_change,
                    ContextCompat.getColor(
                        context,
                        if (isPositive) R.color.positive_muted else R.color.negative_muted
                    )
                )
            } else {
                // Loading state
                views.setTextViewText(R.id.widget_symbol, "Loading...")
                views.setTextViewText(R.id.widget_price, "---")
                views.setTextViewText(R.id.widget_change, "...")
            }

            views.setTextViewText(R.id.widget_update_time, "Updated: ${getCurrentTime()}")
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        private fun getCurrentTime(): String {
            val cal = java.util.Calendar.getInstance()
            return String.format("%02d:%02d", cal.get(java.util.Calendar.HOUR_OF_DAY), cal.get(java.util.Calendar.MINUTE))
        }
    }
}