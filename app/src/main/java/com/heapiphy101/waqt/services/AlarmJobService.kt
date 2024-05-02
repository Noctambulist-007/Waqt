package com.heapiphy101.waqt.services

import android.content.Context
import android.content.Intent
import androidx.core.app.JobIntentService
import com.heapiphy101.waqt.activities.AlarmReceiver

class AlarmJobService : JobIntentService() {

    override fun onHandleWork(intent: Intent) {
        // Handle the work in the background
        // This method is executed on a background thread

        val context = applicationContext
        val alarmIntent = Intent(context, AlarmReceiver::class.java)

        // ... existing code ...

        // Schedule the next job
        AlarmScheduler.scheduleJob(context)
    }

    companion object {
        // Unique job ID for the JobIntentService
        private const val JOB_ID = 1000

        // Enqueue work to the JobIntentService
        fun enqueueWork(context: Context, work: Intent) {
            enqueueWork(context, AlarmJobService::class.java, JOB_ID, work)
        }
    }
}
