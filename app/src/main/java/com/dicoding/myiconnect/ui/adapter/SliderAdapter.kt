package com.dicoding.myiconnect.ui.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.dicoding.myiconnect.R

class SliderAdapter (var dataSlider : ArrayList<Int>, var context: Activity?) : PagerAdapter(){

    lateinit var layoutInflater: LayoutInflater

    override fun getCount(): Int {
        return dataSlider.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }



    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        super.destroyItem(container, position, `object`)

        container.removeView(`object` as View)
    }
}