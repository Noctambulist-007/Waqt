package com.heapiphy101.waqt.services

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import com.heapiphy101.waqt.activities.AlarmReceiver
import java.util.Calendar

class AlarmScheduler {

    companion object {
        // Set a one-time alarm for a specific time
        fun setAlarm(context: Context, hour: Int, minute: Int, requestCode: Int) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val alarmIntent = getPendingIntent(context, requestCode)

            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
            }

            // Use setExactAndAllowWhileIdle for precise alarms
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                alarmIntent
            )
        }

        // Set a repeating alarm (every hour in this case)
        fun setRepeatingAlarm(context: Context) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val alarmIntent = getPendingIntent(context, 0)

            // Use setRepeating for repeated alarms
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis(),
                AlarmManager.INTERVAL_HOUR, // Repeat every hour
                alarmIntent
            )
        }

        fun scheduleJob(context: Context) {
            val serviceComponent = ComponentName(context, AlarmJobService::class.java)
            val jobBuilder = JobInfo.Builder(0, serviceComponent)
                .setPeriodic(AlarmManager.INTERVAL_HOUR) // Repeat every hour
                .setPersisted(true)

            val jobScheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
            jobScheduler.schedule(jobBuilder.build())
        }

        // Cancel a previously set alarm
        fun cancelAlarm(context: Context, requestCode: Int) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val alarmIntent = getPendingIntent(context, requestCode)
            alarmManager.cancel(alarmIntent)
        }

        // Create a PendingIntent for the alarm
        private fun getPendingIntent(context: Context, requestCode: Int): PendingIntent {
            val intent = Intent(context, AlarmReceiver::class.java)
            return PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
    }
}
