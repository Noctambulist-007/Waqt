package com.heapiphy101.waqt.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.heapiphy101.waqt.R
import com.heapiphy101.waqt.activities.AllDuasActivity

class AblutionFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_ablution, container, false)

        // Find the LinearLayout by its id
        val ablution01LinearLayout: LinearLayout = view.findViewById(R.id.ablution01)
        val ablution02LinearLayout: LinearLayout = view.findViewById(R.id.ablution02)

        // Set a click listener for the LinearLayout
        ablution01LinearLayout.setOnClickListener {
            // Start AllDuasActivity when the LinearLayout is clicked
            val intent = Intent(requireActivity(), AllDuasActivity::class.java)

            // Pass information to identify the target fragment
            intent.putExtra("targetFragment", "Ablution01Fragment")

            startActivity(intent)
            activity?.overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }

        // Set a click listener for the adhan02LinearLayout
        ablution02LinearLayout.setOnClickListener {
            // Start AllDuasActivity when the LinearLayout is clicked
            val intent = Intent(requireActivity(), AllDuasActivity::class.java)

            // Pass information to identify the target fragment
            intent.putExtra("targetFragment", "Ablution02_1Fragment")

            startActivity(intent)
            activity?.overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }


        return view
    }

}