package com.abel.mobilin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.abel.mobilin.Navbar.HomeFragment
import com.abel.mobilin.Navbar.SearchFragment
import com.abel.mobilin.Navbar.HistoryFragment
import com.abel.mobilin.Navbar.ProfileFragment
import com.abel.mobilin.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // View Binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        // Fragment default
        replaceFragment(HomeFragment())

        // Bottom Navigation
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navHome -> {
                    replaceFragment(HomeFragment())
                    true
                }
                R.id.navSearch -> {
                    replaceFragment(SearchFragment())
                    true
                }
                R.id.navHistory -> {
                    replaceFragment(HistoryFragment())
                    true
                }
                R.id.navProfile -> {
                    replaceFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, fragment)
            .commit()
    }
}