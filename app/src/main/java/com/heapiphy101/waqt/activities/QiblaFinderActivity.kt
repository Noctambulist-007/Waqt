package com.heapiphy101.waqt.activities

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.res.ResourcesCompat
import com.heapiphy101.waqt.R
import com.heapiphy101.waqt.databinding.ActivityQiblaFinderBinding
import com.heapiphy101.waqt.helper.CompassQibla
import com.heapiphy101.waqt.helper.LocaleHelper
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.util.Calendar
import java.util.Date
import java.util.Locale

class QiblaFinderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQiblaFinderBinding
    private var currentCompassDegree = 0f
    private var currentNeedleDegree = 0f

    private lateinit var sharedPreferencesThemeUpdate: SharedPreferences
    private val handler = Handler(Looper.getMainLooper())
    private val updateIntervalMillis = 1000L


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQiblaFinderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val savedLanguage = LocaleHelper.getSavedLanguage(this)

        // Apply the stored language if available
        if (!savedLanguage.isNullOrEmpty()) {
            LocaleHelper.updateLocale(this, savedLanguage)
        }


        sharedPreferencesThemeUpdate = getSharedPreferences("PrayerTimes", MODE_PRIVATE)

        // Add the shared preferences listener
        sharedPreferencesThemeUpdate.registerOnSharedPreferenceChangeListener(sharedPreferencesListener)

        // Schedule periodic updates
        handler.post(updateThemeRunnable)

        window.navigationBarColor = ResourcesCompat.getColor(
            resources,
            R.color.black,
            theme
        )

        window.statusBarColor = ResourcesCompat.getColor(
            resources,
            R.color.black,
            theme
        )

        binding.backButtonQiblaCompass.setOnClickListener {
            onBackPressed()
        }

        CompassQibla.Builder(this).onPermissionGranted { permission ->
            Toast.makeText(this, "onPermissionGranted $permission", Toast.LENGTH_SHORT).show()
        }.onPermissionDenied {
            Toast.makeText(this, "onPermissionDenied", Toast.LENGTH_SHORT).show()
        }.onGetLocationAddress { address ->
            binding.tvLocation.text = buildString {
                append(address.locality)
                append(", ")
                append(address.subAdminArea)
            }
        }.onDirectionChangeListener { qiblaDirection ->
            binding.tvDirection.text = if (qiblaDirection.isFacingQibla) {
                getString(R.string.facing_qibla_message)
            } else "${qiblaDirection.needleAngle.toInt()}°"

            val rotateCompass = RotateAnimation(
                currentCompassDegree, qiblaDirection.compassAngle, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f
            ).apply {
                duration = 1000
            }
            currentCompassDegree = qiblaDirection.compassAngle

            binding.ivCompass.startAnimation(rotateCompass)

            val rotateNeedle = RotateAnimation(
                currentNeedleDegree, qiblaDirection.needleAngle, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f
            ).apply {
                duration = 1000
            }
            currentNeedleDegree = qiblaDirection.needleAngle

            binding.ivNeedle.startAnimation(rotateNeedle)
        }.build()
    }

    private val updateThemeRunnable = object : Runnable {
        @RequiresApi(Build.VERSION_CODES.O)
        override fun run() {
            updateTheme()
            handler.postDelayed(this, updateIntervalMillis)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private val sharedPreferencesListener =
        SharedPreferences.OnSharedPreferenceChangeListener { _, _ ->
            // Update the theme when shared preferences change
            updateTheme()
        }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun isBetween(current: LocalTime?, start: LocalTime?, end: LocalTime?): Boolean {
        return current != null && start != null && end != null && current.isAfter(start) && current.isBefore(
            end
        )
    }

    private fun getCurrentTime(): String {
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val currentTime: Date = Date()
        return sdf.format(currentTime)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateTheme() {
        val sharedPreferencesPrayerTimes = getSharedPreferences("PrayerTimes", Context.MODE_PRIVATE)
        val fajr = sharedPreferencesPrayerTimes.getString("fajr", "")
        val sunrise = sharedPreferencesPrayerTimes.getString("shurooq", "")
        val dhuhr = sharedPreferencesPrayerTimes.getString("dhuhr", "")
        val asr = sharedPreferencesPrayerTimes.getString("asr", "")
        val maghrib = sharedPreferencesPrayerTimes.getString("maghrib", "")
        val isha = sharedPreferencesPrayerTimes.getString("isha", "")

        val currentTime = getCurrentTime()


        fun convertTo24Hour(time: String?): LocalTime? {
            if (time.isNullOrEmpty()) {
                return null
            }

            val sdf12Hour = SimpleDateFormat("hh:mm a", Locale.getDefault())
            val date: Date? = sdf12Hour.parse(time)
            return if (date != null) {
                val cal = Calendar.getInstance()
                cal.time = date
                LocalTime.of(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE))
            } else {
                null
            }
        }

        val formattedCurrentTime = convertTo24Hour(currentTime)
        val formattedFajr = convertTo24Hour(fajr)
        val formattedSunrise = convertTo24Hour(sunrise)
        val formattedDhuhr = convertTo24Hour(dhuhr)
        val formattedAsr = convertTo24Hour(asr)
        val formattedMaghrib = convertTo24Hour(maghrib)
        val formattedIsha = convertTo24Hour(isha)

        if (isBetween(formattedCurrentTime, formattedIsha, formattedFajr) ||
            isBetween(formattedCurrentTime, formattedIsha, LocalTime.MAX) ||
            isBetween(formattedCurrentTime, LocalTime.MIN, formattedFajr)
        ) {
            binding.layoutQiblaFinder.setBackgroundResource(R.drawable.gradient_background_isha)
        } else if (isBetween(formattedCurrentTime, formattedFajr, formattedSunrise)) {
            binding.layoutQiblaFinder.setBackgroundResource(R.drawable.gradient_background_fajr)
        } else if (isBetween(
                formattedCurrentTime,
                formattedDhuhr,
                formattedAsr
            )
        ) {
            binding.layoutQiblaFinder.setBackgroundResource(R.drawable.gradient_background_juhar)
        } else if (isBetween(formattedCurrentTime, formattedAsr, formattedMaghrib)) {
            binding.layoutQiblaFinder.setBackgroundResource(R.drawable.gradient_background_asr)
        } else if (isBetween(formattedCurrentTime, formattedMaghrib, formattedIsha)) {
            binding.layoutQiblaFinder.setBackgroundResource(R.drawable.gradient_background_maghrib)
        } else {
            binding.layoutQiblaFinder.setBackgroundResource(R.drawable.gradient_background_normal)
        }
    }




    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.fade_in_reverse, R.anim.fade_out_reverse)
    }
}