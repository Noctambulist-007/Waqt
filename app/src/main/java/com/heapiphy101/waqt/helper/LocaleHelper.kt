package com.heapiphy101.waqt.helper

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import java.util.Locale

object LocaleHelper {

    fun setLocale(context: Context, languageCode: String) {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("LanguagePrefs", Context.MODE_PRIVATE)

        val editor = sharedPreferences.edit()
        editor.putString("language", languageCode)
        editor.apply()

        updateLocale(context, languageCode)

        // Recreate the activity to apply changes immediately
        (context as? Activity)?.recreate()
    }

    fun updateLocale(context: Context, languageCode: String) {
        val resources: Resources = context.resources
        val config: Configuration = resources.configuration
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        config.locale = locale
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    fun getSavedLanguage(context: Context): String {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("LanguagePrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("language", "") ?: ""
    }
}

