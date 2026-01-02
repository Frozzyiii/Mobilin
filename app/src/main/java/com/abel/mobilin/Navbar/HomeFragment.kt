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
    private lateinit var listMaster: ArrayList<Mobil>
    private lateinit var listDisplay: ArrayList<Mobil>

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    // State Filter
    private var currentBrandFilter = "Semua"
    private var currentSearchText = ""

    // Warna Resource
    private val colorActive = Color.parseColor("#0F4C81")
    private val colorInactive = Color.parseColor("#FFFFFF")
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

        // 3. PANGGIL FUNGSI LOAD DATA (User & Mobil)
        loadUserProfile()      // <-- INI YANG BIKIN HEADER DINAMIS
        getDataFromFirestore() // <-- INI UNTUK LIST MOBIL

        // 4. Setup Filter Buttons
        binding.btnSemua.setOnClickListener { updateFilter("Semua", binding.btnSemua) }
        binding.btnHonda.setOnClickListener { updateFilter("Honda", binding.btnHonda) }
        binding.btnBMW.setOnClickListener { updateFilter("BMW", binding.btnBMW) }

        // 5. Setup Search
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                currentSearchText = s.toString().lowercase()
                applyCombinedFilter()
            }
        })
    }

    // --- FUNGSI UNTUK HEADER DINAMIS ---
    private fun loadUserProfile() {
        val user = auth.currentUser

        if (user != null) {
            // Opsi A: Ambil dari Auth (Nama & Foto Google/Email) - Lebih Cepat
            val name = user.displayName ?: user.email
            val photoUrl = user.photoUrl

            binding.tvUsername.text = name

            // Load Foto dan buat jadi bulat pakai Glide
            Glide.with(this)
                .load(photoUrl)
                .placeholder(R.drawable.ic_launcher_background) // Gambar default saat loading
                .circleCrop() // <--- INI PENGGANTI CircleImageView
                .into(binding.imgProfile)

            // Opsi B: Jika kamu simpan data lengkap di Firestore (Collection "Users")
            // Gunakan ini jika Nama/Foto di Auth kosong atau custom
            val userId = user.uid
            db.collection("Users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val dbName = document.getString("nama") // Sesuaikan field di DB kamu
                        val dbFoto = document.getString("foto") // URL foto profil

                        if (!dbName.isNullOrEmpty()) {
                            binding.tvUsername.text = dbName
                        }

                        if (!dbFoto.isNullOrEmpty()) {
                            Glide.with(this)
                                .load(dbFoto)
                                .placeholder(R.drawable.ic_launcher_background)
                                .circleCrop() // Membulatkan gambar
                                .into(binding.imgProfile)
                        }
                    }
                }
        } else {
            binding.tvUsername.text = "Tamu"
        }
    }

    // --- FUNGSI AMBIL DATA MOBIL ---
    private fun getDataFromFirestore() {
        db.collection("Mobil")
            .get()
            .addOnSuccessListener { result ->
                listMaster.clear()
                for (document in result) {
                    val mobil = document.toObject(Mobil::class.java)
                    mobil.id = document.id
                    listMaster.add(mobil)
                }
                applyCombinedFilter()
            }
            .addOnFailureListener { e ->
                Log.e("HomeFragment", "Error load data", e)
            }
    }

    private fun updateFilter(brand: String, activeButton: Button) {
        currentBrandFilter = brand

        // Reset Visual
        val buttons = listOf(binding.btnSemua, binding.btnHonda, binding.btnBMW)
        for (btn in buttons) {
            btn.backgroundTintList = ColorStateList.valueOf(colorInactive)
            btn.setTextColor(textInactive)
        }

        // Set Active
        activeButton.backgroundTintList = ColorStateList.valueOf(colorActive)
        activeButton.setTextColor(textActive)

        applyCombinedFilter()
    }

    private fun applyCombinedFilter() {
        listDisplay.clear()

        for (item in listMaster) {
            val namaMobil = item.nama?.lowercase() ?: ""
            val merkMobil = item.merk?.lowercase() ?: namaMobil

            // Logic Filter
            val matchBrand = if (currentBrandFilter == "Semua") true
            else (merkMobil.contains(currentBrandFilter.lowercase()) || namaMobil.contains(currentBrandFilter.lowercase()))

            val matchSearch = if (currentSearchText.isEmpty()) true
            else namaMobil.contains(currentSearchText)

            if (matchBrand && matchSearch) {
                listDisplay.add(item)
            }
        }
        mobilAdapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}