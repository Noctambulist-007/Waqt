package com.heapiphy101.waqt.helper

import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.viewpager.widget.PagerAdapter
import com.heapiphy101.waqt.R

class TextPagerAdapter(private val textList: List<String>) : PagerAdapter() {

    override fun getCount(): Int {
        return textList.size
    }

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view == obj
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        // Create a new TextView programmatically
        val textView = TextView(container.context)

        // Set text from the textList
        textView.text = textList[position]

        // Set gravity to center
        textView.gravity = Gravity.CENTER

        // Set font family dynamically
        val fontResId = textView.context.resources.getIdentifier("prayer_text_font_family", "string", textView.context.packageName)
        val fontFamily = textView.context.resources.getString(fontResId)
        textView.typeface = ResourcesCompat.getFont(textView.context, R.font.kalpurush)

        // Add the TextView to the container
        container.addView(textView)

        // Return the created TextView
        return textView
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        container.removeView(obj as View)
    }
}
