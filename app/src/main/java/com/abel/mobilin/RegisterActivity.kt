package com.abel.mobilin

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()

        val username = findViewById<EditText>(R.id.etUsername)
        val email = findViewById<EditText>(R.id.etEmail)
        val password = findViewById<EditText>(R.id.etPassword)
        val confirmPassword = findViewById<EditText>(R.id.etConfirmPassword)
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val tvLogin = findViewById<TextView>(R.id.tvLogin)

        btnRegister.setOnClickListener {

            val usernameText = username.text.toString()
            val emailText = email.text.toString().trim()
            val passText = password.text.toString()
            val confirmText = confirmPassword.text.toString()

            // VALIDASI USERNAME
            if (usernameText.isEmpty()) {
                username.error = "Username tidak boleh kosong"
                return@setOnClickListener
            }

            // VALIDASI EMAIL
            if (emailText.isEmpty()) {
                email.error = "Email tidak boleh kosong"
                return@setOnClickListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
                email.error = "Format email tidak valid"
                return@setOnClickListener
            }

            // VALIDASI PASSWORD
            if (!isPasswordValid(passText)) {
                password.error =
                    "Password min 8 karakter, huruf besar, huruf kecil, dan angka"
                return@setOnClickListener
            }

            // VALIDASI KONFIRMASI PASSWORD
            if (passText != confirmText) {
                confirmPassword.error = "Password tidak sama"
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(emailText, passText)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Simpan username ke SharedPreferences
                        val prefs = getSharedPreferences("user_pref", MODE_PRIVATE)
                        prefs.edit()
                            .putString("username", usernameText)
                            .putString("email", emailText) // optional, bisa pakai FirebaseAuth juga
                            .apply()


                        // KIRIM EMAIL VERIFIKASI
                        auth.currentUser?.sendEmailVerification()
                            ?.addOnCompleteListener { verifyTask ->
                                if (verifyTask.isSuccessful) {
                                    Toast.makeText(
                                        this,
                                        "Registrasi berhasil. Silakan cek email untuk verifikasi.",
                                        Toast.LENGTH_LONG
                                    ).show()

                                    auth.signOut()
                                    finish()
                                } else {
                                    Toast.makeText(
                                        this,
                                        "Gagal mengirim email verifikasi",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }

                    } else {
                        Toast.makeText(
                            this,
                            task.exception?.localizedMessage ?: "Registrasi gagal",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
        tvLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}

private fun isPasswordValid(password: String): Boolean {
    val passwordPattern =
        Regex("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).{8,}$")
    return passwordPattern.matches(password)
}