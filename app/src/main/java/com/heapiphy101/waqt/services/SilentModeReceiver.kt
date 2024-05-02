package com.heapiphy101.waqt.services

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings

class SilentModeReceiver : BroadcastReceiver() {
    companion object {
        const val ACTION_START_SILENT_MODE = "START_SILENT_MODE"
        const val ACTION_STOP_SILENT_MODE = "STOP_SILENT_MODE"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            ACTION_START_SILENT_MODE -> setPhoneToSilentMode(context)
            ACTION_STOP_SILENT_MODE -> stopSilentMode(context)
        }
    }

    private fun setPhoneToSilentMode(context: Context?) {
        // Your implementation for setting the phone to silent mode
        val notificationManager =
            context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !notificationManager.isNotificationPolicyAccessGranted) {
            // If DND access is not granted, request permission
            requestDndAccess(context)
        } else {
            // DND access is granted, so we can enable DND
            enableDndMode(notificationManager)
        }
    }

    private fun stopSilentMode(context: Context?) {
        // Your implementation for stopping silent mode
        val notificationManager =
            context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !notificationManager.isNotificationPolicyAccessGranted) {
            // If DND access is not granted, request permission
            requestDndAccess(context)
        } else {
            // DND access is granted, so we can disable DND
            disableDndMode(notificationManager)
        }
    }

    private fun requestDndAccess(context: Context?) {
        // Request DND access by opening the settings page
        val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)

        // Check if there's a context and the intent can be resolved
        if (context != null && intent.resolveActivity(context.packageManager) != null) {
            // Start the activity to open the notification policy access settings
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) // Add this line
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
