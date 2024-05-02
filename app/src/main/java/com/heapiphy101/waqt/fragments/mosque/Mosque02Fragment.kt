package com.heapiphy101.waqt.fragments.mosque

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.viewpager.widget.ViewPager
import com.heapiphy101.waqt.R
import com.heapiphy101.waqt.activities.DuaActivity

class Mosque02Fragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_mosque02, container, false)

        val backButtonMosque02: ImageButton = view.findViewById(R.id.backButtonMosque02)

        val forwordButtonMosque02: ImageButton = view.findViewById(R.id.forwordButtonMosque02)

        forwordButtonMosque02.setOnClickListener {
            // Get the reference to the ViewPager in AllDuasActivity
            val viewPager: ViewPager? = activity?.findViewById(R.id.viewPagerAllDuas)

            // Check if viewPager is not null and navigate to the next page
            viewPager?.let {
                val nextPage = it.currentItem + 1
                it.currentItem = nextPage
            }

            // Apply fade-in and fade-out animations
            requireActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }

        backButtonMosque02.setOnClickListener {
            onBackPressed()
        }

        return view
    }

    fun onBackPressed() {
        requireActivity().onBackPressed()
        requireActivity().supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.fade_in_reverse, R.anim.fade_out_reverse)
            .commit()
    }

}