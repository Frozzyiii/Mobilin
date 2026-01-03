package com.abel.mobilin

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.abel.mobilin.databinding.ActivityBookingBinding
import com.bumptech.glide.Glide
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class BookingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBookingBinding

    private var hargaMobilPerHari = 0
    private var tanggalAmbil: Calendar? = null
    private var tanggalKembali: Calendar? = null
    private var fotoMobil = ""
    private var namaMobil = ""

    // Formatter Rupiah (GLOBAL)
    private val rupiahFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID")).apply {
        maximumFractionDigits = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ambilIntent()
        setDataMobil()
        setupTanggalPicker()
        setupKonfirmasi()
    }

    // ================== INIT ==================

    private fun ambilIntent() {
        namaMobil = intent.getStringExtra("namaMobil") ?: "Mobil"
        hargaMobilPerHari = intent.getIntExtra("hargaMobil", 0)
        fotoMobil = intent.getStringExtra("fotoMobil") ?: ""
    }

    private fun setDataMobil() {
        binding.tvNamaMobil.text = namaMobil
        binding.tvHargaMobil.text = "${rupiahFormat.format(hargaMobilPerHari)}/Hari"

        Glide.with(this)
            .load(fotoMobil)
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_launcher_background)
            .centerCrop()
            .into(binding.imgMobil)
    }

    // ================== DATE PICKER ==================

    private fun setupTanggalPicker() {
        binding.btnTanggalAmbil.setOnClickListener {
            showDatePicker {
                tanggalAmbil = it
                binding.btnTanggalAmbil.text = formatTanggal(it)
                updateDurasiDanTotal()
            }
        }

        binding.btnTanggalKembali.setOnClickListener {
            showDatePicker {
                tanggalKembali = it
                binding.btnTanggalKembali.text = formatTanggal(it)
                updateDurasiDanTotal()
            }
        }
    }

    private fun showDatePicker(onSelected: (Calendar) -> Unit) {
        val now = Calendar.getInstance()
        DatePickerDialog(
            this,
            { _, y, m, d ->
                val cal = Calendar.getInstance().apply { set(y, m, d) }
                onSelected(cal)
            },
            now.get(Calendar.YEAR),
            now.get(Calendar.MONTH),
            now.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.minDate = now.timeInMillis
            show()
        }
    }

    private fun formatTanggal(cal: Calendar): String {
        return SimpleDateFormat("dd/MM/yyyy", Locale("id", "ID")).format(cal.time)
    }

    // ================== HITUNG ==================

    private fun hitungDurasiHari(): Int {
        if (tanggalAmbil == null || tanggalKembali == null) return 0
        val diffMillis = tanggalKembali!!.timeInMillis - tanggalAmbil!!.timeInMillis
        return TimeUnit.MILLISECONDS.toDays(diffMillis).toInt() + 1 // inclusive
    }

    private fun updateDurasiDanTotal() {
        val durasi = hitungDurasiHari()

        if (durasi <= 0) {
            binding.tvDurasi.text = "Tanggal kembali harus setelah tanggal ambil"
            binding.tvTotalHarga.text = rupiahFormat.format(0)
            return
        }

        val totalHarga = hargaMobilPerHari * durasi
        binding.tvDurasi.text = "Durasi: $durasi hari"
        binding.tvTotalHarga.text = rupiahFormat.format(totalHarga)
    }

    // ================== KONFIRMASI ==================

    private fun setupKonfirmasi() {
        binding.btnKonfirmasi.setOnClickListener {
            val durasi = hitungDurasiHari()

            if (durasi <= 0) {
                binding.tvDurasi.text = "Pilih tanggal dengan benar!"
                return@setOnClickListener
            }

            val totalHarga = hargaMobilPerHari * durasi
            val rentalDate =
                "${formatTanggal(tanggalAmbil!!)} - ${formatTanggal(tanggalKembali!!)}"

            startActivity(
                Intent(this, PaymentDetailActivity::class.java).apply {
                    putExtra("CAR_NAME", namaMobil)
                    putExtra("CAR_IMAGE", fotoMobil)
                    putExtra("RENTAL_DATE", rentalDate)
                    putExtra("TOTAL_PRICE", rupiahFormat.format(totalHarga))
                    putExtra("PRICE_PER_DAY", hargaMobilPerHari)
                }
            )
        }
    }
}
