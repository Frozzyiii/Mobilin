package com.abel.mobilin.DatabaseAPI

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.abel.mobilin.databinding.ItemMobilBinding
import com.bumptech.glide.Glide

class MobilAdapter(private var list: List<Mobil>) : RecyclerView.Adapter<MobilAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemMobilBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMobilBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        with(holder.binding) {
            tvNamaMobil.text = "${item.merkMobil} ${item.namaMobil}"
            tvHargaSewa.text = "Rp ${item.hargaSewa}/hari"

            // Memuat gambar dari Cloudinary (Praktikum 9)
            Glide.with(root.context)
                .load(item.gambarUrl)
                .centerInside()
                .into(imgMobil)
        }
    }

    override fun getItemCount(): Int = list.size

    fun updateData(newList: List<Mobil>) {
        this.list = newList
        notifyDataSetChanged() // Praktikum 10 (Update UI)
    }
}