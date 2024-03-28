package com.thejas.diamondgroup

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity

class SplashScreen : AppCompatActivity() {
    private val SPLASH_TIMEOUT = 1000L
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        sessionManager = SessionManager(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        Handler().postDelayed({
            if (sessionManager.isLoggedIn()) {
                // User is already logged in, redirect to MainActivity
                val intent = Intent(this@SplashScreen, MainActivity2::class.java)
                startActivity(intent)
            } else {
                // User is not logged in, show LoginScreen
                val intent = Intent(this@SplashScreen, LoginScreen::class.java)
                startActivity(intent)
            }
            finish()
        }, SPLASH_TIMEOUT)
    }
}
