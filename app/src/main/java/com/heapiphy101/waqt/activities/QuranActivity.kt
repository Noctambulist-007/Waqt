package com.heapiphy101.waqt.activities

import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.animation.CycleInterpolator
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.viewpager.widget.ViewPager
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.heapiphy101.waqt.R
import com.heapiphy101.waqt.databinding.ActivityMainBinding
import com.heapiphy101.waqt.databinding.ActivityQuranBinding
import com.heapiphy101.waqt.helper.LocaleHelper
import com.heapiphy101.waqt.helper.TextPagerAdapter

class QuranActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQuranBinding
    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityQuranBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        window.navigationBarColor = ResourcesCompat.getColor(
            resources,
            R.color.card_background_black,
            theme
        )

        window.statusBarColor = ResourcesCompat.getColor(
            resources,
            R.color.card_background_black,
            theme
        )

        val shakeAnimator = ObjectAnimator.ofFloat(binding.ivQuran, "translationX", 0f, 10f, -10f, 10f, -10f, 5f, -5f, 0f)
        shakeAnimator.duration = 500 // Adjust the duration as needed
        shakeAnimator.interpolator = CycleInterpolator(1F) // Number of times to repeat the animation

        // Start the animation on click or wherever you want to trigger it
        binding.ivQuran.setOnClickListener {
            shakeAnimator.start()
        }

        val quranTextsArray = resources.getStringArray(R.array.quran_texts)

        // Convert the array to a list
        val textList = quranTextsArray.toList()

        val viewPagerDuaHadith: ViewPager = findViewById(R.id.viewPagerQuran)
        val adapterHadith = TextPagerAdapter(textList)
        viewPagerDuaHadith.adapter = adapterHadith

        val updateRunnable = object : Runnable {
            override fun run() {
                // Update ViewPager's current item randomly
                val randomPosition = (0 until textList.size).random()
                viewPagerDuaHadith.setCurrentItem(randomPosition, true)

                // Schedule the next update after 30 seconds
                handler.postDelayed(this, 10000)
            }
        }


        // Start the initial delay for the first update
        handler.postDelayed(updateRunnable, 10000)


        val savedLanguage = LocaleHelper.getSavedLanguage(this)

        // Apply the stored language if available
        if (savedLanguage.isNotEmpty()) {
            LocaleHelper.updateLocale(this, savedLanguage)
        }

        val backButtonQuran: ImageButton = findViewById(R.id.backButtonQuran)

        backButtonQuran.setOnClickListener {
            val intent = Intent(this@QuranActivity, MainActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.fade_in_reverse, R.anim.fade_out_reverse)
        }

        binding.quran78.setOnClickListener {
            startAllQuranActivity("Quran78Fragment")
        }

        binding.quran79.setOnClickListener {
            startAllQuranActivity("Quran79Fragment")
        }

        binding.quran80.setOnClickListener {
            startAllQuranActivity("Quran80Fragment")
        }

        binding.quran81.setOnClickListener {
            startAllQuranActivity("Quran81Fragment")
        }

        binding.quran82.setOnClickListener {
            startAllQuranActivity("Quran82Fragment")
        }

        binding.quran83.setOnClickListener {
            startAllQuranActivity("Quran83Fragment")
        }

        binding.quran84.setOnClickListener {
            startAllQuranActivity("Quran84Fragment")
        }

        binding.quran85.setOnClickListener {
            startAllQuranActivity("Quran85Fragment")
        }

        binding.quran86.setOnClickListener {
            startAllQuranActivity("Quran86Fragment")
        }

        binding.quran87.setOnClickListener {
            startAllQuranActivity("Quran87Fragment")
        }

        binding.quran88.setOnClickListener {
            startAllQuranActivity("Quran88Fragment")
        }

        binding.quran89.setOnClickListener {
            startAllQuranActivity("Quran89Fragment")
        }

        binding.quran90.setOnClickListener {
            startAllQuranActivity("Quran90Fragment")
        }

        binding.quran91.setOnClickListener {
            startAllQuranActivity("Quran91Fragment")
        }

        binding.quran92.setOnClickListener {
            startAllQuranActivity("Quran92Fragment")
        }

        binding.quran93.setOnClickListener {
            startAllQuranActivity("Quran93Fragment")
        }

        binding.quran94.setOnClickListener {
            startAllQuranActivity("Quran94Fragment")
        }

        binding.quran95.setOnClickListener {
            startAllQuranActivity("Quran95Fragment")
        }

        binding.quran96.setOnClickListener {
            startAllQuranActivity("Quran96Fragment")
        }

        binding.quran97.setOnClickListener {
            startAllQuranActivity("Quran97Fragment")
        }

        binding.quran98.setOnClickListener {
            startAllQuranActivity("Quran98Fragment")
        }

        binding.quran99.setOnClickListener {
            startAllQuranActivity("Quran99Fragment")
        }

        binding.quran100.setOnClickListener {
            startAllQuranActivity("Quran100Fragment")
        }

        binding.quran101.setOnClickListener {
            startAllQuranActivity("Quran101Fragment")
        }

        binding.quran102.setOnClickListener {
            startAllQuranActivity("Quran102Fragment")
        }

        binding.quran103.setOnClickListener {
            startAllQuranActivity("Quran103Fragment")
        }

        binding.quran104.setOnClickListener {
            startAllQuranActivity("Quran104Fragment")
        }

        binding.quran105.setOnClickListener {
            startAllQuranActivity("Quran105Fragment")
        }

        binding.quran106.setOnClickListener {
            startAllQuranActivity("Quran106Fragment")
        }

        binding.quran107.setOnClickListener {
            startAllQuranActivity("Quran107Fragment")
        }

        binding.quran108.setOnClickListener {
            startAllQuranActivity("Quran108Fragment")
        }

        binding.quran109.setOnClickListener {
            startAllQuranActivity("Quran109Fragment")
        }

        binding.quran110.setOnClickListener {
            startAllQuranActivity("Quran110Fragment")
        }

        binding.quran111.setOnClickListener {
            startAllQuranActivity("Quran111Fragment")
        }

        binding.quran112.setOnClickListener {
            startAllQuranActivity("Quran112Fragment")
        }

        binding.quran113.setOnClickListener {
            startAllQuranActivity("Quran113Fragment")
        }

        binding.quran114.setOnClickListener {
            startAllQuranActivity("Quran114Fragment")
        }

    }

    private fun startAllQuranActivity(targetFragment: String) {
        val intent = Intent(this, AllQuranActivity::class.java)
        intent.putExtra("targetFragment", targetFragment)

        startActivity(intent)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }


    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.fade_in_reverse, R.anim.fade_out_reverse)
    }
}