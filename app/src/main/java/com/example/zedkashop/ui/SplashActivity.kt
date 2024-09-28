package com.example.zedkashop

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import pl.droidsonroids.gif.GifImageView

class SplashActivity : AppCompatActivity() {

    private val splashTimeOut: Long = 3000 // Duration of the splash screen in milliseconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val gifImageView: GifImageView = findViewById(R.id.gifImageView)
        gifImageView.setImageResource(R.drawable.z_logo) // Replace with your GIF resource

        Handler().postDelayed({

            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, splashTimeOut)

    }
}