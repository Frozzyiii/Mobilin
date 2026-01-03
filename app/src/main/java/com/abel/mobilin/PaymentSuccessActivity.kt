package com.abel.mobilin

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.abel.mobilin.databinding.ActivityPaymentSuccessBinding
import com.abel.mobilin.model.HistoryItem
import com.bumptech.glide.Glide
import com.google.gson.Gson
import java.util.UUID

class PaymentSuccessActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPaymentSuccessBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentSuccessBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1️⃣ Load data transaksi
        loadTransactionData()
        // 2️⃣ Setup tombol
        setupButtons()
    }

    private fun loadTransactionData() {

        val carName = intent.getStringExtra("CAR_NAME") ?: "-"
        val carType = intent.getStringExtra("CAR_TYPE") ?: "-"
        val rentalDate = intent.getStringExtra("RENTAL_DATE") ?: "-"
        val totalAmount = intent.getStringExtra("TOTAL_AMOUNT") ?: "Rp 0"
        val carImageUrl = intent.getStringExtra("CAR_IMAGE") ?: ""

        val transactionId = "TRX-${System.currentTimeMillis()}"
        val bookingCode = "BOOK-${UUID.randomUUID().toString().take(8).uppercase()}"

        // Set ke UI
        binding.tvCarName.text = carName
        binding.tvCarType.text = carType
        binding.tvRentalDate.text = rentalDate
        binding.tvTotalAmount.text = totalAmount
        binding.tvTransactionId.text = transactionId
        binding.tvBookingCode.text = bookingCode

        Glide.with(this)
            .load(carImageUrl)
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_launcher_background)
            .into(binding.ivCarImage)

        // 🔥 Simpan transaksi ke SharedPreferences
        saveToHistory(carName, carType, rentalDate, totalAmount, transactionId, bookingCode, carImageUrl)
    }

    private fun setupButtons() {

        binding.ivClose.setOnClickListener { navigateToHistory() }

        binding.btnCopyBooking.setOnClickListener {
            copyToClipboard(binding.tvBookingCode.text.toString(), "Kode Booking")
            Toast.makeText(this, "Kode booking berhasil disalin", Toast.LENGTH_SHORT).show()
        }

        binding.btnDownloadReceipt.setOnClickListener {
            Toast.makeText(this, "Bukti pembayaran berhasil diunduh", Toast.LENGTH_SHORT).show()
        }

        binding.btnBackToHome.setOnClickListener { navigateToHistory() }
    }

    private fun saveToHistory(
        carName: String,
        carType: String,
        rentalDate: String,
        totalAmount: String,
        transactionId: String,
        bookingCode: String,
        carImage: String
    ) {
        val prefs = getSharedPreferences("history_pref", Context.MODE_PRIVATE)
        val gson = Gson()

        val oldData = prefs.getString("history_list", "[]")
        val historyList = gson.fromJson(oldData, Array<HistoryItem>::class.java).toMutableList()

        // Tambahkan item baru
        historyList.add(HistoryItem(carName, carType, rentalDate, totalAmount, transactionId, bookingCode, carImage))

        // Simpan kembali
        prefs.edit().putString("history_list", gson.toJson(historyList)).apply()

        // 🔥 Log debug untuk cek apakah tersimpan
        android.util.Log.d(
            "HISTORY_DEBUG",
            "Saved transaction: $transactionId | $bookingCode | $totalAmount"
        )
    }

    private fun copyToClipboard(text: String, label: String) {
        val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
    }

    private fun navigateToHistory() {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("OPEN_HISTORY", true)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {
        navigateToHistory()
    }
}
