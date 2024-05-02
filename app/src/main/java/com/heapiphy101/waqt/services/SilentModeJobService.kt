package com.heapiphy101.waqt.services

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import androidx.core.app.JobIntentService

class SilentModeJobService : JobIntentService() {

    companion object {
        private const val JOB_ID = 1001
        const val EXTRA_START_SILENT_MODE = "extra_start_silent_mode"

        fun enqueueWork(context: Context, work: Intent) {
            enqueueWork(context, SilentModeJobService::class.java, JOB_ID, work)
        }
    }

    override fun onHandleWork(intent: Intent) {
        val startSilentMode = intent.getBooleanExtra(EXTRA_START_SILENT_MODE, false)

        if (startSilentMode) {
            setPhoneToSilentMode(applicationContext)
        } else {
            stopSilentMode(applicationContext)
        }
    }

    private fun setPhoneToSilentMode(context: Context?) {
        val audioManager = context?.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        // Save the current ringer mode to restore it later
        val savedRingerMode = audioManager.ringerMode

        // Set the phone to silent mode
        audioManager.ringerMode = AudioManager.RINGER_MODE_SILENT

        // Note: You may want to handle additional tasks related to silent mode
        // such as updating UI, logging, etc.

        // For example, you can update UI or show a notification
        // UpdateUIHelper.updateUI(context, "Silent Mode Enabled")

        // To restore the original ringer mode after a certain duration:
        // Handler(Looper.getMainLooper()).postDelayed({
        //     audioManager.ringerMode = savedRingerMode
        // }, 10 * 60 * 1000) // Restore after 10 minutes (adjust as needed)
    }

    private fun stopSilentMode(context: Context?) {
        val audioManager = context?.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        // Set the phone back to normal (non-silent) mode
        audioManager.ringerMode = AudioManager.RINGER_MODE_NORMAL

        // Note: You may want to handle additional tasks related to stopping silent mode
        // such as updating UI, logging, etc.

        // For example, you can update UI or show a notification
        // UpdateUIHelper.updateUI(context, "Silent Mode Disabled")
    }
}
