package com.abel.mobilin.Navbar

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.abel.mobilin.adapter.HistoryAdapter
import com.abel.mobilin.databinding.FragmentHistoryBinding
import com.abel.mobilin.model.HistoryItem
import com.google.gson.Gson

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)

        loadHistory()

        return binding.root
    }

    private fun loadHistory() {
        val prefs = requireContext().getSharedPreferences("history_pref", Context.MODE_PRIVATE)
        val json = prefs.getString("history_list", "[]")

        // Ambil list dari JSON
        val historyList: List<HistoryItem> = Gson().fromJson(
            json,
            Array<HistoryItem>::class.java
        ).toList()

        if (historyList.isEmpty()) {
            binding.tvEmpty.visibility = View.VISIBLE
            binding.rvHistory.visibility = View.GONE
        } else {
            binding.tvEmpty.visibility = View.GONE
            binding.rvHistory.visibility = View.VISIBLE

            binding.rvHistory.layoutManager = LinearLayoutManager(requireContext())
            binding.rvHistory.adapter = HistoryAdapter(historyList)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
