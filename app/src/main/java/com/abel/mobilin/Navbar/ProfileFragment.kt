package com.abel.mobilin.Navbar

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.abel.mobilin.R
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment : Fragment() {

    private lateinit var tvUsername: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvUserEmail: TextView
    private lateinit var btnLogout: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        tvUsername = view.findViewById(R.id.tvUsername)
        tvEmail = view.findViewById(R.id.tvEmail)
        tvUserEmail = view.findViewById(R.id.tvUserEmail)
        btnLogout = view.findViewById(R.id.btnLogout)

        loadUserProfile()

        btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val prefs = requireActivity().getSharedPreferences("user_pref", Context.MODE_PRIVATE)
            prefs.edit().clear().apply()
            Toast.makeText(requireContext(), "Berhasil keluar", Toast.LENGTH_SHORT).show()
            requireActivity().finish()
        }

        return view
    }

    private fun loadUserProfile() {
        val prefs = requireActivity().getSharedPreferences("user_pref", Context.MODE_PRIVATE)
        val username = prefs.getString("username", "Username") ?: "Username"
        val email = FirebaseAuth.getInstance().currentUser?.email ?: prefs.getString("email", "user@example.com")

        tvUsername.text = username
        tvEmail.text = email
        tvUserEmail.text = email
    }
}
