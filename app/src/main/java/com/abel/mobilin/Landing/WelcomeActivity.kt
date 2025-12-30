package com.abel.mobilin.Landing

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.abel.mobilin.Login.LoginActivity
import com.abel.mobilin.R
import com.abel.mobilin.Login.RegisterActivity

class WelcomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        findViewById<Button>(R.id.btnMasuk).setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
        findViewById<Button>(R.id.btnDaftar).setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}