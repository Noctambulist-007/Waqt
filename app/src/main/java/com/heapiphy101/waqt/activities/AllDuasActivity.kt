package com.heapiphy101.waqt.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.heapiphy101.waqt.R
import com.heapiphy101.waqt.fragments.ablution.Ablution01Fragment
import com.heapiphy101.waqt.fragments.ablution.Ablution02_1Fragment
import com.heapiphy101.waqt.fragments.ablution.Ablution02_2Fragment
import com.heapiphy101.waqt.fragments.ablution.Ablution02_3Fragment
import com.heapiphy101.waqt.fragments.adhan.Adhan01Fragment
import com.heapiphy101.waqt.fragments.adhan.Adhan02_1Fragment
import com.heapiphy101.waqt.fragments.adhan.Adhan02_2Fragment
import com.heapiphy101.waqt.fragments.adhan.Adhan03Fragment
import com.heapiphy101.waqt.fragments.mosque.Mosque01_1Fragment
import com.heapiphy101.waqt.fragments.mosque.Mosque01_2Fragment
import com.heapiphy101.waqt.fragments.mosque.Mosque02Fragment
import com.heapiphy101.waqt.fragments.mosque.Mosque03Fragment
import com.heapiphy101.waqt.fragments.salah.Salah01Fragment
import com.heapiphy101.waqt.fragments.salah.Salah02Fragment
import com.heapiphy101.waqt.fragments.salah.Salah03Fragment
import com.heapiphy101.waqt.fragments.salah.Salah04Fragment
import com.heapiphy101.waqt.fragments.salah.Salah05Fragment
import com.heapiphy101.waqt.fragments.salah.Salah06Fragment
import com.heapiphy101.waqt.fragments.salah.Salah07Fragment
import com.heapiphy101.waqt.fragments.salah.Salah08Fragment
import com.heapiphy101.waqt.fragments.salah.Salah09Fragment
import com.heapiphy101.waqt.fragments.salah.Salah10Fragment
import com.heapiphy101.waqt.fragments.salah.Salah11_1Fragment
import com.heapiphy101.waqt.fragments.salah.Salah11_2Fragment
import com.heapiphy101.waqt.fragments.salah.Salah11_3Fragment
import com.heapiphy101.waqt.fragments.salah.Salah11_4Fragment
import com.heapiphy101.waqt.fragments.salah.Salah11_5Fragment
import com.heapiphy101.waqt.fragments.salah.Salah12Fragment
import com.heapiphy101.waqt.helper.LocaleHelper

class AllDuasActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_duas)

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

        val savedLanguage = LocaleHelper.getSavedLanguage(this)

        // Apply the stored language if available
        if (!savedLanguage.isNullOrEmpty()) {
            LocaleHelper.updateLocale(this, savedLanguage)
        }

        val viewPager: ViewPager = findViewById(R.id.viewPagerAllDuas)
        val adapter = ViewPagerAdapter(supportFragmentManager, viewPager)
        viewPager.adapter = adapter

        val intent = intent
        val targetFragment = intent.getStringExtra("targetFragment")

        if (targetFragment != null) {
            when (targetFragment) {
                "Adhan01Fragment" -> {
                    // Navigate to Adhan01Fragment in the ViewPager
                    val adhan01Fragment = Adhan01Fragment()
                    adapter.addFragment(adhan01Fragment, "Adhan01Fragment")
                    adapter.notifyDataSetChanged()
                    viewPager.currentItem = adapter.count - (adapter.count)
                }
                "Adhan02_1Fragment" -> {
                    // Navigate to Adhan01Fragment in the ViewPager
                    val adhan02_1Fragment = Adhan02_1Fragment()
                    adapter.addFragment(adhan02_1Fragment, "Adhan02_1Fragment")
                    adapter.notifyDataSetChanged()
                    viewPager.currentItem = adapter.count - (adapter.count - 1)
                }
                "Adhan03Fragment" -> {
                    // Navigate to Adhan01Fragment in the ViewPager
                    val adhan03Fragment = Adhan03Fragment()
                    adapter.addFragment(adhan03Fragment, "Adhan03Fragment")
                    adapter.notifyDataSetChanged()
                    viewPager.currentItem = adapter.count - (adapter.count - 3)
                }
                "Ablution01Fragment" -> {
                    // Navigate to Adhan01Fragment in the ViewPager
                    val ablution01Fragment = Ablution01Fragment()
                    adapter.addFragment(ablution01Fragment, "Ablution01Fragment")
                    adapter.notifyDataSetChanged()
                    viewPager.currentItem = adapter.count - (adapter.count - 4)
                }
                "Ablution02_1Fragment" -> {
                    // Navigate to Adhan01Fragment in the ViewPager
                    val ablution02_1Fragment = Ablution02_1Fragment()
                    adapter.addFragment(ablution02_1Fragment, "Ablution02_1Fragment")
                    adapter.notifyDataSetChanged()
                    viewPager.currentItem = adapter.count - (adapter.count - 5)
                }
                "Mosque01_1Fragment" -> {
                    // Navigate to Adhan01Fragment in the ViewPager
                    val mosque01_1Fragment = Mosque01_1Fragment()
                    adapter.addFragment(mosque01_1Fragment, "Mosque01_1Fragment")
                    adapter.notifyDataSetChanged()
                    viewPager.currentItem = adapter.count - (adapter.count - 8)
                }
                "Mosque02Fragment" -> {
                    // Navigate to Adhan01Fragment in the ViewPager
                    val mosque02Fragment = Mosque02Fragment()
                    adapter.addFragment(mosque02Fragment, "Mosque02Fragment")
                    adapter.notifyDataSetChanged()
                    viewPager.currentItem = adapter.count - (adapter.count - 10)
                }
                "Mosque03Fragment" -> {
                    // Navigate to Adhan01Fragment in the ViewPager
                    val mosque03Fragment = Mosque03Fragment()
                    adapter.addFragment(mosque03Fragment, "Mosque03Fragment")
                    adapter.notifyDataSetChanged()
                    viewPager.currentItem = adapter.count - (adapter.count - 11)
                }
                "Salah01Fragment" -> {
                    // Navigate to Adhan01Fragment in the ViewPager
                    val salah01Fragment = Salah01Fragment()
                    adapter.addFragment(salah01Fragment, "Salah01Fragment")
                    adapter.notifyDataSetChanged()
                    viewPager.currentItem = adapter.count - (adapter.count - 12)
                }
                "Salah02Fragment" -> {
                    // Navigate to Adhan01Fragment in the ViewPager
                    val salah02Fragment = Salah02Fragment()
                    adapter.addFragment(salah02Fragment, "Salah02Fragment")
                    adapter.notifyDataSetChanged()
                    viewPager.currentItem = adapter.count - (adapter.count - 13)
                }
                "Salah03Fragment" -> {
                    // Navigate to Adhan01Fragment in the ViewPager
                    val salah03Fragment = Salah03Fragment()
                    adapter.addFragment(salah03Fragment, "Salah03Fragment")
                    adapter.notifyDataSetChanged()
                    viewPager.currentItem = adapter.count - (adapter.count - 14)
                }
                "Salah04Fragment" -> {
                    // Navigate to Adhan01Fragment in the ViewPager
                    val salah04Fragment = Salah04Fragment()
                    adapter.addFragment(salah04Fragment, "Salah04Fragment")
                    adapter.notifyDataSetChanged()
                    viewPager.currentItem = adapter.count - (adapter.count - 15)
                }
                "Salah05Fragment" -> {
                    // Navigate to Adhan01Fragment in the ViewPager
                    val salah05Fragment = Salah05Fragment()
                    adapter.addFragment(salah05Fragment, "Salah05Fragment")
                    adapter.notifyDataSetChanged()
                    viewPager.currentItem = adapter.count - (adapter.count - 16)
                }
                "Salah06Fragment" -> {
                    // Navigate to Adhan01Fragment in the ViewPager
                    val salah06Fragment = Salah06Fragment()
                    adapter.addFragment(salah06Fragment, "Salah06Fragment")
                    adapter.notifyDataSetChanged()
                    viewPager.currentItem = adapter.count - (adapter.count - 17)
                }
                "Salah07Fragment" -> {
                    // Navigate to Adhan01Fragment in the ViewPager
                    val salah07Fragment = Salah07Fragment()
                    adapter.addFragment(salah07Fragment, "Salah07Fragment")
                    adapter.notifyDataSetChanged()
                    viewPager.currentItem = adapter.count - (adapter.count - 18)
                }
                "Salah08Fragment" -> {
                    // Navigate to Adhan01Fragment in the ViewPager
                    val salah08Fragment = Salah08Fragment()
                    adapter.addFragment(salah08Fragment, "Salah08Fragment")
                    adapter.notifyDataSetChanged()
                    viewPager.currentItem = adapter.count - (adapter.count - 19)
                }
                "Salah09Fragment" -> {
                    // Navigate to Adhan01Fragment in the ViewPager
                    val salah09Fragment = Salah09Fragment()
                    adapter.addFragment(salah09Fragment, "Salah09Fragment")
                    adapter.notifyDataSetChanged()
                    viewPager.currentItem = adapter.count - (adapter.count - 20)
                }
                "Salah10Fragment" -> {
                    // Navigate to Adhan01Fragment in the ViewPager
                    val salah10Fragment = Salah10Fragment()
                    adapter.addFragment(salah10Fragment, "Salah10Fragment")
                    adapter.notifyDataSetChanged()
                    viewPager.currentItem = adapter.count - (adapter.count - 21)
                }
                "Salah11_1Fragment" -> {
                    // Navigate to Adhan01Fragment in the ViewPager
                    val salah11_1Fragment = Salah11_1Fragment()
                    adapter.addFragment(salah11_1Fragment, "Salah11_1Fragment")
                    adapter.notifyDataSetChanged()
                    viewPager.currentItem = adapter.count - (adapter.count - 22)
                }
                "Salah12Fragment" -> {
                    // Navigate to Adhan01Fragment in the ViewPager
                    val salah12Fragment = Salah12Fragment()
                    adapter.addFragment(salah12Fragment, "Salah12Fragment")
                    adapter.notifyDataSetChanged()
                    viewPager.currentItem = adapter.count - (adapter.count - 27)
                }
            }
        }
    }



    private inner class ViewPagerAdapter(fm: FragmentManager, private val viewPager: ViewPager) :
        FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        override fun getCount(): Int {
            return 28 // Number of fragments
        }

        private val fragmentList = mutableListOf<Fragment>()
        private val fragmentTitleList = mutableListOf<String>()

        fun addFragment(fragment: Fragment, title: String) {
            fragmentList.add(fragment)
            fragmentTitleList.add(title)
        }

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> Adhan01Fragment()
                1 -> Adhan02_1Fragment()
                2 -> Adhan02_2Fragment()
                3 -> Adhan03Fragment()
                4 -> Ablution01Fragment()
                5 -> Ablution02_1Fragment()
                6 -> Ablution02_2Fragment()
                7 -> Ablution02_3Fragment()
                8 -> Mosque01_1Fragment()
                9 -> Mosque01_2Fragment()
                10 -> Mosque02Fragment()
                11 -> Mosque03Fragment()
                12 -> Salah01Fragment()
                13 -> Salah02Fragment()
                14 -> Salah03Fragment()
                15 -> Salah04Fragment()
                16 -> Salah05Fragment()
                17 -> Salah06Fragment()
                18 -> Salah07Fragment()
                19 -> Salah08Fragment()
                20 -> Salah09Fragment()
                21 -> Salah10Fragment()
                22 -> Salah11_1Fragment()
                23 -> Salah11_2Fragment()
                24 -> Salah11_3Fragment()
                25 -> Salah11_4Fragment()
                26 -> Salah11_5Fragment()
                27 -> Salah12Fragment()

                else -> throw IllegalArgumentException("Invalid position")
            }
        }

/*        override fun getPageTitle(position: Int): CharSequence? {
            return when (position) {
                0 -> "Adhan"
                1 -> "Ablution"
                2 -> "Mosque"
                3 -> "Salah"
                else -> throw IllegalArgumentException("Invalid position")
            }
        }*/
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.fade_in_reverse, R.anim.fade_out_reverse)
    }
}