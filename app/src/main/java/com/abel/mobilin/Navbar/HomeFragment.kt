package com.abel.mobilin.Navbar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.abel.mobilin.DatabaseAPI.Mobil
import com.abel.mobilin.DatabaseAPI.MobilAdapter
import com.abel.mobilin.databinding.FragmentHomeBinding
import com.google.firebase.database.*

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: MobilAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup RecyclerView (Praktikum 5)
        adapter = MobilAdapter(mutableListOf())
        binding.rvMobilHome.layoutManager = GridLayoutManager(context, 2)
        binding.rvMobilHome.adapter = adapter

        loadData()
    }

    private fun loadData() {
        // PENTING: Gunakan URL dari gambar Firebase Anda
        val dbUrl = "https://mobilin-8bd80-default-rtdb.asia-southeast1.firebasedatabase.app/"
        val dbRef = FirebaseDatabase.getInstance(dbUrl).getReference("Mobil")

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val listMobil = mutableListOf<Mobil>()
                for (data in snapshot.children) {
                    val m = data.getValue(Mobil::class.java)
                    m?.let { listMobil.add(it) }
                }
                adapter.updateData(listMobil)
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}