package com.abel.mobilin

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val CAMERA_PERMISSION_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()

        val username = findViewById<EditText>(R.id.etUsername)
        val email = findViewById<EditText>(R.id.etEmail)
        val password = findViewById<EditText>(R.id.etPassword)
        val confirmPassword = findViewById<EditText>(R.id.etConfirmPassword)
        val etNIK = findViewById<EditText>(R.id.etNIK)
        val btnUploadKTP = findViewById<Button>(R.id.btnUploadKTP)
        val imgKTP = findViewById<ImageView>(R.id.imgKTP)
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val tvLogin = findViewById<TextView>(R.id.tvLogin)

        // BUTTON SCAN KTP
        btnUploadKTP.setOnClickListener {
            if (isCameraPermissionGranted()) {
                cameraLauncher.launch(null)
            } else {
                requestCameraPermission()
            }
        }

        // BUTTON REGISTER
        btnRegister.setOnClickListener {

            val usernameText = username.text.toString()
            val emailText = email.text.toString().trim()
            val passText = password.text.toString()
            val confirmText = confirmPassword.text.toString()
            val nikText = etNIK.text.toString()

            if (usernameText.isEmpty()) {
                username.error = "Username tidak boleh kosong"
                return@setOnClickListener
            }

            if (emailText.isEmpty()) {
                email.error = "Email tidak boleh kosong"
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
                email.error = "Format email tidak valid"
                return@setOnClickListener
            }

            if (!isPasswordValid(passText)) {
                password.error =
                    "Password min 8 karakter, huruf besar, huruf kecil, dan angka"
                return@setOnClickListener
            }

            if (passText != confirmText) {
                confirmPassword.error = "Password tidak sama"
                return@setOnClickListener
            }

            if (nikText.length != 16) {
                etNIK.error = "NIK harus 16 digit"
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(emailText, passText)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {

                        val prefs = getSharedPreferences("user_pref", MODE_PRIVATE)
                        prefs.edit()
                            .putString("username", usernameText)
                            .putString("email", emailText)
                            .putString("nik", nikText)
                            .apply()

                        auth.currentUser?.sendEmailVerification()
                            ?.addOnSuccessListener {
                                Toast.makeText(
                                    this,
                                    "Registrasi berhasil. Cek email untuk verifikasi.",
                                    Toast.LENGTH_LONG
                                ).show()
                                auth.signOut()
                                finish()
                            }
                            ?.addOnFailureListener {
                                Toast.makeText(
                                    this,
                                    "Gagal mengirim email verifikasi",
                                    Toast.LENGTH_SHORT
                                ).show()
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

    // ================= CAMERA =================

    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            bitmap?.let {
                findViewById<ImageView>(R.id.imgKTP).apply {
                    setImageBitmap(it)
                    visibility = View.VISIBLE
                }
                scanNIKFromBitmap(it)
            }
        }

    private fun isCameraPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            CAMERA_PERMISSION_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            cameraLauncher.launch(null)
        } else {
            Toast.makeText(
                this,
                "Izin kamera diperlukan untuk scan KTP",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // ================= OCR =================

    private fun scanNIKFromBitmap(bitmap: Bitmap) {
        val image = InputImage.fromBitmap(bitmap, 0)
        val recognizer =
            TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        recognizer.process(image)
            .addOnSuccessListener { result ->
                val nik = Regex("\\b\\d{16}\\b").find(result.text)?.value
                if (nik != null) {
                    findViewById<EditText>(R.id.etNIK).setText(nik)
                    Toast.makeText(this, "NIK berhasil discan", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "NIK tidak terbaca", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal scan KTP", Toast.LENGTH_SHORT).show()
            }
    }
}

// ================= PASSWORD VALIDATION =================
private fun isPasswordValid(password: String): Boolean {
    val pattern = Regex("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).{8,}$")
    return pattern.matches(password)
}
