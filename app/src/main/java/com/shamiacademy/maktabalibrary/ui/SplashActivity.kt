package com.shamiacademy.maktabalibrary.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Theme.MaktabaLibrary.Splash draws the Masjid-Nabawi-style logo
        // as the window background — no layout inflation needed, keeps
        // the transition to the logo instant with no white flash.
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 1400)
    }
}
