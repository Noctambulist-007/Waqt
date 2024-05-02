package com.heapiphy101.waqt.activities

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.heapiphy101.waqt.R
import com.heapiphy101.waqt.helper.LocaleHelper

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        supportActionBar?.hide()

        window.statusBarColor = getColor(R.color.black)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.black)

        val savedLanguage = LocaleHelper.getSavedLanguage(this)

        // Apply the stored language if available
        if (!savedLanguage.isNullOrEmpty()) {
            LocaleHelper.updateLocale(this, savedLanguage)
        }

        val tvWaqt = getString(R.string.waqt)
        findViewById<TextView>(R.id.titleTextView).text = tvWaqt

        val tvCopyRight = getString(R.string.from_nheapiphy)
        findViewById<TextView>(R.id.copyrightTextView).text = tvCopyRight

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 2000)
    }
}