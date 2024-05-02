package com.heapiphy101.waqt.activities

import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.CycleInterpolator
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.tabs.TabLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.heapiphy101.waqt.R
import com.heapiphy101.waqt.fragments.AblutionFragment
import com.heapiphy101.waqt.fragments.AdhanFragment
import com.heapiphy101.waqt.fragments.MosqueFragment
import com.heapiphy101.waqt.fragments.SalahFragment
import com.heapiphy101.waqt.helper.LocaleHelper
import com.heapiphy101.waqt.helper.TextPagerAdapter

class DuaActivity : AppCompatActivity() {

    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dua)

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

        val prayerTextsArray = resources.getStringArray(R.array.prayer_texts)

        // Convert the array to a list
        val textList = prayerTextsArray.toList()

        val viewPagerDuaHadith: ViewPager = findViewById(R.id.viewPagerDuaHadith)
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
        handler.postDelayed(updateRunnable, 10000) // 30 seconds

        val lottieBirds: LottieAnimationView = findViewById(R.id.lottieBirds)
        lottieBirds.speed = 0.5f
        lottieBirds.repeatCount = LottieDrawable.INFINITE
        lottieBirds.playAnimation()

        val ivDuaMosqueBg: ImageView = findViewById(R.id.ivDuaMosqueBg)

        // Shake effect animation
        val shakeAnimator = ObjectAnimator.ofFloat(ivDuaMosqueBg, "translationX", 0f, 10f, -10f, 10f, -10f, 5f, -5f, 0f)
        shakeAnimator.duration = 500 // Adjust the duration as needed
        shakeAnimator.interpolator = CycleInterpolator(1F) // Number of times to repeat the animation

        // Start the animation on click or wherever you want to trigger it
        ivDuaMosqueBg.setOnClickListener {
            shakeAnimator.start()
        }

        val ivDuaAdhan: ImageView = findViewById(R.id.ivDuaAdhan)
        val ivDuaAblution: ImageView = findViewById(R.id.ivDuaAblution)
        val ivDuaMosque: ImageView = findViewById(R.id.ivDuaMosque)
        val ivDuaSalah: ImageView = findViewById(R.id.ivDuaSalah)

        val shakeAnimatorAdhan = ObjectAnimator.ofFloat(ivDuaAdhan, "translationX", 0f, 10f, -10f, 10f, -10f, 5f, -5f, 0f)
        shakeAnimatorAdhan.duration = 500 // Adjust the duration as needed
        shakeAnimatorAdhan.interpolator = CycleInterpolator(1F) // Number of times to repeat the animation

        val shakeAnimatorAblution = ObjectAnimator.ofFloat(ivDuaAblution, "translationX", 0f, 10f, -10f, 10f, -10f, 5f, -5f, 0f)
        shakeAnimatorAblution.duration = 500 // Adjust the duration as needed
        shakeAnimatorAblution.interpolator = CycleInterpolator(1F) // Number of times to repeat the animation

        val shakeAnimatorMosque = ObjectAnimator.ofFloat(ivDuaMosque, "translationX", 0f, 10f, -10f, 10f, -10f, 5f, -5f, 0f)
        shakeAnimatorMosque.duration = 500 // Adjust the duration as needed
        shakeAnimatorMosque.interpolator = CycleInterpolator(1F) // Number of times to repeat the animation

        val shakeAnimatorSalah = ObjectAnimator.ofFloat(ivDuaSalah, "translationX", 0f, 10f, -10f, 10f, -10f, 5f, -5f, 0f)
        shakeAnimatorSalah.duration = 500 // Adjust the duration as needed
        shakeAnimatorSalah.interpolator = CycleInterpolator(1F) // Number of times to repeat the animation

        val savedLanguage = LocaleHelper.getSavedLanguage(this)

        // Apply the stored language if available
        if (!savedLanguage.isNullOrEmpty()) {
            LocaleHelper.updateLocale(this, savedLanguage)
        }

        val backButton: ImageButton = findViewById(R.id.backButton)

        backButton.setOnClickListener {
            onBackPressed()
        }

        val viewPager: ViewPager = findViewById(R.id.viewPager)
        val adapter = ViewPagerAdapter(supportFragmentManager, viewPager)
        viewPager.adapter = adapter

        val adhanCard: LinearLayout = findViewById(R.id.adhanLinerLayout)
        val ablutionCard: LinearLayout = findViewById(R.id.ablutionLinerLayout)
        val mosqueCard: LinearLayout = findViewById(R.id.mosqueLinerLayout)
        val salahCard: LinearLayout = findViewById(R.id.salahLinerLayout)

        adhanCard.setOnClickListener {
            selectCard(adhanCard)
            viewPager.currentItem = 0
            shakeAnimatorAdhan.start()
        }

        ablutionCard.setOnClickListener {
            selectCard(ablutionCard)
            viewPager.currentItem = 1
            shakeAnimatorAblution.start()
        }

        mosqueCard.setOnClickListener {
            selectCard(mosqueCard)
            viewPager.currentItem = 2
            shakeAnimatorMosque.start()
        }

        salahCard.setOnClickListener {
            selectCard(salahCard)
            viewPager.currentItem = 3
            shakeAnimatorSalah.start()
        }
    }



    private fun selectCard(linearLayout: LinearLayout) {
        // Reset background color of all LinearLayouts to the default color
        findViewById<LinearLayout>(R.id.adhanLinerLayout).setBackgroundColor(ContextCompat.getColor(this, R.color.dua_background))
        findViewById<LinearLayout>(R.id.ablutionLinerLayout).setBackgroundColor(ContextCompat.getColor(this, R.color.dua_background))
        findViewById<LinearLayout>(R.id.mosqueLinerLayout).setBackgroundColor(ContextCompat.getColor(this, R.color.dua_background))
        findViewById<LinearLayout>(R.id.salahLinerLayout).setBackgroundColor(ContextCompat.getColor(this, R.color.dua_background))

        // Set the background color of the selected LinearLayout
        linearLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.card_background_black))
    }


    private inner class ViewPagerAdapter(fm: FragmentManager, private val viewPager: ViewPager) :
        FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        override fun getCount(): Int {
            return 4 // Number of fragments
        }

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> AdhanFragment()
                1 -> AblutionFragment()
                2 -> MosqueFragment()
                3 -> SalahFragment()
                else -> throw IllegalArgumentException("Invalid position")
            }
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return when (position) {
                0 -> "Adhan"
                1 -> "Ablution"
                2 -> "Mosque"
                3 -> "Salah"
                else -> throw IllegalArgumentException("Invalid position")
            }
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.fade_in_reverse, R.anim.fade_out_reverse)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Remove callbacks to avoid memory leaks
        handler.removeCallbacksAndMessages(null)
    }


}
