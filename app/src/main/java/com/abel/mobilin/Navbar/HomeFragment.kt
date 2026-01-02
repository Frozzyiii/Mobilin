package com.abel.mobilin.ui

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.abel.mobilin.DatabaseAPI.Mobil
import com.abel.mobilin.R
import com.abel.mobilin.adapter.MobilAdapter
import com.abel.mobilin.databinding.FragmentHomeBinding
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var mobilAdapter: MobilAdapter
    private lateinit var listMaster: ArrayList<Mobil> // Menyimpan semua data dari DB
    private lateinit var listDisplay: ArrayList<Mobil> // Menyimpan data yang ditampilkan (hasil filter)

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    // State Filter
    private var currentBrandFilter = "Semua"
    private var currentSearchText = ""

    // Warna Resource
    private val colorActive = Color.parseColor("#0F4C81") // Biru Gelap
    private val colorInactive = Color.parseColor("#FFFFFF") // Putih
    private val textActive = Color.WHITE
    private val textInactive = Color.BLACK

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Init Firebase
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // 2. Setup RecyclerView
        binding.rvMobilHome.layoutManager = LinearLayoutManager(context)
        listMaster = arrayListOf()
        listDisplay = arrayListOf()
        mobilAdapter = MobilAdapter(listDisplay)
        binding.rvMobilHome.adapter = mobilAdapter

        // 3. Load Data
        loadUserProfile()
        getDataFromFirestore()

        // 4. Setup Filter Buttons (Menggunakan Helper Function)
        setupBrandButtons()

        // 5. Setup Search Listener
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                currentSearchText = s.toString().lowercase()
                applyCombinedFilter()
            }
        })
    }

    private fun setupBrandButtons() {
        // Map tombol ke nama Brand
        val buttonMap = mapOf(
            binding.btnSemua to "Semua",
            binding.btnHonda to "Honda",
            binding.btnBMW to "BMW",
            binding.btnMercedes to "Mercedes",
            binding.btnMazda to "Mazda",
            binding.btnSuzuki to "Suzuki",
            binding.btnMitsubishi to "Mitsubishi",
            binding.btnToyota to "Toyota"
        )

        for ((button, brandName) in buttonMap) {
            button.setOnClickListener {
                updateFilterUI(brandName, button, buttonMap.keys.toList())
            }
        }
    }

    private fun updateFilterUI(selectedBrand: String, activeButton: Button, allButtons: List<Button>) {
        currentBrandFilter = selectedBrand

        // Reset semua tombol ke style inactive (Putih)
        for (btn in allButtons) {
            btn.backgroundTintList = ColorStateList.valueOf(colorInactive)
            btn.setTextColor(textInactive)
        }

        // Set tombol yang diklik ke style active (Biru)
        activeButton.backgroundTintList = ColorStateList.valueOf(colorActive)
        activeButton.setTextColor(textActive)

        applyCombinedFilter()
    }

    private fun getDataFromFirestore() {
        db.collection("Mobil")
            .get()
            .addOnSuccessListener { result ->
                listMaster.clear()
                for (document in result) {
                    // Convert document ke object Mobil
                    val mobil = document.toObject(Mobil::class.java)
                    mobil.id = document.id // simpan ID dokumen jika butuh
                    listMaster.add(mobil)
                }
                applyCombinedFilter() // Tampilkan data awal
            }
            .addOnFailureListener { e ->
                Log.e("HomeFragment", "Error getting documents", e)
            }
    }

    private fun applyCombinedFilter() {
        listDisplay.clear()

        for (item in listMaster) {
            val namaMobil = item.nama?.lowercase() ?: ""

            // Logic Filter Brand:
            // Karena di Firestore kamu "Nama": "Honda Civic", kita cek apakah string nama mengandung merk
            val matchBrand = if (currentBrandFilter == "Semua") {
                true
            } else {
                namaMobil.contains(currentBrandFilter.lowercase())
            }

            // Logic Search:
            val matchSearch = if (currentSearchText.isEmpty()) {
                true
            } else {
                namaMobil.contains(currentSearchText)
            }

            // Jika lolos kedua filter, tambahkan ke list tampilan
            if (matchBrand && matchSearch) {
                listDisplay.add(item)
            }
        }
        mobilAdapter.notifyDataSetChanged()
    }

    private fun loadUserProfile() {
        val user = auth.currentUser
        if (user != null) {
            // Priority 1: Display Name dari Auth
            binding.tvUsername.text = user.displayName ?: user.email

            // Load Foto
            Glide.with(this)
                .load(user.photoUrl)
                .placeholder(R.drawable.ic_launcher_background)
                .into(binding.imgProfile)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}