package com.dicoding.myiconnect.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.myiconnect.R
import com.dicoding.myiconnect.ui.home.MainActivity

class SplashScreenActivity : AppCompatActivity() {

    private val SPLASH_TIME_OUT: Long = 1000
    private val MAX_RETRY: Int = 3
    private var retryCount: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        navigateToMainActivityDelayed()
        // Menyembunyikan action bar jika ada
        supportActionBar?.hide()

//        // Menyembunyikan status bar
//        hideStatusBar()
    }

    private fun hideStatusBar() {
        // Memeriksa versi Android
        if (Build.VERSION.SDK_INT in 16..29) {
            // Untuk Android versi 16 (Jelly Bean) hingga 29 (Q)
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
        } else if (Build.VERSION.SDK_INT >= 30) {
            // Untuk Android versi 30 (R) atau yang lebih baru
            window.setDecorFitsSystemWindows(false)
            window.insetsController?.let {
                it.hide(WindowInsets.Type.statusBars())
                it.systemBarsBehavior =
                    WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        }
    }

    private fun navigateToMainActivityDelayed() {
        Handler().postDelayed({
            startMainActivity()
        }, SPLASH_TIME_OUT)
    }

    private fun startMainActivity() {
        try {
            val homeIntent = Intent(this@SplashScreenActivity, MainActivity::class.java)
            startActivity(homeIntent)
            finish()
        } catch (e: Exception) {
            e.printStackTrace()
            handleNavigationError()
        }
    }

    private fun handleNavigationError() {
        if (retryCount < MAX_RETRY) {
            retryCount++
            startMainActivity()
        } else {
        }
    }
}
