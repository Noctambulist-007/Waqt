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
import com.heapiphy101.waqt.fragments.quran.Quran100Fragment
import com.heapiphy101.waqt.fragments.quran.Quran101Fragment
import com.heapiphy101.waqt.fragments.quran.Quran102Fragment
import com.heapiphy101.waqt.fragments.quran.Quran103Fragment
import com.heapiphy101.waqt.fragments.quran.Quran104Fragment
import com.heapiphy101.waqt.fragments.quran.Quran105Fragment
import com.heapiphy101.waqt.fragments.quran.Quran106Fragment
import com.heapiphy101.waqt.fragments.quran.Quran107Fragment
import com.heapiphy101.waqt.fragments.quran.Quran108Fragment
import com.heapiphy101.waqt.fragments.quran.Quran109Fragment
import com.heapiphy101.waqt.fragments.quran.Quran110Fragment
import com.heapiphy101.waqt.fragments.quran.Quran111Fragment
import com.heapiphy101.waqt.fragments.quran.Quran112Fragment
import com.heapiphy101.waqt.fragments.quran.Quran113Fragment
import com.heapiphy101.waqt.fragments.quran.Quran114Fragment
import com.heapiphy101.waqt.fragments.quran.Quran78Fragment
import com.heapiphy101.waqt.fragments.quran.Quran79Fragment
import com.heapiphy101.waqt.fragments.quran.Quran80Fragment
import com.heapiphy101.waqt.fragments.quran.Quran81Fragment
import com.heapiphy101.waqt.fragments.quran.Quran82Fragment
import com.heapiphy101.waqt.fragments.quran.Quran83Fragment
import com.heapiphy101.waqt.fragments.quran.Quran84Fragment
import com.heapiphy101.waqt.fragments.quran.Quran85Fragment
import com.heapiphy101.waqt.fragments.quran.Quran86Fragment
import com.heapiphy101.waqt.fragments.quran.Quran87Fragment
import com.heapiphy101.waqt.fragments.quran.Quran88Fragment
import com.heapiphy101.waqt.fragments.quran.Quran89Fragment
import com.heapiphy101.waqt.fragments.quran.Quran90Fragment
import com.heapiphy101.waqt.fragments.quran.Quran91Fragment
import com.heapiphy101.waqt.fragments.quran.Quran92Fragment
import com.heapiphy101.waqt.fragments.quran.Quran93Fragment
import com.heapiphy101.waqt.fragments.quran.Quran94Fragment
import com.heapiphy101.waqt.fragments.quran.Quran95Fragment
import com.heapiphy101.waqt.fragments.quran.Quran96Fragment
import com.heapiphy101.waqt.fragments.quran.Quran97Fragment
import com.heapiphy101.waqt.fragments.quran.Quran98Fragment
import com.heapiphy101.waqt.fragments.quran.Quran99Fragment
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

class AllQuranActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_quran)

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

        val savedLanguage = LocaleHelper.getSavedLanguage(this)

        // Apply the stored language if available
        if (!savedLanguage.isNullOrEmpty()) {
            LocaleHelper.updateLocale(this, savedLanguage)
        }

        val viewPager: ViewPager = findViewById(R.id.viewPagerAllQuran)
        val adapter = ViewPagerAdapter(supportFragmentManager, viewPager)
        viewPager.adapter = adapter

        val intent = intent
        val targetFragment = intent.getStringExtra("targetFragment")

        if (targetFragment != null) {
            when (targetFragment) {
                "Quran78Fragment" -> {
                    val quran78Fragment = Quran78Fragment()
                    adapter.addFragment(quran78Fragment, "Quran78Fragment")
                    adapter.notifyDataSetChanged()
                    viewPager.currentItem = adapter.count - (adapter.count)
                }
                "Quran79Fragment" -> {
                    val quran79Fragment = Quran79Fragment()
                    adapter.addFragment(quran79Fragment, "Quran79Fragment")
                    adapter.notifyDataSetChanged()
                    viewPager.currentItem = adapter.count - (adapter.count - 1)
                }
                "Quran80Fragment" -> {
                    val quran80Fragment = Quran80Fragment()
                    adapter.addFragment(quran80Fragment, "Quran80Fragment")
                    adapter.notifyDataSetChanged()
                    viewPager.currentItem = adapter.count - (adapter.count - 2)
                }
                "Quran81Fragment" -> {
                    val quran81Fragment = Quran81Fragment()
                    adapter.addFragment(quran81Fragment, "Quran81Fragment")
                    adapter.notifyDataSetChanged()
                    viewPager.currentItem = adapter.count - (adapter.count - 3)
                }
                "Quran82Fragment" -> {
                    val quran82Fragment = Quran82Fragment()
                    adapter.addFragment(quran82Fragment, "Quran82Fragment")
                    adapter.notifyDataSetChanged()
                    viewPager.currentItem = adapter.count - (adapter.count - 4)
                }
                "Quran83Fragment" -> {
                    val quran83Fragment = Quran83Fragment()
                    adapter.addFragment(quran83Fragment, "Quran83Fragment")
                    adapter.notifyDataSetChanged()
                    viewPager.currentItem = adapter.count - (adapter.count - 5)
                }
                "Quran84Fragment" -> {
                    val quran84Fragment = Quran84Fragment()
                    adapter.addFragment(quran84Fragment, "Quran84Fragment")
                    adapter.notifyDataSetChanged()
                    viewPager.currentItem = adapter.count - (adapter.count - 6)
                }
                "Quran85Fragment" -> {
                    val quran85Fragment = Quran85Fragment()
                    adapter.addFragment(quran85Fragment, "Quran85Fragment")
                    adapter.notifyDataSetChanged()
                    viewPager.currentItem = adapter.count - (adapter.count - 7)
                }
                "Quran86Fragment" -> {
                    val quran86Fragment = Quran86Fragment()
                    adapter.addFragment(quran86Fragment, "Quran86Fragment")
                    adapter.notifyDataSetChanged()
                    viewPager.currentItem = adapter.count - (adapter.count - 8)
                }
                "Quran87Fragment" -> {
                    val quran87Fragment = Quran87Fragment()
                    adapter.addFragment(quran87Fragment, "Quran87Fragment")
                    adapter.notifyDataSetChanged()
                    viewPager.currentItem = adapter.count - (adapter.count - 9)
                }
                "Quran88Fragment" -> {
                    val quran88Fragment = Quran88Fragment()
                    adapter.addFragment(quran88Fragment, "Quran88Fragment")
                    adapter.notifyDataSetChanged()
                    viewPager.currentItem = adapter.count - (adapter.count - 10)
                }
                "Quran89Fragment" -> {
                    val quran89Fragment = Quran89Fragment()
                    adapter.addFragment(quran89Fragment, "Quran89Fragment")
                    adapter.notifyDataSetChanged()
                    viewPager.currentItem = adapter.count - (adapter.count - 11)
                }
                "Quran90Fragment" -> {
                    val quran90Fragment = Quran90Fragment()
                    adapter.addFragment(quran90Fragment, "Quran90Fragment")
                    adapter.notifyDataSetChanged()
                    viewPager.currentItem = adapter.count - (adapter.count - 12)
                }
                "Quran91Fragment" -> {
                    val quran91Fragment = Quran91Fragment()
                    adapter.addFragment(quran91Fragment, "Quran91Fragment")
                    adapter.notifyDataSetChanged()
                    viewPager.currentItem = adapter.count - (adapter.count - 13)
                }
                "Quran92Fragment" -> {
                    val quran92Fragment = Quran92Fragment()
                    adapter.addFragment(quran92Fragment, "Quran92Fragment")
                    adapter.notifyDataSetChanged()
                    viewPager.currentItem = adapter.count - (adapter.count - 14)
                }
                "Quran93Fragment" -> {
                    val quran93Fragment = Quran93Fragment()
                    adapter.addFragment(quran93Fragment, "Quran93Fragment")
                    adapter.notifyDataSetChanged()
                    viewPager.currentItem = adapter.count - (adapter.count - 15)
                }
                "Quran94Fragment" -> {
                    val quran94Fragment = Quran94Fragment()
                    adapter.addFragment(quran94Fragment, "Quran94Fragment")
                    adapter.notifyDataSetChanged()
                    viewPager.currentItem = adapter.count - (adapter.count - 16)
                }
                "Quran95Fragment" -> {
                    val quran95Fragment = Quran95Fragment()
                    adapter.addFragment(quran95Fragment, "Quran95Fragment")
                    adapter.notifyDataSetChanged()
                    viewPager.currentItem = adapter.count - (adapter.count - 17)
                }
                "Quran96Fragment" -> {
                    val quran96Fragment = Quran96Fragment()
                    adapter.addFragment(quran96Fragment, "Quran96Fragment")
                    adapter.notifyDataSetChanged()
                    viewPager.currentItem = adapter.count - (adapter.count - 18)
                }
                "Quran97Fragment" -> {
                    val quran97Fragment = Quran97Fragment()
                    adapter.addFragment(quran97Fragment, "Quran97Fragment")
                    adapter.notifyDataSetChanged()
                    viewPager.currentItem = adapter.count - (adapter.count - 19)
                }
                "Quran98Fragment" -> {
                    val quran98Fragment = Quran98Fragment()
                    adapter.addFragment(quran98Fragment, "Quran98Fragment")
                    adapter.notifyDataSetChanged()
                    viewPager.currentItem = adapter.count - (adapter.count - 20)
                }
                "Quran99Fragment" -> {
                    val quran99Fragment = Quran99Fragment()
                    adapter.addFragment(quran99Fragment, "Quran99Fragment")
                    adapter.notifyDataSetChanged()
                    viewPager.currentItem = adapter.count - (adapter.count - 21)
                }
                "Quran100Fragment" -> {
                    val quran100Fragment = Quran100Fragment()
                    adapter.addFragment(quran100Fragment, "Quran100Fragment")
                    adapter.notifyDataSetChanged()
                    viewPager.currentItem = adapter.count - (adapter.count - 22)
                }
                "Quran101Fragment" -> {
                    val quran101Fragment = Quran101Fragment()
                    adapter.addFragment(quran101Fragment, "Quran101Fragment")
                    adapter.notifyDataSetChanged()
                    viewPager.currentItem = adapter.count - (adapter.count - 23)
                }
                "Quran102Fragment" -> {
                    val quran102Fragment = Quran102Fragment()
                    adapter.addFragment(quran102Fragment, "Quran102Fragment")
                    adapter.notifyDataSetChanged()
                    viewPager.currentItem = adapter.count - (adapter.count - 24)
                }
                "Quran103Fragment" -> {
                    val quran103Fragment = Quran103Fragment()
                    adapter.addFragment(quran103Fragment, "Quran103Fragment")
                    adapter.notifyDataSetChanged()
                    viewPager.currentItem = adapter.count - (adapter.count - 25)
                }
                "Quran104Fragment" -> {
                    val quran104Fragment = Quran104Fragment()
                    adapter.addFragment(quran104Fragment, "Quran104Fragment")
                    adapter.notifyDataSetChanged()
                    viewPager.currentItem = adapter.count - (adapter.count - 26)
                }
                "Quran105Fragment" -> {
                    val quran105Fragment = Quran105Fragment()
                    adapter.addFragment(quran105Fragment, "Quran105Fragment")
                    adapter.notifyDataSetChanged()
                    viewPager.currentItem = adapter.count - (adapter.count - 27)
                }
                "Quran106Fragment" -> {
                    val quran106Fragment = Quran106Fragment()
                    adapter.addFragment(quran106Fragment, "Quran106Fragment")
                    adapter.notifyDataSetChanged()
                    viewPager.currentItem = adapter.count - (adapter.count - 28)
                }
                "Quran107Fragment" -> {
                    val quran107Fragment = Quran107Fragment()
                    adapter.addFragment(quran107Fragment, "Quran107Fragment")
                    adapter.notifyDataSetChanged()
                    viewPager.currentItem = adapter.count - (adapter.count - 29)
                }
                "Quran108Fragment" -> {
                    val quran108Fragment = Quran108Fragment()
                    adapter.addFragment(quran108Fragment, "Quran108Fragment")
                    adapter.notifyDataSetChanged()
                    viewPager.currentItem = adapter.count - (adapter.count - 30)
                }
                "Quran109Fragment" -> {
                    val quran109Fragment = Quran109Fragment()
                    adapter.addFragment(quran109Fragment, "Quran109Fragment")
                    adapter.notifyDataSetChanged()
                    viewPager.currentItem = adapter.count - (adapter.count - 31)
                }
                "Quran110Fragment" -> {
                    val quran110Fragment = Quran110Fragment()
                    adapter.addFragment(quran110Fragment, "Quran110Fragment")
                    adapter.notifyDataSetChanged()
                    viewPager.currentItem = adapter.count - (adapter.count - 32)
                }
                "Quran111Fragment" -> {
                    val quran111Fragment = Quran111Fragment()
                    adapter.addFragment(quran111Fragment, "Quran111Fragment")
                    adapter.notifyDataSetChanged()
                    viewPager.currentItem = adapter.count - (adapter.count - 33)
                }
                "Quran112Fragment" -> {
                    val quran112Fragment = Quran112Fragment()
                    adapter.addFragment(quran112Fragment, "Quran112Fragment")
                    adapter.notifyDataSetChanged()
                    viewPager.currentItem = adapter.count - (adapter.count - 34)
                }
                "Quran113Fragment" -> {
                    val quran113Fragment = Quran113Fragment()
                    adapter.addFragment(quran113Fragment, "Quran113Fragment")
                    adapter.notifyDataSetChanged()
                    viewPager.currentItem = adapter.count - (adapter.count - 35)
                }
                "Quran114Fragment" -> {
                    val quran114Fragment = Quran114Fragment()
                    adapter.addFragment(quran114Fragment, "Quran114Fragment")
                    adapter.notifyDataSetChanged()
                    viewPager.currentItem = adapter.count - (adapter.count - 36)
                }
            }
        }
    }



    private inner class ViewPagerAdapter(fm: FragmentManager, private val viewPager: ViewPager) :
        FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        override fun getCount(): Int {
            return 37 // Number of fragments
        }

        private val fragmentList = mutableListOf<Fragment>()
        private val fragmentTitleList = mutableListOf<String>()

        fun addFragment(fragment: Fragment, title: String) {
            fragmentList.add(fragment)
            fragmentTitleList.add(title)
        }

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> Quran78Fragment()
                1 -> Quran79Fragment()
                2 -> Quran80Fragment()
                3 -> Quran81Fragment()
                4 -> Quran82Fragment()
                5 -> Quran83Fragment()
                6 -> Quran84Fragment()
                7 -> Quran85Fragment()
                8 -> Quran86Fragment()
                9 -> Quran87Fragment()
                10 -> Quran88Fragment()
                11 -> Quran89Fragment()
                12 -> Quran90Fragment()
                13 -> Quran91Fragment()
                14 -> Quran92Fragment()
                15 -> Quran93Fragment()
                16 -> Quran94Fragment()
                17 -> Quran95Fragment()
                18 -> Quran96Fragment()
                19 -> Quran97Fragment()
                20 -> Quran98Fragment()
                21 -> Quran99Fragment()
                22 -> Quran100Fragment()
                23 -> Quran101Fragment()
                24 -> Quran102Fragment()
                25 -> Quran103Fragment()
                26 -> Quran104Fragment()
                27 -> Quran105Fragment()
                28 -> Quran106Fragment()
                29 -> Quran107Fragment()
                30 -> Quran108Fragment()
                31 -> Quran109Fragment()
                32 -> Quran110Fragment()
                33 -> Quran111Fragment()
                34 -> Quran112Fragment()
                35 -> Quran113Fragment()
                36 -> Quran114Fragment()

                else -> throw IllegalArgumentException("Invalid position")
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.fade_in_reverse, R.anim.fade_out_reverse)
    }
}