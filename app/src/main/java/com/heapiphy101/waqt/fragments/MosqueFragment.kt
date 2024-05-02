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

class MosqueFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_mosque, container, false)

        val mosque01LinearLayout: LinearLayout = view.findViewById(R.id.mosque01)
        val mosque02LinearLayout: LinearLayout = view.findViewById(R.id.mosque02)
        val mosque03LinearLayout: LinearLayout = view.findViewById(R.id.mosque03)

        // Set a click listener for the LinearLayout
        mosque01LinearLayout.setOnClickListener {
            // Start AllDuasActivity when the LinearLayout is clicked
            val intent = Intent(requireActivity(), AllDuasActivity::class.java)

            // Pass information to identify the target fragment
            intent.putExtra("targetFragment", "Mosque01_1Fragment")

            startActivity(intent)
            activity?.overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }

        // Set a click listener for the adhan02LinearLayout
        mosque02LinearLayout.setOnClickListener {
            // Start AllDuasActivity when the LinearLayout is clicked
            val intent = Intent(requireActivity(), AllDuasActivity::class.java)

            // Pass information to identify the target fragment
            intent.putExtra("targetFragment", "Mosque02Fragment")

            startActivity(intent)
            activity?.overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }

        // Set a click listener for the adhan03LinearLayout
        mosque03LinearLayout.setOnClickListener {
            // Start AllDuasActivity when the LinearLayout is clicked
            val intent = Intent(requireActivity(), AllDuasActivity::class.java)

            // Pass information to identify the target fragment
            intent.putExtra("targetFragment", "Mosque03Fragment")

            startActivity(intent)
            activity?.overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }


        return view
    }


}