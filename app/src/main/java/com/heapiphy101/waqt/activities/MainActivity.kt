package com.heapiphy101.waqt.activities

import android.Manifest
import android.animation.ValueAnimator
import android.app.Activity
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Color
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.ParseException
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.bumptech.glide.Glide
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.heapiphy101.waqt.R
import com.heapiphy101.waqt.data.PrayerTimesData
import com.heapiphy101.waqt.data.PrayerTimesService
import com.heapiphy101.waqt.databinding.ActivityMainBinding
import com.heapiphy101.waqt.dialogs.BottomSheetDialog
import com.heapiphy101.waqt.dialogs.TopSheetDialog
import com.heapiphy101.waqt.helper.LocaleHelper
import com.heapiphy101.waqt.services.AlarmScheduler
import com.heapiphy101.waqt.services.SilentModeReceiver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.NonCancellable.start
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.ref.WeakReference
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var loadingManualAnimation: LottieAnimationView

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var appUpdateManager: AppUpdateManager
    private val REQUEST_CODE = 1145730


    private val connectivityReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            checkNetworkConnection()
        }
    }

    private val dataUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            // Update UI with new data
            updateSalahStatus()
        }
    }

    private val handlerThread = HandlerThread("SalahStatusUpdaterThread").apply { start() }

    // Create a Handler associated with the HandlerThread
    private val handler_ = Handler(handlerThread.looper)

    private val updateSalahStatusRunnable = object : Runnable {
        override fun run() {
            runOnUiThread {
                updateSalahStatus()
            }
            handler_.postDelayed(this, updateIntervalMillis)
        }
    }




    private var remainingTimeHandler: Handler? = null
    private var remainingTimeRunnable: Runnable? = null

    private var backPressedOnce = false
    private var lastBackPressedTimestamp = 0L

    private var isPaused = false

      private lateinit var sharedPreferencesThemeUpdate: SharedPreferences
    private val handler = Handler(Looper.getMainLooper())
    private val updateIntervalMillis = 1000L

    private lateinit var sharedPreferencesFajar: SharedPreferences
    private lateinit var sharedPreferencesJuhar: SharedPreferences
    private lateinit var sharedPreferencesAsar: SharedPreferences
    private lateinit var sharedPreferencesMagrib: SharedPreferences
    private lateinit var sharedPreferencesIsha: SharedPreferences
    private lateinit var sharedPreferencesJumuah: SharedPreferences

    private lateinit var sharedPreferencesTapTarget: SharedPreferences
    private lateinit var sharedPreferencesTapTargetEditor: SharedPreferences.Editor

    private var fajartime: TextView? = null
    private var sunrisetime: TextView? = null
    private var juhartime: TextView? = null
    private var asartime: TextView? = null
    private var magribtime: TextView? = null
    private var ishatime: TextView? = null
    private var locationEditText: EditText? = null
    private var fetchPrayerTimesButton: Button? = null

    private lateinit var cityTextView: TextView
    private lateinit var sharedPreferences: SharedPreferences
    private val LOCATION_PREF_KEY = "location_pref"
    private val CITY_KEY = "city_key"

    private val apiKey = "ae60ee62b720baf29d4c78dac4138622"
    private val baseUrl = "https://muslimsalat.com/"

    private val prayerTimesService by lazy {
        Retrofit.Builder().baseUrl(baseUrl).addConverterFactory(GsonConverterFactory.create())
            .build().create(PrayerTimesService::class.java)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        createNotificationChannel()
        AlarmScheduler.scheduleJob(this)

        sharedPreferencesTapTarget = getSharedPreferences("didShowTapTarget", MODE_PRIVATE)
        sharedPreferencesTapTargetEditor = sharedPreferencesTapTarget.edit()

        showTapTarget()

        loadingManualAnimation = findViewById(R.id.loadingManualAnimation)

        cityTextView = findViewById(R.id.tvFindLocation)
        sharedPreferences = getSharedPreferences(LOCATION_PREF_KEY, Context.MODE_PRIVATE)

        remainingTimeRunnable = Runnable {
            updateTextView()
            remainingTimeRunnable?.let { remainingTimeHandler?.postDelayed(it, 1000) } // Update every second
        }

        handler_.post(updateSalahStatusRunnable)

        updateLocation()

        /*loadLocale()*/

        sharedPreferencesThemeUpdate = getSharedPreferences("PrayerTimes", MODE_PRIVATE)

        // Add the shared preferences listener
        sharedPreferencesThemeUpdate.registerOnSharedPreferenceChangeListener(sharedPreferencesListener)

        // Schedule periodic updates
        handler.post(updateThemeRunnable)

        waqtRemainTime()

        checkNotificationStatus()

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

        checkAppUpdate()

        registerConnectivityReceiver()

        /*        LocalBroadcastManager.getInstance(this)
            .registerReceiver(prayerUpdateReceiver, IntentFilter("prayer_update"))*/

        checkNetworkConnection()

        sharedPreferences = getSharedPreferences("Dnd", Context.MODE_PRIVATE)

        if (!isDndPermissionDialogShown()) {
            showDndPermissionDialog()
            setDndPermissionDialogShown()
        }


        checkDndPermission()

        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)

        binding.ivDua.setOnClickListener {
            val intent = Intent(this@MainActivity, DuaActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }

        binding.ivQuran.setOnClickListener {
            val intent = Intent(this@MainActivity, QuranActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }

        val savedLanguage = LocaleHelper.getSavedLanguage(this)

        // Apply the stored language if available
        if (!savedLanguage.isNullOrEmpty()) {
            LocaleHelper.updateLocale(this, savedLanguage)
        }

        val tvNowTimeText = getString(R.string.waqt_now)
        binding.tvNowTime.text = tvNowTimeText

        val tvNextTimeText = getString(R.string.waqt_next)
        binding.tvNextTime.text = tvNextTimeText

        val tvQuran = getString(R.string.quran_)
        binding.tvQuran.text = tvQuran

        val tvDua = getString(R.string.dua_)
        binding.tvDua.text = tvDua

        val fajarText = getString(R.string.fajr)
        binding.fajar.text = fajarText

        val juharText = getString(R.string.dhuhr)
        binding.juhar.text = juharText

        val asrText = getString(R.string.asr)
        binding.asar.text = asrText

        val maghribText = getString(R.string.maghrib)
        binding.magrib.text = maghribText

        val ishaText = getString(R.string.isha)
        binding.isha.text = ishaText

        binding.lottieActive!!.repeatCount = LottieDrawable.INFINITE
        binding.lottieActive!!.playAnimation()

        binding.lottieInActive!!.repeatCount = LottieDrawable.INFINITE
        binding.lottieInActive!!.playAnimation()

        binding.tvWaqt.let { fadeText(it) }
        binding.tvDua.let { fadeText(it) }
        binding.tvQuran.let { fadeText(it) }

        val ivCloud1 = findViewById<ImageView>(R.id.ivCloud)
        val ivCloud2 = findViewById<ImageView>(R.id.ivCloud2)
        val ivCloud3 = findViewById<ImageView>(R.id.ivCloud3)
        val displayMetrics = resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels

        setOnClickListener(ivCloud1, screenWidth, 30000)
        setOnClickListener(ivCloud2, screenWidth, 40000)
        setOnClickListener(ivCloud3, screenWidth, 50000)


        locationEditText = findViewById(R.id.etLocationInput)
        fetchPrayerTimesButton = findViewById(R.id.btnFind)
        fajartime = findViewById(R.id.fajartime)
        sunrisetime = findViewById(R.id.sunrisetime)
        juhartime = findViewById(R.id.juhartime)
        asartime = findViewById(R.id.asartime)
        magribtime = findViewById(R.id.magribtime)
        ishatime = findViewById(R.id.ishatime)

        val savedLocation = loadSavedLocationFromSharedPreferences()
        binding.tvFindLocation.text = savedLocation

        binding.ivTopSheet.setOnClickListener {
            val topSheetDialog = TopSheetDialog(this)
            topSheetDialog.show()
        }

        binding.ivFindLocation.setOnClickListener {
            if (isInternetConnected()) {
                showInputDialog()
            } else {
                showToast(
                    getString(R.string.check_your_internet_connection)
                )
            }
        }

        binding.ivQiblaCompass.setOnClickListener {
            if (isInternetConnected()) {
                val intent = Intent(this@MainActivity, QiblaFinderActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            } else {
                showToast( getString(R.string.check_your_internet_connection))
            }
        }

        binding.ivInfo.setOnClickListener {
            val bottomSheetFragment = BottomSheetDialog()
            bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
        }

        sharedPreferencesFajar = getSharedPreferences("FajarModePrefs", Context.MODE_PRIVATE)
        sharedPreferencesJuhar = getSharedPreferences("JuharModePrefs", Context.MODE_PRIVATE)
        sharedPreferencesAsar = getSharedPreferences("AsarModePrefs", Context.MODE_PRIVATE)
        sharedPreferencesMagrib = getSharedPreferences("MagribModePrefs", Context.MODE_PRIVATE)
        sharedPreferencesIsha = getSharedPreferences("IshaModePrefs", Context.MODE_PRIVATE)
        sharedPreferencesJumuah = getSharedPreferences("JumuahModePrefs", Context.MODE_PRIVATE)

        /*        val serviceIntent = Intent(this, SilentModeService::class.java)
                startService(serviceIntent)*/


        val savedStartTime = sharedPreferencesFajar.getInt("startHour", 0)
        val savedStartMinute = sharedPreferencesFajar.getInt("startMinute", 0)
        val savedEndTime = sharedPreferencesFajar.getInt("endHour", 0)
        val savedEndMinute = sharedPreferencesFajar.getInt("endMinute", 0)

        val savedJuharStartTime = sharedPreferencesJuhar.getInt("juharStartHour", 0)
        val savedJuharStartMinute = sharedPreferencesJuhar.getInt("juharStartMinute", 0)
        val savedJuharEndTime = sharedPreferencesJuhar.getInt("juharEndHour", 0)
        val savedJuharEndMinute = sharedPreferencesJuhar.getInt("juharEndMinute", 0)

        val savedAsarStartTime = sharedPreferencesAsar.getInt("asarStartHour", 0)
        val savedAsarStartMinute = sharedPreferencesAsar.getInt("asarStartMinute", 0)
        val savedAsarEndTime = sharedPreferencesAsar.getInt("asarEndHour", 0)
        val savedAsarEndMinute = sharedPreferencesAsar.getInt("asarEndMinute", 0)

        val savedMagribStartTime = sharedPreferencesMagrib.getInt("magribStartHour", 0)
        val savedMagribStartMinute = sharedPreferencesMagrib.getInt("magribStartMinute", 0)
        val savedMagribEndTime = sharedPreferencesMagrib.getInt("magribEndHour", 0)
        val savedMagribEndMinute = sharedPreferencesMagrib.getInt("magribEndMinute", 0)

        val savedIshaStartTimeHour = sharedPreferencesIsha.getInt("ishaStartHour", 0)
        val savedIshaStartTimeMinute = sharedPreferencesIsha.getInt("ishaStartMinute", 0)
        val savedIshaEndTimeHour = sharedPreferencesIsha.getInt("ishaEndHour", 0)
        val savedIshaEndTimeMinute = sharedPreferencesIsha.getInt("ishaEndMinute", 0)

//        val savedJumuahStartTime = sharedPreferencesJumuah.getInt("jumuahStartHour", 0)
//        val savedJumuahStartMinute = sharedPreferencesJumuah.getInt("jumuahStartMinute", 0)
//        val savedJumuahEndTime = sharedPreferencesJumuah.getInt("jumuahEndHour", 0)
//        val savedJumuahEndMinute = sharedPreferencesJumuah.getInt("jumuahEndMinute", 0)

        binding.fajarStartTime.text = formatTime(savedStartTime, savedStartMinute)
        binding.fajarEndTime.text = formatTime(savedEndTime, savedEndMinute)

        binding.juharStartTime.text = formatTime(savedJuharStartTime, savedJuharStartMinute)
        binding.juharEndTime.text = formatTime(savedJuharEndTime, savedJuharEndMinute)

        binding.asarStartTime.text = formatTime(savedAsarStartTime, savedAsarStartMinute)
        binding.asarEndTime.text = formatTime(savedAsarEndTime, savedAsarEndMinute)

        binding.magribStartTime.text = formatTime(savedMagribStartTime, savedMagribStartMinute)
        binding.magribEndTime.text = formatTime(savedMagribEndTime, savedMagribEndMinute)

        binding.ishaStartTime.text = formatTime(savedIshaStartTimeHour, savedIshaStartTimeMinute)
        binding.ishaEndTime.text = formatTime(savedIshaEndTimeHour, savedIshaEndTimeMinute)

        binding.fajarStart.setOnClickListener {
            val currentTime = binding.fajarStartTime.text.toString().split(":")
            val hour = currentTime[0].trim().toInt()
            val minute = currentTime[1].trim().split(" ")[0].toInt()
            val isPM = currentTime[1].trim().split(" ")[1] == "PM"

            showTimePickerDialog(hour, minute, isPM) { hourOfDay, minute ->
                binding.fajarStartTime.text = formatTime(hourOfDay, minute)

                saveStartTime(hourOfDay, minute)
            }
        }

        binding.fajarEnd.setOnClickListener {
            val currentTime = binding.fajarEndTime.text.toString().split(":")
            val hour = currentTime[0].trim().toInt()
            val minute = currentTime[1].trim().split(" ")[0].toInt()
            val isPM = currentTime[1].trim().split(" ")[1] == "PM"

            showTimePickerDialog(hour, minute, isPM) { hourOfDay, minute ->
                binding.fajarEndTime.text = formatTime(hourOfDay, minute)

                saveEndTime(hourOfDay, minute)

                if (binding.switchFajar.isOn) {
                    setSilentModeDuringTimeRange()
                }
            }
        }

        binding.switchFajar.setOnToggledListener { _, isChecked ->
            if (isChecked) {

                    sharedPreferencesFajar.edit().putBoolean("switchState", true).apply()

                    binding.fajarStart.isEnabled = true
                    binding.fajarEnd.isEnabled = true

                    val savedStartTimeHour = sharedPreferencesFajar.getInt("startHour", 0)
                    val savedStartTimeMinute = sharedPreferencesFajar.getInt("startMinute", 0)
                    val savedEndTimeHour = sharedPreferencesFajar.getInt("endHour", 0)
                    val savedEndTimeMinute = sharedPreferencesFajar.getInt("endMinute", 0)

                    binding.fajarStartTime.text =
                        formatTime(savedStartTimeHour, savedStartTimeMinute)
                    binding.fajarEndTime.text = formatTime(savedEndTimeHour, savedEndTimeMinute)

                    setSilentModeDuringTimeRange()

            } else {
                sharedPreferencesFajar.edit().putBoolean("switchState", false).apply()

                binding.fajarStart.isEnabled = true
                binding.fajarEnd.isEnabled = true

                val savedStartTimeHour = sharedPreferencesFajar.getInt("startHour", 0)
                val savedStartTimeMinute = sharedPreferencesFajar.getInt("startMinute", 0)
                val savedEndTimeHour = sharedPreferencesFajar.getInt("endHour", 0)
                val savedEndTimeMinute = sharedPreferencesFajar.getInt("endMinute", 0)

                binding.fajarStartTime.text = formatTime(savedStartTimeHour, savedStartTimeMinute)
                binding.fajarEndTime.text = formatTime(savedEndTimeHour, savedEndTimeMinute)
            }
        }


        binding.juharStart.setOnClickListener {
            val currentTime = binding.juharStartTime.text.toString().split(":")
            val hour = currentTime[0].trim().toInt()
            val minute = currentTime[1].trim().split(" ")[0].toInt()
            val isPM = currentTime[1].trim().split(" ")[1] == "PM"

            showTimePickerDialog(hour, minute, isPM) { hourOfDay, minute ->
                binding.juharStartTime.text = formatTime(hourOfDay, minute)

                saveJuharStartTime(hourOfDay, minute)

                if (binding.switchJuhar.isOn) {
                    setSilentModeDuringJuharTimeRange()
                }
            }
        }

        binding.juharEnd.setOnClickListener {
            val currentTime = binding.juharEndTime.text.toString().split(":")
            val hour = currentTime[0].trim().toInt()
            val minute = currentTime[1].trim().split(" ")[0].toInt()
            val isPM = currentTime[1].trim().split(" ")[1] == "PM"

            showTimePickerDialog(hour, minute, isPM) { hourOfDay, minute ->
                binding.juharEndTime.text = formatTime(hourOfDay, minute)

                saveJuharEndTime(hourOfDay, minute)

                if (binding.switchJuhar.isOn) {
                    setSilentModeDuringJuharTimeRange()
                }
            }
        }

        binding.switchJuhar.setOnToggledListener { _, isChecked ->
            if (isChecked) {

                    sharedPreferencesJuhar.edit().putBoolean("juharSwitchState", true).apply()

                    binding.juharStart.isEnabled = true
                    binding.juharEnd.isEnabled = true

                    val savedJuharStartTimeHour = sharedPreferencesJuhar.getInt("juharStartHour", 0)
                    val savedJuharStartTimeMinute =
                        sharedPreferencesJuhar.getInt("juharStartMinute", 0)
                    val savedJuharEndTimeHour = sharedPreferencesJuhar.getInt("juharEndHour", 0)
                    val savedJuharEndTimeMinute = sharedPreferencesJuhar.getInt("juharEndMinute", 0)

                    binding.juharStartTime.text =
                        formatTime(savedJuharStartTimeHour, savedJuharStartTimeMinute)
                    binding.juharEndTime.text =
                        formatTime(savedJuharEndTimeHour, savedJuharEndTimeMinute)

                    setSilentModeDuringJuharTimeRange()

            } else {
                sharedPreferencesJuhar.edit().putBoolean("juharSwitchState", false).apply()

                binding.juharStart.isEnabled = true
                binding.juharEnd.isEnabled = true

                val savedJuharStartTimeHour = sharedPreferencesJuhar.getInt("juharStartHour", 0)
                val savedJuharStartTimeMinute = sharedPreferencesJuhar.getInt("juharStartMinute", 0)
                val savedJuharEndTimeHour = sharedPreferencesJuhar.getInt("juharEndHour", 0)
                val savedJuharEndTimeMinute = sharedPreferencesJuhar.getInt("juharEndMinute", 0)

                binding.juharStartTime.text =
                    formatTime(savedJuharStartTimeHour, savedJuharStartTimeMinute)
                binding.juharEndTime.text =
                    formatTime(savedJuharEndTimeHour, savedJuharEndTimeMinute)
            }
        }


        binding.asarStart.setOnClickListener {
            val currentTime = binding.asarStartTime.text.toString().split(":")
            val hour = currentTime[0].trim().toInt()
            val minute = currentTime[1].trim().split(" ")[0].toInt()
            val isPM = currentTime[1].trim().split(" ")[1] == "PM"

            showTimePickerDialog(hour, minute, isPM) { hourOfDay, minute ->
                binding.asarStartTime.text = formatTime(hourOfDay, minute)

                saveAsarStartTime(hourOfDay, minute)

                if (binding.switchAsar.isOn) {
                    setSilentModeDuringAsarTimeRange()
                }
            }
        }

        binding.asarEnd.setOnClickListener {
            val currentTime = binding.asarEndTime.text.toString().split(":")
            val hour = currentTime[0].trim().toInt()
            val minute = currentTime[1].trim().split(" ")[0].toInt()
            val isPM = currentTime[1].trim().split(" ")[1] == "PM"

            showTimePickerDialog(hour, minute, isPM) { hourOfDay, minute ->
                binding.asarEndTime.text = formatTime(hourOfDay, minute)

                saveAsarEndTime(hourOfDay, minute)

                if (binding.switchAsar.isOn) {
                    setSilentModeDuringAsarTimeRange()
                }
            }
        }

        binding.switchAsar.setOnToggledListener { _, isChecked ->
            if (isChecked) {

                    sharedPreferencesAsar.edit().putBoolean("asarSwitchState", true).apply()

                    binding.asarStart.isEnabled = true
                    binding.asarEnd.isEnabled = true

                    val savedAsarStartTimeHour = sharedPreferencesAsar.getInt("asarStartHour", 0)
                    val savedAsarStartTimeMinute =
                        sharedPreferencesAsar.getInt("asarStartMinute", 0)
                    val savedAsarEndTimeHour = sharedPreferencesAsar.getInt("asarEndHour", 0)
                    val savedAsarEndTimeMinute = sharedPreferencesAsar.getInt("asarEndMinute", 0)

                    binding.asarStartTime.text =
                        formatTime(savedAsarStartTimeHour, savedAsarStartTimeMinute)
                    binding.asarEndTime.text =
                        formatTime(savedAsarEndTimeHour, savedAsarEndTimeMinute)

                    setSilentModeDuringAsarTimeRange()

            } else {
                sharedPreferencesAsar.edit().putBoolean("asarSwitchState", false).apply()

                binding.asarStart.isEnabled = true
                binding.asarEnd.isEnabled = true

                val savedAsarStartTimeHour = sharedPreferencesAsar.getInt("asarStartHour", 0)
                val savedAsarStartTimeMinute = sharedPreferencesAsar.getInt("asarStartMinute", 0)
                val savedAsarEndTimeHour = sharedPreferencesAsar.getInt("asarEndHour", 0)
                val savedAsarEndTimeMinute = sharedPreferencesAsar.getInt("asarEndMinute", 0)

                binding.asarStartTime.text =
                    formatTime(savedAsarStartTimeHour, savedAsarStartTimeMinute)
                binding.asarEndTime.text = formatTime(savedAsarEndTimeHour, savedAsarEndTimeMinute)
            }
        }

        binding.magribStart.setOnClickListener {
            val currentTime = binding.magribStartTime.text.toString().split(":")
            val hour = currentTime[0].trim().toInt()
            val minute = currentTime[1].trim().split(" ")[0].toInt()
            val isPM = currentTime[1].trim().split(" ")[1] == "PM"

            showTimePickerDialog(hour, minute, isPM) { hourOfDay, minute ->
                binding.magribStartTime.text = formatTime(hourOfDay, minute)

                saveMagribStartTime(hourOfDay, minute)

                if (binding.switchMagrib.isOn) {
                    setSilentModeDuringMagribTimeRange()
                }
            }
        }

        binding.magribEnd.setOnClickListener {
            val currentTime = binding.magribEndTime.text.toString().split(":")
            val hour = currentTime[0].trim().toInt()
            val minute = currentTime[1].trim().split(" ")[0].toInt()
            val isPM = currentTime[1].trim().split(" ")[1] == "PM"

            showTimePickerDialog(hour, minute, isPM) { hourOfDay, minute ->
                binding.magribEndTime.text = formatTime(hourOfDay, minute)

                saveMagribEndTime(hourOfDay, minute)

                if (binding.switchMagrib.isOn) {
                    setSilentModeDuringMagribTimeRange()
                }
            }
        }

        binding.switchMagrib.setOnToggledListener { _, isChecked ->
            if (isChecked) {

                    sharedPreferencesMagrib.edit().putBoolean("magribSwitchState", true).apply()

                    binding.magribStart.isEnabled = true
                    binding.magribEnd.isEnabled = true


                    val savedMagribStartTimeHour =
                        sharedPreferencesMagrib.getInt("magribStartHour", 0)
                    val savedMagribStartTimeMinute =
                        sharedPreferencesMagrib.getInt("magribStartMinute", 0)
                    val savedMagribEndTimeHour = sharedPreferencesMagrib.getInt("magribEndHour", 0)
                    val savedMagribEndTimeMinute =
                        sharedPreferencesMagrib.getInt("magribEndMinute", 0)

                    binding.magribStartTime.text =
                        formatTime(savedMagribStartTimeHour, savedMagribStartTimeMinute)
                    binding.magribEndTime.text =
                        formatTime(savedMagribEndTimeHour, savedMagribEndTimeMinute)

                    setSilentModeDuringMagribTimeRange()

            } else {
                sharedPreferencesMagrib.edit().putBoolean("magribSwitchState", false).apply()

                binding.magribStart.isEnabled = true
                binding.magribEnd.isEnabled = true

                val savedMagribStartTimeHour = sharedPreferencesMagrib.getInt("magribStartHour", 0)
                val savedMagribStartTimeMinute =
                    sharedPreferencesMagrib.getInt("magribStartMinute", 0)
                val savedMagribEndTimeHour = sharedPreferencesMagrib.getInt("magribEndHour", 0)
                val savedMagribEndTimeMinute = sharedPreferencesMagrib.getInt("magribEndMinute", 0)

                binding.magribStartTime.text =
                    formatTime(savedMagribStartTimeHour, savedMagribStartTimeMinute)
                binding.magribEndTime.text =
                    formatTime(savedMagribEndTimeHour, savedMagribEndTimeMinute)
            }
        }


        binding.ishaStart.setOnClickListener {
            val currentTime = binding.ishaStartTime.text.toString().split(":")
            val hour = currentTime[0].trim().toInt()
            val minute = currentTime[1].trim().split(" ")[0].toInt()
            val isPM = currentTime[1].trim().split(" ")[1] == "PM"

            showTimePickerDialog(hour, minute, isPM) { hourOfDay, minute ->
                binding.ishaStartTime.text = formatTime(hourOfDay, minute)

                saveIshaStartTime(hourOfDay, minute)

                if (binding.switchIsha.isOn) {
                    setSilentModeDuringIshaTimeRange()
                }
            }
        }

        binding.ishaEnd.setOnClickListener {
            val currentTime = binding.ishaEndTime.text.toString().split(":")
            val hour = currentTime[0].trim().toInt()
            val minute = currentTime[1].trim().split(" ")[0].toInt()
            val isPM = currentTime[1].trim().split(" ")[1] == "PM"

            showTimePickerDialog(hour, minute, isPM) { hourOfDay, minute ->
                binding.ishaEndTime.text = formatTime(hourOfDay, minute)

                saveIshaEndTime(hourOfDay, minute)

                if (binding.switchIsha.isOn) {
                    setSilentModeDuringIshaTimeRange()
                }
            }
        }

        binding.switchIsha.setOnToggledListener { _, isChecked ->
            if (isChecked) {

                    sharedPreferencesIsha.edit().putBoolean("ishaSwitchState", true).apply()

                    binding.ishaStart.isEnabled = true
                    binding.ishaEnd.isEnabled = true

                    val savedIshaStartTimeHour =
                        sharedPreferencesIsha.getInt("ishaStartHour", 0)
                    val savedIshaStartTimeMinute =
                        sharedPreferencesIsha.getInt("ishaStartMinute", 0)
                    val savedIshaEndTimeHour =
                        sharedPreferencesIsha.getInt("ishaEndHour", 0)
                    val savedIshaEndTimeMinute =
                        sharedPreferencesIsha.getInt("ishaEndMinute", 0)

                    binding.ishaStartTime.text =
                        formatTime(savedIshaStartTimeHour, savedIshaStartTimeMinute)
                    binding.ishaEndTime.text =
                        formatTime(savedIshaEndTimeHour, savedIshaEndTimeMinute)

                    setSilentModeDuringIshaTimeRange()

            } else {
                sharedPreferencesIsha.edit().putBoolean("ishaSwitchState", false).apply()

                binding.ishaStart.isEnabled = true
                binding.ishaEnd.isEnabled = true

                val savedIshaStartTimeHour = sharedPreferencesIsha.getInt("ishaStartHour", 0)
                val savedIshaStartTimeMinute =
                    sharedPreferencesIsha.getInt("ishaStartMinute", 0)
                val savedIshaEndTimeHour = sharedPreferencesIsha.getInt("ishaEndHour", 0)
                val savedIshaEndTimeMinute = sharedPreferencesIsha.getInt("ishaEndMinute", 0)

                binding.ishaStartTime.text =
                    formatTime(savedIshaStartTimeHour, savedIshaStartTimeMinute)
                binding.ishaEndTime.text =
                    formatTime(savedIshaEndTimeHour, savedIshaEndTimeMinute)
            }
        }


    }

   /* private fun loadLocale() {
        val preferences = getSharedPreferences("settings", MODE_PRIVATE)
        val language = preferences.getString("language", "")
        setLocale(language ?: "")
    }

    private fun saveLocale(language: String) {
        val preferences = getSharedPreferences("settings", MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putString("language", language)
        editor.apply()
    }

    private fun setLocale(languageCode: String) {
        val resources: Resources = resources
        val config: Configuration = resources.configuration
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        config.locale = locale
        resources.updateConfiguration(config, resources.displayMetrics)

        // You may need to recreate your activity or fragments to apply the changes
        // For example, you can recreate the current activity
        recreate()
    }*/

    private fun checkAppUpdate() {
        appUpdateManager = AppUpdateManagerFactory.create(this)

        // Returns an intent object that you use to check for an update.
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo

        // Checks whether the platform allows the specified type of update,
        // and current version staleness.
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
               /* && (appUpdateInfo.clientVersionStalenessDays() ?: -1) >= DAYS_FOR_FLEXIBLE_UPDATE*/
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {

                appUpdateManager.startUpdateFlowForResult(appUpdateInfo,
                    AppUpdateType.FLEXIBLE,
                    this,
                    REQUEST_CODE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE) {
            if(resultCode != RESULT_OK) {
                showToast("Update failed. Please try again.")

                // You may want to implement a retry mechanism, for example, by prompting the user to retry
                showRetryDialog()
            }
        }
    }

    private fun checkNotificationStatus() {
        val sharedPreferences = getSharedPreferences("NotificationSettings", Context.MODE_PRIVATE)
        val isNotificationOn = sharedPreferences.getBoolean("isNotificationOn", false)

        if (isNotificationOn) {
            // Notifications are turned on, so you can call the method to schedule notifications
            scheduleSalahNotification()
        } else {
            // Notifications are turned off
            // If you have previously scheduled notifications, you may want to cancel them here
            cancelScheduledNotifications()
        }
    }

    private fun cancelScheduledNotifications() {
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java)

        // Use the same requestCode (0 in your case) and the same intent to create the PendingIntent
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Cancel the scheduled alarm by passing the PendingIntent
        alarmManager.cancel(pendingIntent)

        // If you have a notification associated with this alarm, you may also want to cancel it
        cancelNotification()
    }

    private fun cancelNotification() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Use a unique notification ID to identify your scheduled notification
        val notificationId = 1 // Replace this with your actual notification ID

        // Cancel the notification by its ID
        notificationManager.cancel(notificationId)
    }



    private fun showRetryDialog() {
        AlertDialog.Builder(this)
            .setTitle("Update Failed")
            .setMessage("The update was not successful. Do you want to retry?")
            .setPositiveButton("Retry") { _, _ ->
                // Retry logic goes here, you may want to call startUpdateFlow() again
                checkAppUpdate()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
                // Handle cancellation, for example, return to the main screen
            }
            .show()
    }

    private fun registerConnectivityReceiver() {
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(connectivityReceiver, filter)
    }


    private fun checkNetworkConnection() {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo

        if (networkInfo == null || !networkInfo.isConnected) {
            showNoConnection()
        } else {
            showConnected()
        }
    }

    private fun showNoConnection() {
        binding.tvCheckConnection.visibility = View.VISIBLE
        binding.tvCheckConnection.text = "No connection"
        setBackgroundColor(R.color.black)
        setTextColor(R.color.white)
    }

    private fun showConnected() {
        binding.tvCheckConnection.visibility = View.VISIBLE
        binding.tvCheckConnection.text = "Connected"
        setBackgroundColor(R.color.card_background_normal) // Change to the appropriate color resource
        setTextColor(R.color.white)

        // Remove the Handler and set visibility directly
        binding.tvCheckConnection.postDelayed({
            binding.tvCheckConnection.visibility = View.GONE
        }, 1000)
    }

    private fun setBackgroundColor(@ColorRes colorRes: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            binding.tvCheckConnection.setBackgroundColor(resources.getColor(colorRes, theme))
        } else {
            binding.tvCheckConnection.setBackgroundColor(resources.getColor(colorRes))
        }
    }

    private fun setTextColor(@ColorRes colorRes: Int) {
        binding.tvCheckConnection.setTextColor(ContextCompat.getColor(this, colorRes))
    }

    fun scheduleSalahNotification() {
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java)

        // Use FLAG_IMMUTABLE for non-mutable PendingIntent
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Use setRepeating for repeated alarms
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis(),
            60000, // Repeat every minute
            pendingIntent
        )
    }


    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "default",
                "Default Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showTapTarget() {
        if (!sharedPreferencesTapTarget.getBoolean("didShowTapTarget", false)) {
            TapTargetView.showFor(this,
                TapTarget.forView(
                    binding.tvFindLocation,
                    "Step 1: GPS Tracker",
                    "Automatically find your current location."
                ).tintTarget(true).outerCircleColor(R.color.card_background_normal),
                object : TapTargetView.Listener() {
                    override fun onTargetClick(view: TapTargetView) {
                        super.onTargetClick(view)
                        showTapTarget1()
                    }
                })
        }
    }

    private fun showTapTarget1() {
        TapTargetView.showFor(this,
            TapTarget.forView(
                binding.ivQiblaCompass, "Step 2: Qibla Compass", "Automatically find your qibla."
            ).tintTarget(true).outerCircleColor(R.color.card_background_normal),
            object : TapTargetView.Listener() {
                override fun onTargetClick(view: TapTargetView) {
                    super.onTargetClick(view)
                    showTapTarget2()
                }
            })
    }

    private fun showTapTarget2() {
        TapTargetView.showFor(this,
            TapTarget.forView(
                binding.ivTopSheet, "Step 3: Salah Times", "Salah times are visible here."
            ).tintTarget(true).outerCircleColor(R.color.card_background_normal),
            object : TapTargetView.Listener() {
                override fun onTargetClick(view: TapTargetView) {
                    super.onTargetClick(view)
                    showTapTarget3()
                }
            })
    }

    private fun showTapTarget3() {
        TapTargetView.showFor(this,
            TapTarget.forView(
                binding.ivFindLocation,
                "Step 4: Manual Location Tracking",
                "By entering the city name, you can view the salah times for other cities."
            ).tintTarget(true).outerCircleColor(R.color.card_background_normal),
            object : TapTargetView.Listener() {
                override fun onTargetClick(view: TapTargetView) {
                    super.onTargetClick(view)
                    showTapTarget4()
                }
            })
    }

    private fun showTapTarget4() {
        TapTargetView.showFor(this,
            TapTarget.forView(
                binding.ivInfo, "Step 5: More", "To see important information."
            ).tintTarget(true).outerCircleColor(R.color.card_background_normal),
            object : TapTargetView.Listener() {
                override fun onTargetClick(view: TapTargetView) {
                    super.onTargetClick(view)
                    showTapTarget4_1()
                }
            })
    }

    private fun showTapTarget4_1() {
        TapTargetView.showFor(this,
            TapTarget.forView(
                binding.ivQuran, "Step 6: Quran", "Read the Quran offline."
            ).tintTarget(true).outerCircleColor(R.color.card_background_normal),
            object : TapTargetView.Listener() {
                override fun onTargetClick(view: TapTargetView) {
                    super.onTargetClick(view)
                    showTapTarget4_2()
                }
            })
    }

    private fun showTapTarget4_2() {
        TapTargetView.showFor(this,
            TapTarget.forView(
                binding.ivDua, "Step 7: Dua", "Here you find Mas'nun Zekir."
            ).tintTarget(true).outerCircleColor(R.color.card_background_normal),
            object : TapTargetView.Listener() {
                override fun onTargetClick(view: TapTargetView) {
                    super.onTargetClick(view)
                    showTapTarget5()
                }
            })
    }


    private fun showTapTarget5() {
        TapTargetView.showFor(this,
            TapTarget.forView(
                binding.fajarStart, "Step 8: Auto Silent Mode", "Set start time."
            ).tintTarget(true).outerCircleColor(R.color.card_background_normal),
            object : TapTargetView.Listener() {
                override fun onTargetClick(view: TapTargetView) {
                    super.onTargetClick(view)
                    showTapTarget6()
                }
            })
    }

    private fun showTapTarget6() {
        TapTargetView.showFor(this,
            TapTarget.forView(
                binding.fajarEnd, "Step 9: Auto Silent Mode", "Set end time."
            ).tintTarget(true).outerCircleColor(R.color.card_background_normal),
            object : TapTargetView.Listener() {
                override fun onTargetClick(view: TapTargetView) {
                    super.onTargetClick(view)
                    showTapTarget7()
                }
            })
    }

    private fun showTapTarget7() {
        TapTargetView.showFor(this,
            TapTarget.forView(
                binding.switchFajar,
                "Step 10: Auto Silent Mode",
                "Toggle silent mode is active or inactive"
            ).tintTarget(true).outerCircleColor(R.color.card_background_normal),
            object : TapTargetView.Listener() {
                override fun onTargetClick(view: TapTargetView) {
                    super.onTargetClick(view)
                    val prefEditor = sharedPreferencesTapTarget.edit()
                    prefEditor.putBoolean("didShowTapTarget", true)
                    prefEditor.apply()
                }
            })
    }

    private var locationFound = false
    private val LOCATION_FOUND_KEY = "location_found"
    private var lastSavedCity: String? = null
    private var topSnackbarLayout: View? = null

    private fun updateLocation() {
        cityTextView = findViewById(R.id.tvFindLocation)
        sharedPreferences = getSharedPreferences(LOCATION_PREF_KEY, Context.MODE_PRIVATE)
        val locationFoundBefore = sharedPreferences.getBoolean(LOCATION_FOUND_KEY, false)
        lastSavedCity = sharedPreferences.getString(CITY_KEY, "")
        cityTextView.text = lastSavedCity

        // Initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Check network capabilities
        val connectivityManagerCheck =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManagerCheck.activeNetwork
        val capabilities = connectivityManagerCheck.getNetworkCapabilities(network)

        if (capabilities != null && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || capabilities.hasTransport(
                NetworkCapabilities.TRANSPORT_CELLULAR
            ))
        ) {
            showTopSnackbar(getString(R.string.updating_location_this_may_take_a_moment))
        }


        // Check if GPS provider is enabled
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // GPS is not enabled, prompt the user to enable it
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
            return
        }

        // Request location updates only if GPS permission is granted
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION
                ), 1
            )
            dismissSnackbar()
        } else {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    location?.let {
                        handleLocation(it)
                    }
                }

            fusedLocationClient.requestLocationUpdates(
                LocationRequest.create(),
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.lastLocation?.let { handleLocation(it) }
        }
    }

    private fun handleLocation(location: Location) {
        try {
            // Your existing logic for handling location changes
            val geocoder = Geocoder(this@MainActivity, Locale.getDefault())
            val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)

            // Update UI and perform other necessary actions
            if (!addresses.isNullOrEmpty()) {
                val cityName = addresses[0].locality
                cityTextView.text = cityName

                // Save city to SharedPreferences
                with(sharedPreferences.edit()) {
                    putString(CITY_KEY, cityName)
                    apply()
                }

                fetchSalahTimes(cityName)

                if (!locationFound && lastSavedCity != cityName) {
                    // Location found for the first time
                    with(sharedPreferences.edit()) {
                        putString(CITY_KEY, cityName)
                        putBoolean(LOCATION_FOUND_KEY, true) // Set the flag to true
                        apply()
                    }

                    // Show location updated dialog
/*                    showLocationUpdatedDialog()*/

                    locationFound = true
                }

            } else {
                cityTextView.text = "Loading..."
            }
            dismissSnackbar()
        } catch (e: Exception) {
            e.printStackTrace()
            cityTextView.text = "Loading..."
            dismissSnackbar()
        }
    }

    private fun showLocationUpdatedDialog() {
        val alertDialogBuilder = AlertDialog.Builder(this@MainActivity)
        val inflater = layoutInflater
        val view = inflater.inflate(R.layout.dialog_refresh, null)

        val titleTextView: TextView = view.findViewById(R.id.dialog_refresh_title)
        val messageTextView: TextView = view.findViewById(R.id.dialog_refresh_message)
        val btnRefresh: Button = view.findViewById(R.id.btnRefresh)

        titleTextView.text = "Location updated!"
        messageTextView.text = "Refresh the app to see the changes."

        val alertDialog = alertDialogBuilder.setView(view)
            .setCancelable(true)
            .create()

        btnRefresh.setOnClickListener {
            finish()
            val intent = Intent(this@MainActivity, MainActivity::class.java)
            intent.addFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK or
                        Intent.FLAG_ACTIVITY_NEW_TASK
            )
            startActivity(intent)
            alertDialog.dismiss()
        }

        alertDialog.setOnShowListener {
            alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        }
        alertDialog.window?.attributes?.windowAnimations = R.style.TimePickerDialogAnimation
        alertDialog.show()
    }



    private fun showTopSnackbar(message: String) {
        val inflater = LayoutInflater.from(this)
        val topSnackbarLayout = inflater.inflate(R.layout.layout_top_snackbar, null)
        val tvTopSnackbar = topSnackbarLayout.findViewById<TextView>(R.id.tvTopSnackbar)
        tvTopSnackbar.text = message

        val rootView: View = window.decorView.findViewById(android.R.id.content)

        val layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val parentLayout: ViewGroup = rootView as ViewGroup

        parentLayout.addView(topSnackbarLayout, layoutParams)

        Handler(Looper.getMainLooper()).postDelayed({
            parentLayout.removeView(topSnackbarLayout)
        }, 2000)
    }


    private fun dismissSnackbar() {
        val parentLayout: ViewGroup = window.decorView.findViewById(android.R.id.content)

        topSnackbarLayout?.let {
            parentLayout.removeView(it)
            topSnackbarLayout = null
        }
    }


    private fun isInternetConnected(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)

        return capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
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
            Glide.with(this)
                .load(R.drawable.isha)
                .placeholder(R.drawable.loading_img)
                .into(binding.imageView)
            binding.constraintLayout.setBackgroundResource(R.drawable.gradient_background_isha)

            binding.cardView1.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent))
            binding.cardView2.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent))
            binding.cardView3.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent))
            binding.cardView4.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent))
            binding.cardView5.setBackgroundColor(ContextCompat.getColor(this, R.color.card_background_black))
        } else if (isBetween(formattedCurrentTime, formattedFajr, formattedSunrise)) {
            Glide.with(this)
                .load(R.drawable.fajar)
                .placeholder(R.drawable.loading_img)
                .into(binding.imageView)
            binding.constraintLayout.setBackgroundResource(R.drawable.gradient_background_fajr)

            binding.cardView1.setBackgroundColor(ContextCompat.getColor(this, R.color.card_background_black))
            binding.cardView2.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent))
            binding.cardView3.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent))
            binding.cardView4.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent))
            binding.cardView5.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent))
        } else if (isBetween(
                formattedCurrentTime,
                formattedDhuhr,
                formattedAsr
            )
        ) {
            Glide.with(this)
                .load(R.drawable.juhar)
                .placeholder(R.drawable.loading_img)
                .into(binding.imageView)
            binding.constraintLayout.setBackgroundResource(R.drawable.gradient_background_juhar)
            binding.cardView1.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent))
            binding.cardView2.setBackgroundColor(ContextCompat.getColor(this, R.color.card_background_black))
            binding.cardView3.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent))
            binding.cardView4.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent))
            binding.cardView5.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent))
        } /*else if (dayOfWeek == Calendar.FRIDAY && isBetween(
                formattedCurrentTime,
                formattedDhuhr,
                formattedAsr
            )
        ) {
            Glide.with(this)
                .load(R.drawable.juhar)
                .into(binding.imageView)
            binding.constraintLayout.setBackgroundResource(R.drawable.gradient_background_juhar)
            binding.cardView1.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent))
            binding.cardView2.setBackgroundColor(ContextCompat.getColor(this, R.color.card_background_black))
            binding.cardView3.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent))
            binding.cardView4.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent))
            binding.cardView5.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent))
        }*/ else if (isBetween(formattedCurrentTime, formattedAsr, formattedMaghrib)) {
            Glide.with(this)
                .load(R.drawable.asar)
                .placeholder(R.drawable.loading_img)
                .into(binding.imageView)
            binding.constraintLayout.setBackgroundResource(R.drawable.gradient_background_asr)
            binding.cardView1.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent))
            binding.cardView2.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent))
            binding.cardView3.setBackgroundColor(ContextCompat.getColor(this, R.color.card_background_black))
            binding.cardView4.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent))
            binding.cardView5.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent))
        } else if (isBetween(formattedCurrentTime, formattedMaghrib, formattedIsha)) {
            Glide.with(this)
                .load(R.drawable.magrib)
                .placeholder(R.drawable.loading_img)
                .into(binding.imageView)
            binding.constraintLayout.setBackgroundResource(R.drawable.gradient_background_maghrib)
            binding.cardView1.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent))
            binding.cardView2.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent))
            binding.cardView3.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent))
            binding.cardView4.setBackgroundColor(ContextCompat.getColor(this, R.color.card_background_black))
            binding.cardView5.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent))
        } else {
            Glide.with(this)
                .load(R.drawable.shurooq)
                .placeholder(R.drawable.loading_img)
                .into(binding.imageView)
            binding.constraintLayout.setBackgroundResource(R.drawable.gradient_background_normal)
            binding.cardView1.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent))
            binding.cardView2.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent))
            binding.cardView3.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent))
            binding.cardView4.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent))
            binding.cardView5.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent))
        }

    }



    @RequiresApi(Build.VERSION_CODES.O)
    private fun isBetween(current: LocalTime?, start: LocalTime?, end: LocalTime?): Boolean {
        return current != null && start != null && end != null && current.isAfter(start) && current.isBefore(
            end
        )
    }

///*    private fun setCardBackground(
//        imageResource: Int,
//        backgroundColorResource: Int
//    ) {
//        // Load image using Glide
//        Glide.with(this)
//            .load(imageResource)
//            .into(binding.imageView)
//
//        // Set background color using ColorStateList
//        val colorStateList = ColorStateList.valueOf(ContextCompat.getColor(this, backgroundColorResource))
//        ViewCompat.setBackgroundTintList(binding.constraintLayout, colorStateList)
//
//        // Set card background colors
//        val cardBackgroundColor = ContextCompat.getColor(this, backgroundColorResource)
//        binding.cardView1.setBackgroundColor(cardBackgroundColor)
//        binding.cardView2.setBackgroundColor(cardBackgroundColor)
//        binding.cardView3.setBackgroundColor(cardBackgroundColor)
//        binding.cardView4.setBackgroundColor(cardBackgroundColor)
//        binding.cardView5.setBackgroundColor(cardBackgroundColor)
//    }*/

    private fun getCurrentTime(): String {
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val currentTime: Date = Date()
        return sdf.format(currentTime)
    }

    private fun formatTime(hour: Int, minute: Int): String {
        val amPm = if (hour >= 12) "PM" else "AM"
        val formattedHour = if (hour % 12 == 0) 12 else hour % 12
        return String.format("%02d:%02d %s", formattedHour, minute, amPm)
    }

    private fun showTimePickerDialog(
        hour: Int, minute: Int, isPM: Boolean, callback: (Int, Int) -> Unit
    ) {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)

        val timePickerDialog = TimePickerDialog(
            this@MainActivity, TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                callback(hourOfDay, minute)
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false
        )

        timePickerDialog.window?.attributes?.windowAnimations = R.style.TimePickerDialogAnimation

        timePickerDialog.setOnShowListener {
            val timePicker = timePickerDialog.findViewById<TimePicker>(
                Resources.getSystem().getIdentifier("timePicker", "id", "android")
            )
            if (isPM) {
                timePicker?.hour = hour + 12
            } else {
                timePicker?.hour = hour
            }
            timePicker?.minute = minute
        }

        timePickerDialog.show()
    }

    private fun setSilentModeDuringTimeRange() {
        val currentCalendar = Calendar.getInstance()
        val currentHour = currentCalendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = currentCalendar.get(Calendar.MINUTE)

        val startHour = sharedPreferencesFajar.getInt("startHour", 0)
        val startMinute = sharedPreferencesFajar.getInt("startMinute", 0)
        val endHour = sharedPreferencesFajar.getInt("endHour", 0)
        val endMinute = sharedPreferencesFajar.getInt("endMinute", 0)

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val silentModeStartIntent = Intent(this, SilentModeReceiver::class.java)
            .setAction(SilentModeReceiver.ACTION_START_SILENT_MODE)

        val silentModeStopIntent = Intent(this, SilentModeReceiver::class.java)
            .setAction(SilentModeReceiver.ACTION_STOP_SILENT_MODE)

        val startPendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            silentModeStartIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val stopPendingIntent = PendingIntent.getBroadcast(
            this,
            1,
            silentModeStopIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val startTimeMillis = calculateTimeMillis(startHour, startMinute)
        val endTimeMillis = calculateTimeMillis(endHour, endMinute)

        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP, startTimeMillis, startPendingIntent
        )

        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP, endTimeMillis, stopPendingIntent
        )

        if (binding.switchFajar.isOn) {
            if (currentHour == startHour && currentMinute == startMinute) {
                setPhoneToSilentMode()
            } else if (currentHour == endHour && currentMinute == endMinute) {
                stopSilentMode()
            }
        }
    }


    private fun calculateTimeMillis(hour: Int, minute: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    private fun showToast(message: String) {
        Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
    }

    private fun setPhoneToSilentMode() {
        val silentModeStartIntent = Intent(this, SilentModeReceiver::class.java)
            .setAction(SilentModeReceiver.ACTION_START_SILENT_MODE)
        sendBroadcast(silentModeStartIntent)
        }


    private fun setSilentModeDuringJuharTimeRange() {
        val currentCalendar = Calendar.getInstance()
        val currentHour = currentCalendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = currentCalendar.get(Calendar.MINUTE)

        val juharStartHour = sharedPreferencesJuhar.getInt("juharStartHour", 0)
        val juharStartMinute = sharedPreferencesJuhar.getInt("juharStartMinute", 0)
        val juharEndHour = sharedPreferencesJuhar.getInt("juharEndHour", 0)
        val juharEndMinute = sharedPreferencesJuhar.getInt("juharEndMinute", 0)

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val silentModeStartIntent = Intent(this, SilentModeReceiver::class.java)
            .setAction(SilentModeReceiver.ACTION_START_SILENT_MODE)

        val silentModeStopIntent = Intent(this, SilentModeReceiver::class.java)
            .setAction(SilentModeReceiver.ACTION_STOP_SILENT_MODE)

        val startPendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            silentModeStartIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val stopPendingIntent = PendingIntent.getBroadcast(
            this,
            1,
            silentModeStopIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val startTimeMillis = calculateTimeMillis(juharStartHour, juharStartMinute)
        val endTimeMillis = calculateTimeMillis(juharEndHour, juharEndMinute)

        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP, startTimeMillis, startPendingIntent
        )

        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP, endTimeMillis, stopPendingIntent
        )

        if (binding.switchJuhar.isOn) {
            if (currentHour == juharStartHour && currentMinute == juharStartMinute) {
                setPhoneToSilentMode()
            } else if (currentHour == juharEndHour && currentMinute == juharEndMinute) {
                stopSilentMode()
            }
        }
    }


    private fun setSilentModeDuringAsarTimeRange() {
        val currentCalendar = Calendar.getInstance()
        val currentHour = currentCalendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = currentCalendar.get(Calendar.MINUTE)

        val asarStartHour = sharedPreferencesAsar.getInt("asarStartHour", 0)
        val asarStartMinute = sharedPreferencesAsar.getInt("asarStartMinute", 0)
        val asarEndHour = sharedPreferencesAsar.getInt("asarEndHour", 0)
        val asarEndMinute = sharedPreferencesAsar.getInt("asarEndMinute", 0)

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val silentModeStartIntent = Intent(this, SilentModeReceiver::class.java)
            .setAction(SilentModeReceiver.ACTION_START_SILENT_MODE)

        val silentModeStopIntent = Intent(this, SilentModeReceiver::class.java)
            .setAction(SilentModeReceiver.ACTION_STOP_SILENT_MODE)

        val startPendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            silentModeStartIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val stopPendingIntent = PendingIntent.getBroadcast(
            this,
            1,
            silentModeStopIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val startTimeMillis = calculateTimeMillis(asarStartHour, asarStartMinute)
        val endTimeMillis = calculateTimeMillis(asarEndHour, asarEndMinute)

        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP, startTimeMillis, startPendingIntent
        )

        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP, endTimeMillis, stopPendingIntent
        )

        if (binding.switchAsar.isOn) {
            if (currentHour == asarStartHour && currentMinute == asarStartMinute) {
                setPhoneToSilentMode()
            } else if (currentHour == asarEndHour && currentMinute == asarEndMinute) {
                stopSilentMode()
            }
        }
    }


    private fun setSilentModeDuringMagribTimeRange() {
        val currentCalendar = Calendar.getInstance()
        val currentHour = currentCalendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = currentCalendar.get(Calendar.MINUTE)

        val magribStartHour = sharedPreferencesMagrib.getInt("magribStartHour", 0)
        val magribStartMinute = sharedPreferencesMagrib.getInt("magribStartMinute", 0)
        val magribEndHour = sharedPreferencesMagrib.getInt("magribEndHour", 0)
        val magribEndMinute = sharedPreferencesMagrib.getInt("magribEndMinute", 0)

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val silentModeStartIntent = Intent(this, SilentModeReceiver::class.java)
            .setAction(SilentModeReceiver.ACTION_START_SILENT_MODE)

        val silentModeStopIntent = Intent(this, SilentModeReceiver::class.java)
            .setAction(SilentModeReceiver.ACTION_STOP_SILENT_MODE)

        val startPendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            silentModeStartIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val stopPendingIntent = PendingIntent.getBroadcast(
            this,
            1,
            silentModeStopIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val startTimeMillis = calculateTimeMillis(magribStartHour, magribStartMinute)
        val endTimeMillis = calculateTimeMillis(magribEndHour, magribEndMinute)

        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP, startTimeMillis, startPendingIntent
        )

        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP, endTimeMillis, stopPendingIntent
        )

        if (binding.switchMagrib.isOn) {
            if (currentHour == magribStartHour && currentMinute == magribStartMinute) {
                setPhoneToSilentMode()
            } else if (currentHour == magribEndHour && currentMinute == magribEndMinute) {
                stopSilentMode()
            }
        }
    }


    private fun setSilentModeDuringIshaTimeRange() {
        val currentCalendar = Calendar.getInstance()
        val currentHour = currentCalendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = currentCalendar.get(Calendar.MINUTE)

        val ishaStartHour = sharedPreferencesIsha.getInt("ishaStartHour", 0)
        val ishaStartMinute = sharedPreferencesIsha.getInt("ishaStartMinute", 0)
        val ishaEndHour = sharedPreferencesIsha.getInt("ishaEndHour", 0)
        val ishaEndMinute = sharedPreferencesIsha.getInt("ishaEndMinute", 0)

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val silentModeStartIntent = Intent(this, SilentModeReceiver::class.java)
            .setAction(SilentModeReceiver.ACTION_START_SILENT_MODE)

        val silentModeStopIntent = Intent(this, SilentModeReceiver::class.java)
            .setAction(SilentModeReceiver.ACTION_STOP_SILENT_MODE)

        val startPendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            silentModeStartIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val stopPendingIntent = PendingIntent.getBroadcast(
            this,
            1,
            silentModeStopIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val startTimeMillis = calculateTimeMillis(ishaStartHour, ishaStartMinute)
        val endTimeMillis = calculateTimeMillis(ishaEndHour, ishaEndMinute)

        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP, startTimeMillis, startPendingIntent
        )

        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP, endTimeMillis, stopPendingIntent
        )

        if (binding.switchIsha.isOn) {
            if (currentHour == ishaStartHour && currentMinute == ishaStartMinute) {
                setPhoneToSilentMode()
            } else if (currentHour == ishaEndHour && currentMinute == ishaEndMinute) {
                stopSilentMode()
            }
        }
    }


    /*private fun setSilentModeDuringJumuahTimeRange() {
        val currentCalendar = Calendar.getInstance()
        val currentHour = currentCalendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = currentCalendar.get(Calendar.MINUTE)

        val jumuahStartHour = sharedPreferencesJumuah.getInt("jumuahStartHour", 0)
        val jumuahStartMinute = sharedPreferencesJumuah.getInt("jumuahStartMinute", 0)
        val jumuahEndHour = sharedPreferencesJumuah.getInt("jumuahEndHour", 0)
        val jumuahEndMinute = sharedPreferencesJumuah.getInt("jumuahEndMinute", 0)

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val silentModeStartIntent = Intent(this, SilentModeReceiver::class.java)
            .setAction(SilentModeReceiver.ACTION_START_SILENT_MODE)

        val silentModeStopIntent = Intent(this, SilentModeReceiver::class.java)
            .setAction(SilentModeReceiver.ACTION_STOP_SILENT_MODE)

        val startPendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            silentModeStartIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val stopPendingIntent = PendingIntent.getBroadcast(
            this,
            1,
            silentModeStopIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val startTimeMillis = calculateTimeMillis(jumuahStartHour, jumuahStartMinute)
        val endTimeMillis = calculateTimeMillis(jumuahEndHour, jumuahEndMinute)

        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP, startTimeMillis, startPendingIntent
        )

        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP, endTimeMillis, stopPendingIntent
        )

    }*/


    private fun stopSilentMode() {
        val silentModeStopIntent = Intent(this, SilentModeReceiver::class.java)
            .setAction(SilentModeReceiver.ACTION_STOP_SILENT_MODE)
        sendBroadcast(silentModeStopIntent)
    }

    private fun saveStartTime(hour: Int, minute: Int) {
        sharedPreferencesFajar.edit().apply {
            putInt("startHour", hour)
            putInt("startMinute", minute)
            apply()
        }
    }

    private fun saveEndTime(hour: Int, minute: Int) {
        sharedPreferencesFajar.edit().apply {
            putInt("endHour", hour)
            putInt("endMinute", minute)
            apply()
        }
    }

    private fun saveJuharStartTime(hour: Int, minute: Int) {
        sharedPreferencesJuhar.edit().apply {
            putInt("juharStartHour", hour)
            putInt("juharStartMinute", minute)
            apply()
        }
    }

    private fun saveJuharEndTime(hour: Int, minute: Int) {
        sharedPreferencesJuhar.edit().apply {
            putInt("juharEndHour", hour)
            putInt("juharEndMinute", minute)
            apply()
        }
    }

    private fun saveAsarStartTime(hour: Int, minute: Int) {
        sharedPreferencesAsar.edit().apply {
            putInt("asarStartHour", hour)
            putInt("asarStartMinute", minute)
            apply()
        }
    }

    private fun saveAsarEndTime(hour: Int, minute: Int) {
        sharedPreferencesAsar.edit().apply {
            putInt("asarEndHour", hour)
            putInt("asarEndMinute", minute)
            apply()
        }
    }

    private fun saveMagribStartTime(hour: Int, minute: Int) {
        sharedPreferencesMagrib.edit().apply {
            putInt("magribStartHour", hour)
            putInt("magribStartMinute", minute)
            apply()
        }
    }

    private fun saveMagribEndTime(hour: Int, minute: Int) {
        sharedPreferencesMagrib.edit().apply {
            putInt("magribEndHour", hour)
            putInt("magribEndMinute", minute)
            apply()
        }
    }

    private fun saveIshaStartTime(hour: Int, minute: Int) {
        sharedPreferencesIsha.edit().apply {
            putInt("ishaStartHour", hour)
            putInt("ishaStartMinute", minute)
            apply()
        }
    }

    private fun saveIshaEndTime(hour: Int, minute: Int) {
        sharedPreferencesIsha.edit().apply {
            putInt("ishaEndHour", hour)
            putInt("ishaEndMinute", minute)
            apply()
        }
    }


/*    private fun saveJumuahStartTime(hour: Int, minute: Int) {
        sharedPreferencesJumuah.edit().apply {
            putInt("jumuahStartHour", hour)
            putInt("jumuahStartMinute", minute)
            apply()
        }
    }

    private fun saveJumuahEndTime(hour: Int, minute: Int) {
        sharedPreferencesJumuah.edit().apply {
            putInt("jumuahEndHour", hour)
            putInt("jumuahEndMinute", minute)
            apply()
        }
    }*/


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()

        /*checkNetworkConnection()*/

        updateTheme()

        updateSalahStatus()

        val switchState = sharedPreferencesFajar.getBoolean("switchState", false)
        binding.switchFajar.isOn = switchState

        val juharSwitchState = sharedPreferencesJuhar.getBoolean("juharSwitchState", false)
        binding.switchJuhar.isOn = juharSwitchState

        val asarSwitchState = sharedPreferencesAsar.getBoolean("asarSwitchState", false)
        binding.switchAsar.isOn = asarSwitchState

        val magribSwitchState = sharedPreferencesMagrib.getBoolean("magribSwitchState", false)
        binding.switchMagrib.isOn = magribSwitchState

        val ishaSwitchState = sharedPreferencesIsha.getBoolean("ishaSwitchState", false)
        binding.switchIsha.isOn = ishaSwitchState

        val savedStartTimeHour = sharedPreferencesFajar.getInt("startHour", 0)
        val savedStartTimeMinute = sharedPreferencesFajar.getInt("startMinute", 0)
        val savedEndTimeHour = sharedPreferencesFajar.getInt("endHour", 0)
        val savedEndTimeMinute = sharedPreferencesFajar.getInt("endMinute", 0)

        val savedJuharStartTimeHour = sharedPreferencesJuhar.getInt("juharStartHour", 0)
        val savedJuharStartTimeMinute = sharedPreferencesJuhar.getInt("juharStartMinute", 0)
        val savedJuharEndTimeHour = sharedPreferencesJuhar.getInt("juharEndHour", 0)
        val savedJuharEndTimeMinute = sharedPreferencesJuhar.getInt("juharEndMinute", 0)

        val savedAsarStartTimeHour = sharedPreferencesAsar.getInt("asarStartHour", 0)
        val savedAsarStartTimeMinute = sharedPreferencesAsar.getInt("asarStartMinute", 0)
        val savedAsarEndTimeHour = sharedPreferencesAsar.getInt("asarEndHour", 0)
        val savedAsarEndTimeMinute = sharedPreferencesAsar.getInt("asarEndMinute", 0)

        val savedMagribStartTimeHour = sharedPreferencesMagrib.getInt("magribStartHour", 0)
        val savedMagribStartTimeMinute = sharedPreferencesMagrib.getInt("magribStartMinute", 0)
        val savedMagribEndTimeHour = sharedPreferencesMagrib.getInt("magribEndHour", 0)
        val savedMagribEndTimeMinute = sharedPreferencesMagrib.getInt("magribEndMinute", 0)

        val savedIshaStartTimeHour = sharedPreferencesIsha.getInt("ishaStartHour", 0)
        val savedIshaStartTimeMinute = sharedPreferencesIsha.getInt("ishaStartMinute", 0)
        val savedIshaEndTimeHour = sharedPreferencesIsha.getInt("ishaEndHour", 0)
        val savedIshaEndTimeMinute = sharedPreferencesIsha.getInt("ishaEndMinute", 0)

        binding.fajarStartTime.text = formatTime(savedStartTimeHour, savedStartTimeMinute)
        binding.fajarEndTime.text = formatTime(savedEndTimeHour, savedEndTimeMinute)

        binding.juharStartTime.text = formatTime(savedJuharStartTimeHour, savedJuharStartTimeMinute)
        binding.juharEndTime.text = formatTime(savedJuharEndTimeHour, savedJuharEndTimeMinute)

        binding.asarStartTime.text = formatTime(savedAsarStartTimeHour, savedAsarStartTimeMinute)
        binding.asarEndTime.text = formatTime(savedAsarEndTimeHour, savedAsarEndTimeMinute)

        binding.magribStartTime.text =
            formatTime(savedMagribStartTimeHour, savedMagribStartTimeMinute)
        binding.magribEndTime.text = formatTime(savedMagribEndTimeHour, savedMagribEndTimeMinute)

        binding.ishaStartTime.text = formatTime(savedIshaStartTimeHour, savedIshaStartTimeMinute)
        binding.ishaEndTime.text = formatTime(savedIshaEndTimeHour, savedIshaEndTimeMinute)

       /* if (switchState) {
            setSilentModeDuringTimeRange()
        }

        if (juharSwitchState) {
            setSilentModeDuringJuharTimeRange()
        }

        if (asarSwitchState) {
            setSilentModeDuringAsarTimeRange()
        }

        if (magribSwitchState) {
            setSilentModeDuringMagribTimeRange()
        }

        if (ishaSwitchState) {
            setSilentModeDuringIshaTimeRange()
        }*/

        updateLocation()

        val currentLocation = binding.tvFindLocation.text.toString()
        saveLocationToSharedPreferences(currentLocation)

        registerReceiver(dataUpdateReceiver, IntentFilter("DATA_UPDATE_ACTION"))
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(dataUpdateReceiver)
    }

    private fun showInputDialog() {
        val builder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
        val dialogView = inflater.inflate(R.layout.location_input_dialog, null)
        val etLocationInput = dialogView.findViewById<EditText>(R.id.etLocationInput)
        val btnSubmit = dialogView.findViewById<Button>(R.id.btnFind)

        builder.setView(dialogView)
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        btnSubmit.setOnClickListener {
            val location = etLocationInput.text.toString()
            if (location.isEmpty()) {
                etLocationInput.hint = getString(R.string.please_enter_a_city_name)
            } else {
                dialog.dismiss()
                showLoadingAnimation()

                Handler(Looper.getMainLooper()).postDelayed({
                    hideLoadingAnimation()

                    binding.tvFindLocation.text = location
                    saveLocationToSharedPreferences(location)
                    fetchPrayerTimes(location)
                    /*showLocationUpdatedDialog()*/
                    /*locationFound = true*/

                }, 1000)
            }
        }

        builder.setNegativeButton("Cancel") { dialog, which ->
            dialog.dismiss()
        }

        dialog.window?.attributes?.windowAnimations = R.style.TimePickerDialogAnimation
        dialog.show()
    }

    private fun saveLocationToSharedPreferences(location: String) {
        val sharedPrefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        editor.putString("LocationKey", location)
        editor.apply()
    }

    private fun loadSavedLocationFromSharedPreferences(): String {
        val sharedPrefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        return sharedPrefs.getString("LocationKey", "") ?: ""
    }

    private fun fetchPrayerTimes(location: String) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = prayerTimesService.getPrayerTimes(location, apiKey)
                if (response.isSuccessful) {
                    val prayerTimesResponse = response.body()
                    if (prayerTimesResponse != null) {
                        val prayerTimesData = prayerTimesResponse.items.firstOrNull()
                        if (prayerTimesData != null) {
                            savePrayerTimesToSharedPreferences(prayerTimesData)

                            runOnUiThread {
                                fajartime?.text = "${prayerTimesData.fajr}"
                                juhartime?.text = "${prayerTimesData.dhuhr}"
                                asartime?.text = "${prayerTimesData.asr}"
                                magribtime?.text = "${prayerTimesData.maghrib}"
                                ishatime?.text = "${prayerTimesData.isha}"
                                sunrisetime?.text = "${prayerTimesData.shurooq}"

                                showTopSheetDialog(prayerTimesData)
                            }

                        }
                    }
                } else {

                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun fetchSalahTimes(location: String) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = prayerTimesService.getPrayerTimes(location, apiKey)
                if (response.isSuccessful) {
                    val prayerTimesResponse = response.body()
                    if (prayerTimesResponse != null) {
                        val prayerTimesData = prayerTimesResponse.items.firstOrNull()
                        if (prayerTimesData != null) {
                            savePrayerTimesToSharedPreferences(prayerTimesData)

                            runOnUiThread {
                                fajartime?.text = "${prayerTimesData.fajr}"
                                juhartime?.text = "${prayerTimesData.dhuhr}"
                                asartime?.text = "${prayerTimesData.asr}"
                                magribtime?.text = "${prayerTimesData.maghrib}"
                                ishatime?.text = "${prayerTimesData.isha}"
                                sunrisetime?.text = "${prayerTimesData.shurooq}"
                            }
                        }
                    }
                } else {

                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun showTopSheetDialog(prayerTimesData: PrayerTimesData) {
        val topSheetDialog = TopSheetDialog(this)
        topSheetDialog.setPrayerTimes(prayerTimesData)
        topSheetDialog.show()
    }

    private fun savePrayerTimesToSharedPreferences(prayerTimesData: PrayerTimesData) {
        val sharedPreferences = getSharedPreferences("PrayerTimes", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.putString("fajr", prayerTimesData.fajr)
        editor.putString("shurooq", prayerTimesData.shurooq)

        /*        // Calculate Duha start time (Shurooq + 15 minutes) and save it
                val duhaStartTime = addMinutes(prayerTimesData.shurooq, 15)
                editor.putString("duha", duhaStartTime)*/

        editor.putString("dhuhr", prayerTimesData.dhuhr)
        editor.putString("asr", prayerTimesData.asr)
        editor.putString("maghrib", prayerTimesData.maghrib)
        editor.putString("isha", prayerTimesData.isha)

        editor.apply()
    }

    private fun getTranslatedPrayerName(prayerName: String): String {
        val resourceId = when (prayerName.toLowerCase(Locale.ROOT)) {
            "fajr" -> R.string.fajr
            "shurooq" -> R.string.shurooq
            "dhuhr" -> R.string.dhuhr
            "asr" -> R.string.asr
            "maghrib" -> R.string.maghrib
            "isha" -> R.string.isha
            else -> R.string.loading
        }
        return getString(resourceId)
    }

    private fun updateSalahStatus() {
        val sharedPreferencesPrayerTimes = getSharedPreferences("PrayerTimes", Context.MODE_PRIVATE)
        val prayerTimes = mutableListOf<Pair<String, String>>()

        val prayerOrder = listOf("Fajr", "Shurooq", "Dhuhr", "Asr", "Maghrib", "Isha")

        val sdf24Hour = SimpleDateFormat("HH:mm", Locale.getDefault())
        val currentTimeFormatted = sdf24Hour.format(Date())

        for (prayerName in prayerOrder) {
            val prayerTime =
                sharedPreferencesPrayerTimes.getString(prayerName.toLowerCase(Locale.ROOT), "")
            if (!prayerTime.isNullOrEmpty()) {
                val formattedTime = convertTo24Hour(prayerTime)
                prayerTimes.add(formattedTime to prayerName)
            }
        }

        prayerTimes.sortBy { it.first }

        if (prayerTimes.isEmpty()) {
            return
        }

        var currentPrayerIndex = -1
        var nextPrayerIndex = -1

        for (i in 0 until prayerTimes.size) {
            val currentPrayerTime = prayerTimes[i].first
            if (currentTimeFormatted < currentPrayerTime) {
                nextPrayerIndex = i
                break
            }
            currentPrayerIndex = i
        }

        if (currentPrayerIndex != -1) {
            val currentPrayerPair = prayerTimes[currentPrayerIndex]
            val nextPrayerPair = prayerTimes[0]

            binding.tvNowSalah.text = getTranslatedPrayerName(currentPrayerPair.second)
            binding.tvNowSalahTime.text = "${convertTo12Hour(currentPrayerPair.first)}"

            if (currentPrayerPair.second.equals("Shurooq", ignoreCase = true)) {
                val duhaStartTime = addMinutes(currentPrayerPair.first, 15)
                binding.tvNowSalahEndTime.text = "${convertTo12Hour(duhaStartTime)}"
            } else if (currentPrayerPair.second.equals("Asr", ignoreCase = true)) {
                val asrEndTime = subtractMinutes(prayerTimes[nextPrayerIndex].first, 15)
                binding.tvNowSalahEndTime.text = "${convertTo12Hour(asrEndTime)}"
            } else if (currentPrayerPair.second.equals("Isha", ignoreCase = true)) {
                val ishaEndTime = subtractMinutes(nextPrayerPair.first, 1)
                binding.tvNowSalahEndTime.text = "${convertTo12Hour(ishaEndTime)}"
            } else {
                if (nextPrayerIndex != -1) {
                    val nextPrayerPair = prayerTimes[nextPrayerIndex]
                    val nextPrayerTimeMinusOneMinute = subtractMinutes(nextPrayerPair.first, 1)
                    binding.tvNowSalahEndTime.text =
                        "${convertTo12Hour(nextPrayerTimeMinusOneMinute)}"
                }
            }
        }

        if (currentPrayerIndex == -1) {
            val currentPrayerPair = prayerTimes[5]
            val nextPrayerPair = prayerTimes[0]

            if (currentPrayerPair.second.equals("Isha", ignoreCase = true)) {
                binding.tvNowSalah.text = getTranslatedPrayerName(currentPrayerPair.second)
                binding.tvNowSalahTime.text = "${convertTo12Hour(currentPrayerPair.first)}"
                val ishaEndTime = subtractMinutes(nextPrayerPair.first, 1)
                binding.tvNowSalahEndTime.text = "${convertTo12Hour(ishaEndTime)}"
            }
        }

        if (nextPrayerIndex != -1) {
            val nextPrayerPair = prayerTimes[nextPrayerIndex]

            binding.tvNextSalah.text = getTranslatedPrayerName(nextPrayerPair.second)
            binding.tvNextSalahTime.text = "${convertTo12Hour(nextPrayerPair.first)}"

            if (nextPrayerPair.second.equals("Shurooq", ignoreCase = true)) {
                val shurooqEndTime = addMinutes(nextPrayerPair.first, 15)
                binding.tvNextSalahEndTime.text = "${convertTo12Hour(shurooqEndTime)}"
            } else if (nextPrayerPair.second.equals("Asr", ignoreCase = true)) {
                val nextNextPrayerIndex = (nextPrayerIndex + 1) % prayerTimes.size
                val asrEndTime = subtractMinutes(prayerTimes[nextNextPrayerIndex].first, 15)
                binding.tvNextSalahEndTime.text = "${convertTo12Hour(asrEndTime)}"
            } else {
                val nextNextPrayerIndex = (nextPrayerIndex + 1) % prayerTimes.size
                val nextNextPrayerPair = prayerTimes[nextNextPrayerIndex]
                val nextNextPrayerTimeMinusOneMinute = subtractMinutes(nextNextPrayerPair.first, 1)

                binding.tvNextSalahEndTime.text =
                    "${convertTo12Hour(nextNextPrayerTimeMinusOneMinute)}"

            }

        } else if (currentPrayerIndex == prayerTimes.size - 1) {
            val currentPrayerPair = prayerTimes[currentPrayerIndex]
            val nextPrayerPairEnd = prayerTimes[0]
            val ishaEndTime = subtractMinutes(nextPrayerPairEnd.first, 1)
            binding.tvNowSalah.text = getTranslatedPrayerName(currentPrayerPair.second)
            binding.tvNowSalahTime.text = "${convertTo12Hour(currentPrayerPair.first)}"
            binding.tvNowSalahEndTime.text = "${convertTo12Hour(ishaEndTime)}"

            val nextPrayerPair = prayerTimes[0]

            binding.tvNextSalah.text = getTranslatedPrayerName(nextPrayerPair.second)
            binding.tvNextSalahTime.text = "${convertTo12Hour(nextPrayerPair.first)}"

            val nextNextPrayerIndex = (nextPrayerIndex + 2) % prayerTimes.size
            val nextNextPrayerPair = prayerTimes[nextNextPrayerIndex]
            val nextNextPrayerTimeMinusOneMinute = subtractMinutes(nextNextPrayerPair.first, 1)

            binding.tvNextSalahEndTime.text = "${convertTo12Hour(nextNextPrayerTimeMinusOneMinute)}"
        }

        Handler(Looper.getMainLooper()).postDelayed({ updateSalahStatus() }, 1000)

        val intent = Intent("DATA_UPDATE_ACTION")
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    fun addMinutes(time: String?, minutes: Int): String {
        // Check if the time parameter is null or blank
        if (time.isNullOrBlank()) {
            // Handle the case where the input time is null or empty
            return ""
        }

        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())

        try {
            val date = sdf.parse(time)
            if (date != null) {
                val calendar = Calendar.getInstance()
                calendar.time = date
                calendar.add(Calendar.MINUTE, minutes)
                return sdf.format(calendar.time)
            } else {
                // Handle the case where parsing fails
                return ""
            }
        } catch (e: ParseException) {
            // Handle the case where parsing fails due to an exception
            return ""
        }
    }



    fun subtractMinutes(time: String?, minutes: Int): String {
        // Check if the time parameter is null or blank
        if (time.isNullOrBlank()) {
            // Handle the case where the input time is null or empty
            return ""
        }

        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())

        try {
            val date = sdf.parse(time)
            if (date != null) {
                val calendar = Calendar.getInstance()
                calendar.time = date
                calendar.add(Calendar.MINUTE, -minutes)
                return sdf.format(calendar.time)
            } else {
                // Handle the case where parsing fails
                return ""
            }
        } catch (e: ParseException) {
            // Handle the case where parsing fails due to an exception
            return ""
        }
    }



    private fun convertTo24Hour(time: String): String {
        try {
            if (time.isNotEmpty()) {
                val sdf12Hour = SimpleDateFormat("hh:mm a", Locale.getDefault())
                val sdf24Hour = SimpleDateFormat("HH:mm", Locale.getDefault())
                val date = sdf12Hour.parse(time)
                return date?.let { sdf24Hour.format(it) } ?: ""
            } else {
                return ""
            }
        } catch (e: ParseException) {
            e.printStackTrace()
            return ""
        }
    }

    private fun convertTo12Hour(time: String): String {
        val sdf24Hour = SimpleDateFormat("HH:mm", Locale.getDefault())
        val sdf12Hour = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val date = sdf24Hour.parse(time)
        return date?.let { sdf12Hour.format(it) } ?: ""
    }

    /*    private fun updateTime() {
        val dateFormat = SimpleDateFormat("hh:mm:ss a", Locale.getDefault())
        val currentTime = dateFormat.format(Date())
        binding.tvNormalTime.text = currentTime

        val currentTimeMillis = System.currentTimeMillis()
        val nextSecondMillis = (currentTimeMillis / 1000 + 1) * 1000
        val delayMillis = nextSecondMillis - currentTimeMillis

        Handler(Looper.getMainLooper()).postDelayed(
            { updateTime() }, delayMillis
        )
    }*/


    private fun waqtRemainTime() {
        val weakRefHandler = Handler(Looper.getMainLooper()) { weakRefRunnable ->
            val runnable = (weakRefRunnable.obj as? Runnable)
            runnable?.run()
            true
        }

        val weakRefRunnable = WeakReference(object : Runnable {
            override fun run() {
                updateTextView()
                remainingTimeHandler?.postDelayed(this, 1000) // Update every second
            }
        })

        remainingTimeHandler = weakRefHandler
        weakRefRunnable.get()?.let { remainingTimeHandler?.post(it) }
    }


    private fun updateTextView() {
        // Your existing code to calculate the current prayer and remaining time
        val currentPrayer = getCurrentPrayer()

        // Check if the language is Bengali ('bn') and if the current prayer is 'Isha'
        if (currentPrayer?.first?.toLowerCase(Locale.ROOT) == "isha" &&
            Locale.getDefault().language == "bn") {
            // Skip updating the text for 'Isha' prayer in Bengali language
            return
        }

        currentPrayer?.let { (prayerName, prayerTime) ->
            val remainingTime = calculateTimeRemaining(getCurrentTime(), prayerTime)

            // Fetch the prayer name from resources
            val prayerNameString = getString(getPrayerNameResId(prayerName))

            // Fetch the formatted string from resources directly
            val formattedString = getString(R.string.prayer_time_template, prayerNameString, remainingTime)

            binding.tvNormalTime.text = formattedString
        }
    }


    private fun getPrayerNameResId(prayerName: String): Int {
        return when (prayerName.toLowerCase(Locale.ROOT)) {
            "fajr" -> R.string.fajr
            "shurooq" -> R.string.shurooq
            "dhuhr" -> R.string.dhuhr
            "asr" -> R.string.asr
            "maghrib" -> R.string.maghrib
            "isha" -> R.string.isha
            "dhuha" -> R.string.dhuha
            "forbidden time (sunrise)" -> R.string.forbidden_time_sunrise
            "forbidden time (zawaal)" -> R.string.forbidden_time_zawaal
            "forbidden time (sunset)" -> R.string.forbidden_time_sunset
            else -> R.string.loading
        }
    }


    private fun getCurrentPrayer(): Pair<String, String>? {
        val sharedPreferencesPrayerTimes =
            getSharedPreferences("PrayerTimes", Context.MODE_PRIVATE)

        val fajr = sharedPreferencesPrayerTimes.getString("fajr", "")
        val sunrise = sharedPreferencesPrayerTimes.getString("shurooq", "")
        val dhuhr = sharedPreferencesPrayerTimes.getString("dhuhr", "")
        val asr = sharedPreferencesPrayerTimes.getString("asr", "")
        val maghrib = sharedPreferencesPrayerTimes.getString("maghrib", "")
        val isha = sharedPreferencesPrayerTimes.getString("isha", "")

        val currentTime = getCurrentTime()
        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val formattedCurrentTime = convertTo24HourWaqt(currentTime)

        return try {
            when {
                formattedCurrentTime >= convertTo24HourWaqt(fajr)
                        && formattedCurrentTime < convertTo24HourWaqt(sunrise) -> "Fajr" to sunrise.let {
                    subtractMinutes(it!!, 1)
                }

                formattedCurrentTime >= convertTo24HourWaqt(sunrise)
                        && formattedCurrentTime < convertTo24HourWaqt(sunrise).let {
                    addMinutes(it, 15)
                } -> "Forbidden Time (Sunrise)" to sunrise.let {
                    addMinutes(it!!, 15)
                }

                formattedCurrentTime >= convertTo24HourWaqt(sunrise).let {
                    addMinutes(it, 16)
                } && formattedCurrentTime < convertTo24HourWaqt(dhuhr).let {
                    subtractMinutes(it, 7)
                } -> "Dhuha" to dhuhr.let {
                    subtractMinutes(it!!, 7)
                }

                formattedCurrentTime >= convertTo24HourWaqt(dhuhr).let {
                    subtractMinutes(it, 6)
                } && formattedCurrentTime < convertTo24HourWaqt(dhuhr).let {
                    subtractMinutes(it, 1)
                } -> "Forbidden Time (Zawaal)" to dhuhr.let {
                    subtractMinutes(it!!, 1)
                }

                currentHour < 13 && formattedCurrentTime > convertTo24HourWaqt(dhuhr)
                        && formattedCurrentTime < convertTo24HourWaqt(asr) -> {
                    val modifiedAsrTime = asr.let {
                        subtractMinutes(it!!, 1)
                    }
                    val twelveHoursBeforeAsr = subtractHours(modifiedAsrTime, 12)

                    "Dhuhr" to twelveHoursBeforeAsr
                }

                currentHour >= 13 && formattedCurrentTime > convertTo24HourWaqt(dhuhr)
                        && formattedCurrentTime < convertTo24HourWaqt(asr) -> "Dhuhr" to asr.let {
                    subtractMinutes(it!!, 1)
                }

                /*currentDayOfWeek == Calendar.FRIDAY &&  currentHour >= 13 && formattedCurrentTime > convertTo24HourWaqt(dhuhr)
                        && formattedCurrentTime < convertTo24HourWaqt(asr) -> "Jumuah" to asr.let {
                    subtractMinutes(it!!, 1)
                }

                currentDayOfWeek == Calendar.FRIDAY &&  currentHour < 13 && formattedCurrentTime > convertTo24HourWaqt(dhuhr)
                        && formattedCurrentTime < convertTo24HourWaqt(asr) -> {
                    val modifiedAsrTime = asr.let {
                        subtractMinutes(it!!, 1)
                    }
                    val twelveHoursBeforeAsr = subtractHours(modifiedAsrTime, 12)

                    "Jumuah" to twelveHoursBeforeAsr
                }*/

                formattedCurrentTime >= convertTo24HourWaqt(asr)
                        && formattedCurrentTime < convertTo24HourWaqt(maghrib).let {
                    subtractMinutes(it, 16)
                } -> "Asr" to maghrib.let {
                    subtractMinutes(it!!, 16)
                }

                formattedCurrentTime >= convertTo24HourWaqt(maghrib).let {
                    subtractMinutes(it, 15)
                } && formattedCurrentTime < convertTo24HourWaqt(maghrib).let {
                    subtractMinutes(it, 1)
                } -> "Forbidden Time (Sunset)" to maghrib.let {
                    subtractMinutes(it!!, 1)
                }

                formattedCurrentTime >= convertTo24HourWaqt(maghrib) &&
                        formattedCurrentTime < convertTo24HourWaqt(
                    isha
                ).let {
                    subtractMinutes(it, 1)
                } -> "Maghrib" to isha.let {
                    subtractMinutes(it!!, 1)
                }

                formattedCurrentTime >= convertTo24HourWaqt(isha) ||
                        formattedCurrentTime < convertTo24HourWaqt(fajr).let {
                    subtractMinutes(it, 1)
                } -> {
                    val modifiedFajrTime = fajr.let {
                        subtractMinutes(it, 1)
                    }
                    val twelveHoursBeforeFajr = subtractHours(modifiedFajrTime, 12)

                    val isAfterMidnight = formattedCurrentTime >= "00:00" && formattedCurrentTime < "01:00"
                    val isBeforeMidnight = formattedCurrentTime >= convertTo24HourWaqt(isha) && formattedCurrentTime <= "23:59"

                    val finalIshaTime = when {
                        isAfterMidnight -> {
                            // Subtract 12 hours for the midnight hour
                            subtractHours(twelveHoursBeforeFajr, 24)
                        }
                        isBeforeMidnight -> {
                            // Subtract 12 more hours before midnight
                            subtractHours(modifiedFajrTime, 36)
                        }
                        else -> {
                            subtractHours(modifiedFajrTime, 24)
                        }
                    }

                    "Isha" to finalIshaTime
                }

                else -> null
            }
        } catch (e: ParseException) {
            // Handle the ParseException here, log or perform any necessary action
            null
        }
    }


    private fun calculateTimeRemaining(currentTime: String?, prayerTime: String?): String {
        // Check if either currentTime or prayerTime is null or blank
        if (currentTime.isNullOrBlank() || prayerTime.isNullOrBlank()) {
            // Handle the case where either currentTime or prayerTime is null or empty
            return ""
        }

        val sdf24Hour = SimpleDateFormat("HH:mm", Locale.getDefault())

        try {
            val currentDate = sdf24Hour.parse(currentTime)
            val prayerDate = sdf24Hour.parse(prayerTime)

            if (currentDate != null && prayerDate != null) {
                val diffInMillis =
                    (prayerDate.time - currentDate.time + 24 * 60 * 60 * 1000) % (24 * 60 * 60 * 1000)
                val minutesRemaining = TimeUnit.MILLISECONDS.toMinutes(diffInMillis)

                return when {
                    minutesRemaining > 0 -> {
                        when {
                            minutesRemaining >= 60 -> {
                                val hours = minutesRemaining / 60
                                val remainingMinutes = minutesRemaining % 60
                                val hoursLabel = if (hours == 1L) "h" else "h"
                                val minutesLabel = if (remainingMinutes == 1L) "m" else "m"
                                "$hours $hoursLabel $remainingMinutes $minutesLabel"
                            }
                            else -> {
                                val minutesLabel = if (minutesRemaining == 1L) "m" else "m"
                                "$minutesRemaining $minutesLabel"
                            }
                        }
                    }
                    else -> ""
                }
            }
        } catch (e: java.text.ParseException) {
            // Handle the case where parsing fails due to an exception
        }

        return ""
    }


    private fun subtractHours(time: String?, hours: Int): String {
        // Check if the time parameter is null or blank
        if (time.isNullOrBlank()) {
            // Handle the case where the input time is null or empty
            return ""
        }

        val sdf24Hour = SimpleDateFormat("HH:mm", Locale.getDefault())

        try {
            val date = sdf24Hour.parse(time)
            if (date != null) {
                val modifiedDate = Date(date.time - hours * 60 * 60 * 1000)
                return sdf24Hour.format(modifiedDate)
            } else {
                // Handle the case where parsing fails
                return ""
            }
        } catch (e: java.text.ParseException) {
            // Handle the case where parsing fails due to an exception
            return ""
        }
    }


    private fun convertTo24HourWaqt(time: String?): String {
        if (time.isNullOrEmpty()) {
            return ""
        }

        val sdf12Hour = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val sdf24Hour = SimpleDateFormat("HH:mm", Locale.getDefault())
        try {
            val date: Date? = sdf12Hour.parse(time)
            return if (date != null) sdf24Hour.format(date) else ""
        } catch (e: ParseException) {

        }
        return ""
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onDestroy() {
        super.onDestroy()

        binding.lottieActive?.cancelAnimation()
        binding.lottieInActive?.cancelAnimation()

        Handler(Looper.getMainLooper()).removeCallbacksAndMessages(null)

        remainingTimeRunnable?.let {
            remainingTimeHandler?.removeCallbacks(it)
        }

        sharedPreferencesThemeUpdate.unregisterOnSharedPreferenceChangeListener(sharedPreferencesListener)

        // Remove the update theme runnable
        handler.removeCallbacks(updateThemeRunnable)

        /*LocalBroadcastManager.getInstance(this).unregisterReceiver(prayerUpdateReceiver)*/
        unregisterReceiver(connectivityReceiver)

        handlerThread.quitSafely()
    }


    override fun onBackPressed() {
        if (backPressedOnce) {
            super.onBackPressed()
        } else {
            val snackbar = Snackbar.make(
                findViewById(android.R.id.content),
                getString(R.string.press_back_again_to_exit),
                Snackbar.LENGTH_SHORT
            )
            snackbar.setTextColor(Color.WHITE)
            snackbar.setBackgroundTint(Color.BLACK)

            snackbar.show()

            snackbar.duration = Snackbar.LENGTH_SHORT

            backPressedOnce = true
            lastBackPressedTimestamp = System.currentTimeMillis()

            Handler(Looper.getMainLooper()).postDelayed({
                backPressedOnce = false
                lastBackPressedTimestamp = 0L
            }, 2000)
        }
        overridePendingTransition(R.anim.fade_in_reverse, R.anim.fade_out_reverse)
    }

    private fun setOnClickListener(view: ImageView, screenWidth: Int, duration: Long) {
        view.setOnClickListener {
            if (!isPaused) {
                pauseAnimation(view)
            } else {
                resumeAnimation(view, screenWidth, duration)
            }
            isPaused = !isPaused
        }

        moveViewContinuously(view, screenWidth, duration)
    }

    private fun moveViewContinuously(view: ImageView, screenWidth: Int, duration: Long) {
        val valueAnimator = ValueAnimator.ofFloat(0f, 1f)
        valueAnimator.addUpdateListener { animation ->
            val value = animation.animatedValue as Float
            val translationX = (value * screenWidth).toInt()
            view.translationX = translationX.toFloat()
        }
        valueAnimator.repeatCount = ValueAnimator.INFINITE
        valueAnimator.duration = duration
        valueAnimator.start()
    }

    private fun pauseAnimation(view: ImageView) {
        view.clearAnimation()
    }

    private fun resumeAnimation(view: ImageView, screenWidth: Int, duration: Long) {
        moveViewContinuously(view, screenWidth, duration)
    }

    private fun fadeText(textView: TextView) {
        val fadeAnimator = ValueAnimator.ofFloat(0f, 1f)
        fadeAnimator.repeatCount = ValueAnimator.INFINITE
        fadeAnimator.repeatMode = ValueAnimator.REVERSE
        fadeAnimator.duration = 3000

        fadeAnimator.addUpdateListener { animation ->
            val animatedValue = animation.animatedValue as Float
            textView.alpha = animatedValue
        }

        fadeAnimator.start()
    }

    private fun showLoadingAnimation() {
        loadingManualAnimation.visibility = View.VISIBLE
        loadingManualAnimation.repeatCount = LottieDrawable.INFINITE
        loadingManualAnimation.playAnimation()
    }

    private fun hideLoadingAnimation() {
        loadingManualAnimation.visibility = View.GONE
        loadingManualAnimation.cancelAnimation()
    }

    private fun checkDndPermission() {
        if (!isDndPermissionGranted()) {
            showDndPermissionDialog()
        }
    }

    private fun isDndPermissionGranted(): Boolean {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return notificationManager.isNotificationPolicyAccessGranted
        } else {
            // For versions below Marshmallow, check if DND mode is enabled
            val zenMode = Settings.Global.getInt(
                contentResolver,
                "zen_mode",
                0
            )
            // Use the actual constant value for ZEN_MODE_OFF
            val zenModeOff = 0
            return zenMode != zenModeOff
        }
    }


    private fun isDndPermissionDialogShown(): Boolean {
        return sharedPreferences.getBoolean("DND_PERMISSION_DIALOG_SHOWN", false)
    }

    private fun setDndPermissionDialogShown() {
        sharedPreferences.edit().putBoolean("DND_PERMISSION_DIALOG_SHOWN", true).apply()
    }

    private fun showDndPermissionDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_custom, null)
        val dialogTitle = dialogView.findViewById<TextView>(R.id.dialog_title)
        val dialogMessage = dialogView.findViewById<TextView>(R.id.dialog_message)
        val btnGrant = dialogView.findViewById<Button>(R.id.btnGrant)
        val btnDeny = dialogView.findViewById<Button>(R.id.btnDeny)

        dialogTitle.setTextColor(Color.WHITE)
        dialogMessage.setTextColor(Color.WHITE)

        val dialog = AlertDialog.Builder(this, R.style.CustomAlertDialog)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        dialog.show()

        btnGrant.setOnClickListener {
            openDndPermissionSettings()
            setDndPermissionDialogShown()
            dialog.dismiss()
        }

        btnDeny.setOnClickListener {
            setDndPermissionDialogShown()
            dialog.dismiss()
        }

        val window = dialog.window
        window?.setBackgroundDrawableResource(android.R.color.transparent)
    }


    private fun openDndPermissionSettings() {
        val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
        } else {
            Intent(Settings.ACTION_SOUND_SETTINGS)
        }
        startActivity(intent)
    }
}

class AlarmReceiver : BroadcastReceiver() {

    private val REQUEST_CODE_PERMISSION = 114

    override fun onReceive(context: Context, intent: Intent) {

        val sharedPreferencesPrayerTimes =
            context.getSharedPreferences("PrayerTimes", Context.MODE_PRIVATE)


        val fajr = sharedPreferencesPrayerTimes.getString("fajr", "")
        val sunrise = sharedPreferencesPrayerTimes.getString("shurooq", "")
        val dhuhr = sharedPreferencesPrayerTimes.getString("dhuhr", "")
        val asr = sharedPreferencesPrayerTimes.getString("asr", "")
        val maghrib = sharedPreferencesPrayerTimes.getString("maghrib", "")
        val isha = sharedPreferencesPrayerTimes.getString("isha", "")

        // Ensure that prayer times are not null or blank before using them
        if (fajr.isNullOrBlank() || sunrise.isNullOrBlank() || dhuhr.isNullOrBlank() ||
            asr.isNullOrBlank() || maghrib.isNullOrBlank() || isha.isNullOrBlank()
        ) {
            // Handle the case where one or more prayer times are null or blank
            return
        }

        val currentTime = getCurrentTime()
        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        /*val currentDayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)*/
        val formattedCurrentTime = convertTo24Hour(currentTime)

        val currentPrayer = try {
            when {
                formattedCurrentTime >= convertTo24Hour(fajr)
                        && formattedCurrentTime < convertTo24Hour(sunrise) -> "Fajr" to sunrise.let {
                    subtractMinutes(it, 1)
                }

                formattedCurrentTime >= convertTo24Hour(sunrise)
                        && formattedCurrentTime < convertTo24Hour(sunrise).let {
                    addMinutes(it, 15)
                } -> "Forbidden Time (Sunrise)" to sunrise.let {
                    addMinutes(it, 15)
                }

                formattedCurrentTime >= convertTo24Hour(sunrise).let {
                    addMinutes(it, 16)
                } && formattedCurrentTime < convertTo24Hour(dhuhr).let {
                    subtractMinutes(it, 7)
                } -> "Dhuha" to dhuhr.let {
                    subtractMinutes(it, 7)
                }

                formattedCurrentTime >= convertTo24Hour(dhuhr).let {
                    subtractMinutes(it, 6)
                } && formattedCurrentTime < convertTo24Hour(dhuhr).let {
                    subtractMinutes(it, 1)
                } -> "Forbidden Time (Zawaal)" to dhuhr.let {
                    subtractMinutes(it, 1)
                }

                /*currentDayOfWeek != Calendar.FRIDAY &&*/ currentHour < 13 && formattedCurrentTime > convertTo24Hour(dhuhr)
                        && formattedCurrentTime < convertTo24Hour(asr) -> {
                    val modifiedAsrTime = asr.let {
                        subtractMinutes(it, 1)
                    }
                    val twelveHoursBeforeAsr = subtractHours(modifiedAsrTime, 12)

                    "Dhuhr" to twelveHoursBeforeAsr
                }

                /*currentDayOfWeek != Calendar.FRIDAY &&*/ currentHour >= 13 && formattedCurrentTime > convertTo24Hour(dhuhr)
                        && formattedCurrentTime < convertTo24Hour(asr) -> "Dhuhr" to asr.let {
                    subtractMinutes(it, 1)
                }

                /*currentDayOfWeek == Calendar.FRIDAY && currentHour >= 13 && formattedCurrentTime > convertTo24Hour(dhuhr)
                        && formattedCurrentTime < convertTo24Hour(asr) -> "Jumuah" to asr.let {
                    subtractMinutes(it, 1)
                }

                currentDayOfWeek == Calendar.FRIDAY && currentHour < 13 && formattedCurrentTime > convertTo24Hour(dhuhr)
                        && formattedCurrentTime < convertTo24Hour(asr) -> {
                    val modifiedAsrTime = asr.let {
                        subtractMinutes(it, 1)
                    }
                    val twelveHoursBeforeAsr = subtractHours(modifiedAsrTime, 12)

                    "Jumuah" to twelveHoursBeforeAsr
                }*/

                formattedCurrentTime >= convertTo24Hour(asr)
                        && formattedCurrentTime < convertTo24Hour(maghrib).let {
                    subtractMinutes(it, 16)
                } -> "Asr" to maghrib.let {
                    subtractMinutes(it, 16)
                }

                formattedCurrentTime >= convertTo24Hour(maghrib).let {
                    subtractMinutes(it, 15)
                } && formattedCurrentTime < convertTo24Hour(maghrib).let {
                    subtractMinutes(it, 1)
                } -> "Forbidden Time (Sunset)" to maghrib.let {
                    subtractMinutes(it, 1)
                }

                formattedCurrentTime >= convertTo24Hour(maghrib)
                        && formattedCurrentTime < convertTo24Hour(isha).let {
                    subtractMinutes(it, 1)
                } -> "Maghrib" to isha.let {
                    subtractMinutes(it, 1)
                }

                formattedCurrentTime >= convertTo24Hour(isha) ||
                        formattedCurrentTime < convertTo24Hour(fajr).let {
                    subtractMinutes(it, 1)
                } -> {
                    val modifiedFajrTime = fajr.let {
                        subtractMinutes(it, 1)
                    }
                    val twelveHoursBeforeFajr = subtractHours(modifiedFajrTime, 12)

                    val isAfterMidnight = formattedCurrentTime >= "00:00" && formattedCurrentTime < "01:00"
                    val isBeforeMidnight = formattedCurrentTime >= convertTo24Hour(isha) && formattedCurrentTime <= "23:59"

                    val finalIshaTime = when {
                        isAfterMidnight -> {
                            // Subtract 12 hours for the midnight hour
                            subtractHours(twelveHoursBeforeFajr, 24)
                        }
                        isBeforeMidnight -> {
                            // Subtract 12 more hours before midnight
                            subtractHours(modifiedFajrTime, 36)
                        }
                        else -> {
                            subtractHours(modifiedFajrTime, 24)
                        }
                    }

                    "Isha" to finalIshaTime
                }

                else -> null
            }
        } catch (e: ParseException) {
            // Handle the ParseException here, log or perform any necessary action
            null
        }

        currentPrayer?.let { (prayerName, prayerTime) ->
            if (currentPrayer.first.toLowerCase(Locale.ROOT) == "isha" &&
                Locale.getDefault().language == "bn") {
                // Skip updating the text for 'Isha' prayer in Bengali language
                return
            }
            val remainingTime = calculateTimeRemaining(currentTime, prayerTime)
            val notificationText = "waqt $remainingTime left (Approx)"
            showNotification(context, prayerName, notificationText, /*getPrayerIconResId(prayerName)*/)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.SCHEDULE_EXACT_ALARM
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Request the permission
                if (context is Activity) {
                    ActivityCompat.requestPermissions(
                        context,
                        arrayOf(Manifest.permission.SCHEDULE_EXACT_ALARM),
                        REQUEST_CODE_PERMISSION
                    )
                } else {
                    // Handle the case where the context is not an instance of Activity
                    // You may show a message or take appropriate action for non-activity contexts
                }
            } else {
                // Permission already granted, proceed with setting the exact alarm
                scheduleNextAlarm(context)
            }
        } else {
            // For devices below Android 12, no runtime permission is required
            scheduleNextAlarm(context)
        }


    }

    private fun calculateTimeRemaining(currentTime: String, prayerTime: String): String {
        val sdf24Hour = SimpleDateFormat("HH:mm", Locale.getDefault())
        val currentDate = sdf24Hour.parse(currentTime)
        val prayerDate = sdf24Hour.parse(prayerTime)

        if (currentDate != null && prayerDate != null) {

            val diffInMillis = (prayerDate.time - currentDate.time + 24 * 60 * 60 * 1000) % (24 * 60 * 60 * 1000)
            val minutesRemaining = TimeUnit.MILLISECONDS.toMinutes(diffInMillis)

            return when {
                minutesRemaining > 0 -> {
                    when {
                        minutesRemaining >= 60 -> {
                            val hours = minutesRemaining / 60
                            val remainingMinutes = minutesRemaining % 60
                            val hoursLabel = if (hours == 1L) "h" else "h"
                            val minutesLabel = if (remainingMinutes == 1L) "m" else "m"
                            "$hours $hoursLabel $remainingMinutes $minutesLabel"
                        }
                        else -> {
                            val minutesLabel = if (minutesRemaining == 1L) "m" else "m"
                            "$minutesRemaining $minutesLabel"
                        }
                    }
                }
                else -> ""
            }
        }

        return ""
    }

    private fun subtractHours(time: String, hours: Int): String {
        val sdf24Hour = SimpleDateFormat("HH:mm", Locale.getDefault())
        val date = sdf24Hour.parse(time)

        if (date != null) {
            val modifiedDate = Date(date.time - hours * 60 * 60 * 1000)
            return sdf24Hour.format(modifiedDate)
        }

        // Handle the case where the input time is not valid
        return time
    }


    /*private fun getPrayerIconResId(prayerName: String): Int {
        return when (prayerName.toLowerCase(Locale.getDefault())) {
            "fajr" -> R.drawable.ic_fajar
            "dhuhr", "jumuah" -> R.drawable.ic_juhar
            "asr" -> R.drawable.ic_asar
            "maghrib" -> R.drawable.ic_magrib
            "isha" -> R.drawable.ic_isha
            else -> R.drawable.ic_juhar // Default icon
        }
    }*/

    private fun subtractMinutes(time: String, minutes: Int): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        val date = sdf.parse(time)

        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(Calendar.MINUTE, -minutes)

        return sdf.format(calendar.time)
    }

   private fun addMinutes(time: String, minutes: Int): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        val date = sdf.parse(time)
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(Calendar.MINUTE, minutes)
        return sdf.format(calendar.time)
    }

    private fun getCurrentTime(): String {
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val currentTime: Date = Date()
        return sdf.format(currentTime)
    }

    private fun convertTo24Hour(time: String?): String {
        if (time.isNullOrEmpty()) {
            return ""
        }

        val sdf12Hour = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val sdf24Hour = SimpleDateFormat("HH:mm", Locale.getDefault())
        try {
            val date: Date? = sdf12Hour.parse(time)
            return if (date != null) sdf24Hour.format(date) else ""
        } catch (e: ParseException) {

        }
        return ""
    }

    private fun showNotification(context: Context, title: String, text: String) {
        // Create an explicit intent for launching the main activity
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        // Create a PendingIntent to be triggered when the notification is clicked
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE // Add the required flag
        )

        // Show the notification
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create a notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "default",
                "Default Channel",
                NotificationManager.IMPORTANCE_MIN // Set the importance to LOW or MIN
            )
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(context, "default")
            .setSmallIcon(R.drawable.waqt_notification)
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)  // Set the PendingIntent
            .setOngoing(true)
            .setSound(null) // Set the sound to null to suppress the notification sound

        notificationManager.notify(1, builder.build())
    }

    private fun scheduleNextAlarm(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Create an explicit intent for the AlarmReceiver
        val alarmIntent = Intent(context, AlarmReceiver::class.java)
        val requestCode = 0
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Calculate the time for the next alarm (you may customize this logic)
        val intervalMillis = 1000L
        val nextAlarmTime = System.currentTimeMillis() + intervalMillis

        // Check if the app has the required permission
        val hasPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.SCHEDULE_EXACT_ALARM
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true // No runtime permission needed for devices below Android 12
        }

        if (hasPermission) {
            // Set up the alarm to trigger periodically
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    nextAlarmTime,
                    pendingIntent
                )
            } else {
                // For devices below Android 6.0, use setExact
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    nextAlarmTime,
                    pendingIntent
                )
            }
        } else {
            // Handle the case where permission is denied
            // You may want to show a message to the user or disable certain functionality
            // Example: Toast.makeText(context, "Permission denied. Cannot set exact alarm.", Toast.LENGTH_SHORT).show()
        }
    }

}
