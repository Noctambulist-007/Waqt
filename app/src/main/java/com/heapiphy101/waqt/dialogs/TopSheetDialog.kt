package com.heapiphy101.waqt.dialogs

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatDialog
import com.heapiphy101.waqt.data.PrayerTimesData
import com.heapiphy101.waqt.R
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class TopSheetDialog(context: Context) : AppCompatDialog(context, R.style.TopSheetDialogTheme) {

    private var fajartime: TextView? = null
    private var sunrisetime: TextView? = null
    private var juhartime: TextView? = null
    private var asartime: TextView? = null
    private var magribtime: TextView? = null
    private var ishatime: TextView? = null

    private var fajartimeEnd: TextView? = null
    private var sunrisetimeEnd: TextView? = null
    private var juhartimeEnd: TextView? = null
    private var asartimeEnd: TextView? = null
    private var magribtimeEnd: TextView? = null
    private var ishatimeEnd: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window?.requestFeature(Window.FEATURE_NO_TITLE)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setContentView(R.layout.top_sheet_dialog)



        fajartime = findViewById(R.id.fajartime)
        sunrisetime = findViewById(R.id.sunrisetime)
        juhartime = findViewById(R.id.juhartime)
        asartime = findViewById(R.id.asartime)
        magribtime = findViewById(R.id.magribtime)
        ishatime = findViewById(R.id.ishatime)

        fajartimeEnd = findViewById(R.id.fajartimeEnd)
        sunrisetimeEnd = findViewById(R.id.sunrisetimeEnd)
        juhartimeEnd = findViewById(R.id.juhartimeEnd)
        asartimeEnd = findViewById(R.id.asartimeEnd)
        magribtimeEnd = findViewById(R.id.magribtimeEnd)
        ishatimeEnd = findViewById(R.id.ishatimeEnd)

        loadPrayerTimesFromSharedPreferences()

        val layoutParams = window?.attributes
        layoutParams?.gravity = Gravity.CENTER
        layoutParams?.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams?.height = WindowManager.LayoutParams.WRAP_CONTENT
        window?.attributes = layoutParams
    }

    private fun loadPrayerTimesFromSharedPreferences() {
        val sharedPreferences = context.getSharedPreferences("PrayerTimes", Context.MODE_PRIVATE)

        Handler(Looper.getMainLooper()).post {
            fajartime?.text = getPrayerTime(sharedPreferences, "fajr")
            sunrisetime?.text = getPrayerTime(sharedPreferences, "shurooq")
            juhartime?.text = getPrayerTime(sharedPreferences, "dhuhr")
            asartime?.text = getPrayerTime(sharedPreferences, "asr")
            magribtime?.text = getPrayerTime(sharedPreferences, "maghrib")
            ishatime?.text = getPrayerTime(sharedPreferences, "isha")

            fajartimeEnd?.text = subtractMinutes(sunrisetime?.text.toString(), 1)
            sunrisetimeEnd?.text = addMinutes(sunrisetime?.text.toString(), 15)
            juhartimeEnd?.text = subtractMinutes(asartime?.text.toString(), 1)
            asartimeEnd?.text = subtractMinutes(magribtime?.text.toString(), 15)
            magribtimeEnd?.text = subtractMinutes(ishatime?.text.toString(), 1)
            ishatimeEnd?.text = subtractMinutes(fajartime?.text.toString(), 1)
        }
    }



    private fun getPrayerTime(sharedPreferences: SharedPreferences, key: String): String {
        return sharedPreferences.getString(key, "") ?: ""
    }


    fun setPrayerTimes(prayerTimesData: PrayerTimesData) {
        fajartime?.text = prayerTimesData.fajr
        sunrisetime?.text = prayerTimesData.shurooq
        juhartime?.text = prayerTimesData.dhuhr
        asartime?.text = prayerTimesData.asr
        magribtime?.text = prayerTimesData.maghrib
        ishatime?.text = prayerTimesData.isha

        // Set end times
        fajartimeEnd?.text = subtractMinutes(prayerTimesData.shurooq, 1)
        sunrisetimeEnd?.text = addMinutes(prayerTimesData.shurooq, 15)
        juhartimeEnd?.text = subtractMinutes(prayerTimesData.asr, 1)
        asartimeEnd?.text = subtractMinutes(prayerTimesData.maghrib, 15)
        magribtimeEnd?.text = subtractMinutes(prayerTimesData.isha, 1)
        ishatimeEnd?.text = subtractMinutes(prayerTimesData.fajr, 1)

        savePrayerTimesToSharedPreferences(prayerTimesData)
    }


    private fun savePrayerTimesToSharedPreferences(prayerTimesData: PrayerTimesData) {
        val sharedPreferences = context.getSharedPreferences("PrayerTimes", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("fajr", prayerTimesData.fajr)
            putString("shurooq", prayerTimesData.shurooq)
            putString("dhuhr", prayerTimesData.dhuhr)
            putString("asr", prayerTimesData.asr)
            putString("maghrib", prayerTimesData.maghrib)
            putString("isha", prayerTimesData.isha)
            apply()
        }
    }

    private fun createLowerCaseAmPmDateFormat(): SimpleDateFormat {
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val symbols = DateFormatSymbols(Locale.getDefault())
        symbols.amPmStrings = arrayOf("am", "pm") // Use lowercase am/pm
        sdf.dateFormatSymbols = symbols
        return sdf
    }

    private fun subtractMinutes(time: String, minutes: Int): String {
        if (time.isEmpty()) {
            return ""
        }

        val sdf = createLowerCaseAmPmDateFormat()
        val date = sdf.parse(time)

        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(Calendar.MINUTE, -minutes)

        return sdf.format(calendar.time)
    }


    private fun addMinutes(time: String, minutes: Int): String {
        if (time.isEmpty()) {
            return ""
        }

        val sdf = createLowerCaseAmPmDateFormat()
        val date = sdf.parse(time)

        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(Calendar.MINUTE, minutes)

        return sdf.format(calendar.time)
    }

}
