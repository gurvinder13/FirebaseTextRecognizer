package com.example.firebasetextrecognizer

import PageAdapter
import ZoomOutPageTransformer
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager


class MainActivity : AppCompatActivity() {
    private var viewPager: ViewPager? = null
    private var pageAdapter: PageAdapter? = null
    var tag_ResultLayout: String? = null
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val window = this@MainActivity.window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this@MainActivity, R.color.colorPrimary)
        setContentView(R.layout.activity_main)
        viewPager = findViewById(R.id.viewPager)
        pageAdapter = PageAdapter(supportFragmentManager, 2)
        viewPager?.setAdapter(pageAdapter)
        viewPager?.setPageTransformer(true, ZoomOutPageTransformer())
    }

    fun openMainLayout() {
        viewPager!!.setCurrentItem(0, true)
    }

    fun openResultLayout() {
        viewPager!!.setCurrentItem(1, true)
    }

    override fun onBackPressed() {
        if (viewPager!!.currentItem == 1) {
            openMainLayout()
        } else {
            super.onBackPressed()
        }
    }
}