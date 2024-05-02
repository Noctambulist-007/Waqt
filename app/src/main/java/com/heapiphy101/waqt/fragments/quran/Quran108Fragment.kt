package com.heapiphy101.waqt.fragments.quran

import android.animation.ObjectAnimator
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.CycleInterpolator
import android.widget.ImageButton
import android.widget.ImageView
import androidx.viewpager.widget.ViewPager
import com.heapiphy101.waqt.R


class Quran108Fragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imageView = view.findViewById<ImageView>(R.id.ivQuran108)

        // Apply shake effect to the ImageView
        val shakeAnimatorQuran108 = ObjectAnimator.ofFloat(imageView, "translationX", 0f, 10f, -10f, 10f, -10f, 5f, -5f, 0f)
        shakeAnimatorQuran108.duration = 1000 // Adjust the duration as needed
        shakeAnimatorQuran108.interpolator = CycleInterpolator(1F) // Number of times to repeat the animation

        shakeAnimatorQuran108.start()

        val backButtonQuran108: ImageButton = view.findViewById(R.id.backButtonQuran108)
        val forwordButtonQuran108: ImageButton = view.findViewById(R.id.forwordButtonQuran108)

        forwordButtonQuran108.setOnClickListener {
            // Get the reference to the ViewPager in AllDuasActivity
            val viewPager: ViewPager? = activity?.findViewById(R.id.viewPagerAllQuran)

            // Check if viewPager is not null and navigate to the next page
            viewPager?.let {
                val nextPage = it.currentItem + 1
                it.currentItem = nextPage
            }

            // Apply fade-in and fade-out animations
            requireActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }

        backButtonQuran108.setOnClickListener {
            // Handle back button click
            requireActivity().onBackPressed()

            // Apply custom animations
            requireActivity().supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fade_in_reverse, R.anim.fade_out_reverse)
                .commit()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_quran108, container, false)
    }


}