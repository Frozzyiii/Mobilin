package com.abel.mobilin

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.abel.mobilin.databinding.ActivityBookingBinding
import com.bumptech.glide.Glide
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class BookingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBookingBinding

    private var hargaMobilPerHari: Int = 0
    private var tanggalAmbil: Calendar? = null
    private var tanggalKembali: Calendar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ambil data dari Intent
        val namaMobil = intent.getStringExtra("namaMobil") ?: "Mobil"
        hargaMobilPerHari = intent.getIntExtra("hargaMobil", 0)
        val fotoMobil = intent.getStringExtra("fotoMobil") ?: ""

        // Set data ke layout
        binding.tvNamaMobil.text = namaMobil

        val localeID = Locale("id", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeID)
        numberFormat.maximumFractionDigits = 0
        binding.tvHargaMobil.text = "${numberFormat.format(hargaMobilPerHari)}/Hari"

        Glide.with(this)
            .load(fotoMobil)
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_launcher_background)
            .centerCrop()
            .into(binding.imgMobil)

        // Tombol Tanggal Ambil
        binding.btnTanggalAmbil.setOnClickListener {
            showDatePicker { date, calendar ->
                tanggalAmbil = calendar
                binding.btnTanggalAmbil.text = date
                updateDurasiDanTotal()
            }
        }

        // Tombol Tanggal Kembali
        binding.btnTanggalKembali.setOnClickListener {
            showDatePicker { date, calendar ->
                tanggalKembali = calendar
                binding.btnTanggalKembali.text = date
                updateDurasiDanTotal()
            }
        }

        // Tombol Konfirmasi
        binding.btnKonfirmasi.setOnClickListener {
            if (tanggalAmbil == null || tanggalKembali == null) {
                binding.tvDurasi.text = "Pilih tanggal dulu!"
                return@setOnClickListener
            }

            binding.tvDurasi.text = "Booking berhasil!"
        }
    }

    // Fungsi DatePicker dengan minimal date hari ini
    private fun showDatePicker(onDateSelected: (String, Calendar) -> Unit) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePicker = DatePickerDialog(this, { _, y, m, d ->
            val selectedCal = Calendar.getInstance()
            selectedCal.set(y, m, d)
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale("id", "ID"))
            onDateSelected(sdf.format(selectedCal.time), selectedCal)
        }, year, month, day)

        // Tidak bisa pilih tanggal sebelum hari ini
        datePicker.datePicker.minDate = calendar.timeInMillis
        datePicker.show()
    }

    // Update durasi & total harga
    private fun updateDurasiDanTotal() {
        if (tanggalAmbil != null && tanggalKembali != null) {
            val diffMillis = tanggalKembali!!.timeInMillis - tanggalAmbil!!.timeInMillis
            val diffHari = (diffMillis / (1000 * 60 * 60 * 24)).toInt() + 1

            if (diffHari <= 0) {
                binding.tvDurasi.text = "Tanggal kembali harus setelah ambil"
                binding.tvTotalHarga.text = "Rp 0"
                return
            }

            binding.tvDurasi.text = "Durasi: $diffHari hari"
            val totalHarga = hargaMobilPerHari * diffHari
            val localeID = Locale("id", "ID")
            val numberFormat = NumberFormat.getCurrencyInstance(localeID)
            numberFormat.maximumFractionDigits = 0
            binding.tvTotalHarga.text = numberFormat.format(totalHarga)
        }
    }
}