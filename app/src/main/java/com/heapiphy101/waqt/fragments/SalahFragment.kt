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

class SalahFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_salah, container, false)

        val salah01LinearLayout: LinearLayout = view.findViewById(R.id.salah01)
        val salah02LinearLayout: LinearLayout = view.findViewById(R.id.salah02)
        val salah03LinearLayout: LinearLayout = view.findViewById(R.id.salah03)
        val salah04LinearLayout: LinearLayout = view.findViewById(R.id.salah04)
        val salah05LinearLayout: LinearLayout = view.findViewById(R.id.salah05)
        val salah06LinearLayout: LinearLayout = view.findViewById(R.id.salah06)
        val salah07LinearLayout: LinearLayout = view.findViewById(R.id.salah07)
        val salah08LinearLayout: LinearLayout = view.findViewById(R.id.salah08)
        val salah09LinearLayout: LinearLayout = view.findViewById(R.id.salah09)
        val salah10LinearLayout: LinearLayout = view.findViewById(R.id.salah10)
        val salah11LinearLayout: LinearLayout = view.findViewById(R.id.salah11)
        val salah12LinearLayout: LinearLayout = view.findViewById(R.id.salah12)


        // Set a click listener for the LinearLayout
        salah01LinearLayout.setOnClickListener {
            // Start AllDuasActivity when the LinearLayout is clicked
            val intent = Intent(requireActivity(), AllDuasActivity::class.java)

            // Pass information to identify the target fragment
            intent.putExtra("targetFragment", "Salah01Fragment")

            startActivity(intent)
            activity?.overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }


        salah02LinearLayout.setOnClickListener {
            // Start AllDuasActivity when the LinearLayout is clicked
            val intent = Intent(requireActivity(), AllDuasActivity::class.java)

            // Pass information to identify the target fragment
            intent.putExtra("targetFragment", "Salah02Fragment")

            startActivity(intent)
            activity?.overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }

        salah03LinearLayout.setOnClickListener {
            // Start AllDuasActivity when the LinearLayout is clicked
            val intent = Intent(requireActivity(), AllDuasActivity::class.java)

            // Pass information to identify the target fragment
            intent.putExtra("targetFragment", "Salah03Fragment")

            startActivity(intent)
            activity?.overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }

        salah04LinearLayout.setOnClickListener {
            // Start AllDuasActivity when the LinearLayout is clicked
            val intent = Intent(requireActivity(), AllDuasActivity::class.java)

            // Pass information to identify the target fragment
            intent.putExtra("targetFragment", "Salah04Fragment")

            startActivity(intent)
            activity?.overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }

        salah05LinearLayout.setOnClickListener {
            // Start AllDuasActivity when the LinearLayout is clicked
            val intent = Intent(requireActivity(), AllDuasActivity::class.java)

            // Pass information to identify the target fragment
            intent.putExtra("targetFragment", "Salah05Fragment")

            startActivity(intent)
            activity?.overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }

        salah06LinearLayout.setOnClickListener {
            // Start AllDuasActivity when the LinearLayout is clicked
            val intent = Intent(requireActivity(), AllDuasActivity::class.java)

            // Pass information to identify the target fragment
            intent.putExtra("targetFragment", "Salah06Fragment")

            startActivity(intent)
            activity?.overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }
        salah07LinearLayout.setOnClickListener {
            // Start AllDuasActivity when the LinearLayout is clicked
            val intent = Intent(requireActivity(), AllDuasActivity::class.java)

            // Pass information to identify the target fragment
            intent.putExtra("targetFragment", "Salah07Fragment")

            startActivity(intent)
            activity?.overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }

        salah08LinearLayout.setOnClickListener {
            // Start AllDuasActivity when the LinearLayout is clicked
            val intent = Intent(requireActivity(), AllDuasActivity::class.java)

            // Pass information to identify the target fragment
            intent.putExtra("targetFragment", "Salah08Fragment")

            startActivity(intent)
            activity?.overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }

        salah09LinearLayout.setOnClickListener {
            // Start AllDuasActivity when the LinearLayout is clicked
            val intent = Intent(requireActivity(), AllDuasActivity::class.java)

            // Pass information to identify the target fragment
            intent.putExtra("targetFragment", "Salah09Fragment")

            startActivity(intent)
            activity?.overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }

        salah10LinearLayout.setOnClickListener {
            // Start AllDuasActivity when the LinearLayout is clicked
            val intent = Intent(requireActivity(), AllDuasActivity::class.java)

            // Pass information to identify the target fragment
            intent.putExtra("targetFragment", "Salah10Fragment")

            startActivity(intent)
            activity?.overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }

        salah11LinearLayout.setOnClickListener {
            // Start AllDuasActivity when the LinearLayout is clicked
            val intent = Intent(requireActivity(), AllDuasActivity::class.java)

            // Pass information to identify the target fragment
            intent.putExtra("targetFragment", "Salah11_1Fragment")

            startActivity(intent)
            activity?.overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }

        salah12LinearLayout.setOnClickListener {
            // Start AllDuasActivity when the LinearLayout is clicked
            val intent = Intent(requireActivity(), AllDuasActivity::class.java)

            // Pass information to identify the target fragment
            intent.putExtra("targetFragment", "Salah12Fragment")

            startActivity(intent)
            activity?.overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }


        return view
    }

}