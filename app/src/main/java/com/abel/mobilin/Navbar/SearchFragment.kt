package com.abel.mobilin.Navbar

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.abel.mobilin.DatabaseAPI.Mobil
import com.abel.mobilin.DatabaseAPI.MobilAdapter
import com.abel.mobilin.databinding.FragmentSearchBinding
import com.google.firebase.database.*

class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: MobilAdapter
    private val fullList = mutableListOf<Mobil>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)

        setupRecyclerView()
        loadAllData()

        // Logika Pencarian (Search Filter)
        binding.btnSearch.setOnClickListener {
            val query = binding.etSearch.text.toString().lowercase()
            val filtered = fullList.filter { it.merkMobil.lowercase().contains(query) || it.namaMobil.lowercase().contains(query) }
            adapter.updateData(filtered)
        }

        return binding.root
    }

    private fun setupRecyclerView() {
        adapter = MobilAdapter(mutableListOf())
        binding.rvSearch.layoutManager = LinearLayoutManager(context)
        binding.rvSearch.adapter = adapter
    }

    private fun loadAllData() {
        FirebaseDatabase.getInstance().getReference("Mobil")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    fullList.clear()
                    for (data in snapshot.children) {
                        data.getValue(Mobil::class.java)?.let { fullList.add(it) }
                    }
                    adapter.updateData(fullList)
                }
                override fun onCancelled(error: DatabaseError) {}
            })
    }
}