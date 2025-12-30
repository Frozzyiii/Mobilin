package com.abel.mobilin

import android.content.Intent
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val logo = findViewById<ImageView>(R.id.imgLogo)
        val animation = AnimationUtils.loadAnimation(this, R.anim.slide_fade)

        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                startActivity(Intent(this@SplashActivity, WelcomeActivity::class.java))
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                finishAfterTransition()
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })

        logo.startAnimation(animation)
    }
}