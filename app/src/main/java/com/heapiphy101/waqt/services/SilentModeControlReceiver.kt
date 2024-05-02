package com.heapiphy101.waqt.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.app.NotificationManager

class SilentModeControlReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            SilentModeReceiver.ACTION_START_SILENT_MODE -> startJobService(context, true)
            SilentModeReceiver.ACTION_STOP_SILENT_MODE -> startJobService(context, false)
        }
    }

    private fun startJobService(context: Context?, startSilentMode: Boolean) {
        val serviceIntent = Intent(context, SilentModeJobService::class.java)
        serviceIntent.putExtra(SilentModeJobService.EXTRA_START_SILENT_MODE, startSilentMode)

        if (context != null) {
            SilentModeJobService.enqueueWork(context, serviceIntent)
        }
    }

    private fun startSilentModeService(context: Context?) {
        val serviceIntent = Intent(context, SilentModeService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context?.startForegroundService(serviceIntent)
        } else {
            context?.startService(serviceIntent)
        }
    }

    private fun stopSilentModeService(context: Context?) {
        val serviceIntent = Intent(context, SilentModeService::class.java)
        context?.stopService(serviceIntent)
    }

    private fun requestDndAccess(context: Context?) {
        // Request DND access by opening the settings page
        val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)

        // Check if there's a context and the intent can be resolved
        if (context != null && intent.resolveActivity(context.packageManager) != null) {
            // Start the activity to open the notification policy access settings
            context.startActivity(intent)
        } else {
            // Handle the case where the intent cannot be resolved, e.g., settings not available
            // You may want to provide a message to the user or handle it in your app logic
        }
    }

    private fun enableDndMode(notificationManager: NotificationManager) {
        // Enable Do Not Disturb mode
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALARMS)
        } else {
            @Suppress("DEPRECATION")
            notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL)
        }
    }

    private fun disableDndMode(notificationManager: NotificationManager) {
        // Disable Do Not Disturb mode
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL)
        } else {
            @Suppress("DEPRECATION")
            notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL)
        }
    }
}
