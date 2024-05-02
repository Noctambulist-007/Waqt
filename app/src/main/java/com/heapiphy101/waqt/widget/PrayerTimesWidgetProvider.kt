package com.heapiphy101.waqt.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.RemoteViews
import com.heapiphy101.waqt.data.PrayerTimesData
import com.heapiphy101.waqt.R

class PrayerTimesWidgetProvider : AppWidgetProvider() {

    private val handler = Handler(Looper.getMainLooper())
    private val updateIntervalMillis = 1000 // 1 second

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
        // Schedule periodic updates
        handler.postDelayed({
            onUpdate(context, appWidgetManager, appWidgetIds)
        }, updateIntervalMillis.toLong())
    }

    companion object {
        internal fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val views = RemoteViews(context.packageName, R.layout.prayer_times_widget)

            // Fetch prayer times from shared preferences
            val prayerTimesData = getPrayerTimesDataFromSharedPreferences(context)

            // Update widget UI with prayer times
            views.setTextViewText(R.id.widget_fajartime, prayerTimesData.fajr)
            views.setTextViewText(R.id.widget_juhartime, prayerTimesData.dhuhr)
            views.setTextViewText(R.id.widget_asartime, prayerTimesData.asr)
            views.setTextViewText(R.id.widget_magribtime, prayerTimesData.maghrib)
            views.setTextViewText(R.id.widget_ishatime, prayerTimesData.isha)

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        private fun getPrayerTimesDataFromSharedPreferences(context: Context): PrayerTimesData {
            val sharedPreferences = context.getSharedPreferences("PrayerTimes", Context.MODE_PRIVATE)
            return PrayerTimesData(
                "Date",
                sharedPreferences.getString("fajr", "") ?: "",
                sharedPreferences.getString("dhuhr", "") ?: "",
                sharedPreferences.getString("asr", "") ?: "",
                sharedPreferences.getString("maghrib", "") ?: "",
                sharedPreferences.getString("isha", "") ?: "",
                sharedPreferences.getString("shurooq", "") ?: ""
            )
        }
    }
}