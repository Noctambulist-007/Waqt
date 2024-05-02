package com.heapiphy101.waqt.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.heapiphy101.waqt.R
import com.heapiphy101.waqt.activities.AllDuasActivity
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AdhanFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_adhan, container, false)


        val fragment_adhan: LinearLayout = view.findViewById(R.id.fragment_adhan)

        /*fragment_adhan.setBackgroundResource(R.drawable.gradient_background_isha)*/

        // Find the LinearLayout by its id
        val adhan01LinearLayout: LinearLayout = view.findViewById(R.id.adhan01)
        val adhan02LinearLayout: LinearLayout = view.findViewById(R.id.adhan02)
        val adhan03LinearLayout: LinearLayout = view.findViewById(R.id.adhan03)

        // Set a click listener for the LinearLayout
        adhan01LinearLayout.setOnClickListener {
            // Start AllDuasActivity when the LinearLayout is clicked
            val intent = Intent(requireActivity(), AllDuasActivity::class.java)

            // Pass information to identify the target fragment
            intent.putExtra("targetFragment", "Adhan01Fragment")

            startActivity(intent)
            activity?.overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }

        // Set a click listener for the adhan02LinearLayout
        adhan02LinearLayout.setOnClickListener {
            // Start AllDuasActivity when the LinearLayout is clicked
            val intent = Intent(requireActivity(), AllDuasActivity::class.java)

            // Pass information to identify the target fragment
            intent.putExtra("targetFragment", "Adhan02_1Fragment")

            startActivity(intent)
            activity?.overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }

          // Set a click listener for the adhan03LinearLayout
        adhan03LinearLayout.setOnClickListener {
            // Start AllDuasActivity when the LinearLayout is clicked
            val intent = Intent(requireActivity(), AllDuasActivity::class.java)

            // Pass information to identify the target fragment
            intent.putExtra("targetFragment", "Adhan03Fragment")

            startActivity(intent)
            activity?.overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }


        return view
    }


}
