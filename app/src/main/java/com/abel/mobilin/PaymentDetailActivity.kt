package com.abel.mobilin

import android.content.Intent
import android.os.Bundle
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.abel.mobilin.databinding.ActivityPaymentDetailBinding
import com.bumptech.glide.Glide

class PaymentDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPaymentDetailBinding

    // Data dari Booking
    private var carName = ""
    private var rentalDate = ""
    private var totalPrice = ""
    private var carImageUrl = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getIntentData()
        setupView()
        setupAction()
    }

    // ================== AMBIL DATA ==================
    private fun getIntentData() {
        carName = intent.getStringExtra("CAR_NAME") ?: "-"
        rentalDate = intent.getStringExtra("RENTAL_DATE") ?: "-"
        totalPrice = intent.getStringExtra("TOTAL_PRICE") ?: "Rp 0"
        carImageUrl = intent.getStringExtra("CAR_IMAGE") ?: ""
    }

    // ================== SET VIEW ==================
    private fun setupView() {
        binding.tvCarName.text = carName
        binding.tvCarType.text = "MPV • Manual"
        binding.tvRentalDuration.text = rentalDate
        binding.tvTotalPrice.text = totalPrice

        Glide.with(this)
            .load(carImageUrl)
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_launcher_background)
            .into(binding.ivCarImage)
    }

    // ================== ACTION ==================
    private fun setupAction() {

        // tombol back
        binding.ivBack.setOnClickListener {
            finish()
        }

        // tombol bayar
        binding.btnPayNow.setOnClickListener {

            val selectedId = binding.rgBankOptions.checkedRadioButtonId
            if (selectedId == -1) {
                Toast.makeText(
                    this,
                    "Silakan pilih metode pembayaran terlebih dahulu",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val selectedBank =
                findViewById<RadioButton>(selectedId).text.toString()

            // pindah ke success
            startActivity(
                Intent(this, PaymentSuccessActivity::class.java).apply {
                    putExtra("CAR_NAME", carName)
                    putExtra("CAR_TYPE", "MPV • Manual")
                    putExtra("RENTAL_DATE", rentalDate)
                    putExtra("TOTAL_AMOUNT", totalPrice)
                    putExtra("BANK_NAME", selectedBank)
                    putExtra("CAR_IMAGE", carImageUrl)
                }
            )

            finish()
        }
    }
}
