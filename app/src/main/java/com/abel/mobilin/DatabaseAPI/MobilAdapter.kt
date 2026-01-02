package com.abel.mobilin.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.abel.mobilin.DatabaseAPI.Mobil
import com.abel.mobilin.R
import com.abel.mobilin.databinding.ItemMobilBinding
import com.bumptech.glide.Glide
import java.text.NumberFormat
import java.util.Locale

class MobilAdapter(private val listMobil: ArrayList<Mobil>) :
    RecyclerView.Adapter<MobilAdapter.MobilViewHolder>() {

    inner class MobilViewHolder(val binding: ItemMobilBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MobilViewHolder {
        val binding = ItemMobilBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MobilViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MobilViewHolder, position: Int) {
        // 1. Ambil satu data mobil dari list berdasarkan posisi
        val mobil = listMobil[position]

        with(holder.binding) {
            // 2. Masukkan data ke Text View (DINAMIS)
            tvNamaMobil.text = mobil.nama ?: "Nama Tidak Tersedia"
            tvTransmisi.text = mobil.transmisi ?: "-"

            // Cek apakah field seat di database cuma angka atau ada teksnya
            tvSeat.text = mobil.seat ?: "0 Seat"

            // 3. Format Harga (Int dari database diubah jadi format Rupiah)
            val localeID = Locale("id", "ID")
            val numberFormat = NumberFormat.getCurrencyInstance(localeID)
            numberFormat.maximumFractionDigits = 0
            val hargaRp = numberFormat.format(mobil.harga ?: 0)

            tvHargaSewa.text = "$hargaRp/Hari"

            // 4. Masukkan Gambar dari URL Database ke ImageView pakai Glide
            Glide.with(holder.itemView.context)
                .load(mobil.foto) // URL dari Firestore field "Foto"
                .placeholder(R.drawable.ic_launcher_background) // Gambar sementara pas loading
                .error(R.drawable.ic_launcher_background) // Gambar kalau link mati/error
                .centerCrop()
                .into(imgMobil) // ID ImageView di item_mobil.xml

            // Event Klik Tombol Pesan
            btnPesan.setOnClickListener {
                Toast.makeText(holder.itemView.context, "Kamu memilih ${mobil.nama}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount(): Int = listMobil.size
}