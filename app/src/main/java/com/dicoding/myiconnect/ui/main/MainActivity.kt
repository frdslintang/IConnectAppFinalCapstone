package com.dicoding.myiconnect.ui.home

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.dicoding.myiconnect.R
import com.dicoding.myiconnect.databinding.ActivityMainBinding
import com.dicoding.myiconnect.ui.articelfragment.ArticelFragment
import com.dicoding.myiconnect.ui.dictionaryfragment.DictionaryFragment
import com.dicoding.myiconnect.ui.homefragment.HomeFragment
import com.dicoding.myiconnect.ui.profilefragment.ProfileFragment
import com.dicoding.myiconnect.ui.translatefragment.TranslateFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var bottomNavigationView: BottomNavigationView

    private val fragmentHome: Fragment = HomeFragment()
    private val fragmentArticel: Fragment = ArticelFragment()
    private val fragmentDictionary: Fragment = DictionaryFragment()
    private val fragmentTranslate: Fragment = TranslateFragment()
    private val fragmentProfile: Fragment = ProfileFragment()

    private val fm: FragmentManager = supportFragmentManager
    private lateinit var active: Fragment

    private var activeFragmentId: Int = R.id.navigation_home // Default fragment ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        setUpBottomNav()

        // Set fragment default ke HomeFragment
        switchFragment(fragmentHome)
        bottomNavigationView.selectedItemId = R.id.navigation_home
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("lastActiveFragment", activeFragmentId)
    }

    override fun onResume() {
        super.onResume()
        checkUserLoggedOut()
    }

    private fun checkUserLoggedOut() {
        val userLoggedOut = intent.getBooleanExtra("userLoggedOut", false)
        if (userLoggedOut) {
            activeFragmentId = R.id.navigation_home // Set fragment default ke home setelah logout
            switchFragment(fragmentHome) // Tampilkan fragment home
            bottomNavigationView.selectedItemId = R.id.navigation_home // Atur item menu yang dipilih
            intent.removeExtra("userLoggedOut") // Hapus informasi userLoggedOut agar tidak ter-trigger di onResume selanjutnya
        }
    }

    private fun switchFragment(fragment: Fragment) {
        if (active != fragment) {
            fm.beginTransaction()
                .hide(active)
                .show(fragment)
                .commit()
            active = fragment
        }
    }

    private fun setUpBottomNav() {
        fm.beginTransaction().add(R.id.container, fragmentHome).commit()
        fm.beginTransaction().add(R.id.container, fragmentArticel).hide(fragmentArticel).commit()
        fm.beginTransaction().add(R.id.container, fragmentDictionary).hide(fragmentDictionary).commit()
        fm.beginTransaction().add(R.id.container, fragmentTranslate).hide(fragmentTranslate).commit()
        fm.beginTransaction().add(R.id.container, fragmentProfile).hide(fragmentProfile).commit()

        active = fragmentHome

        bottomNavigationView = binding.navView
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            activeFragmentId = item.itemId // Simpan ID fragment yang baru aktif
            when (item.itemId) {
                R.id.navigation_home -> {
                    switchFragment(fragmentHome)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.navigation_articel -> {
                    switchFragment(fragmentArticel)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.navigation_dictionary -> {
                    switchFragment(fragmentDictionary)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.navigation_translator -> {
                    switchFragment(fragmentTranslate)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.navigation_profile -> {
                    switchFragment(fragmentProfile)
                    return@setOnNavigationItemSelectedListener true
                }
            }
            false
        }
    }

    fun switchToDictionaryFragment() {
        switchFragment(fragmentDictionary)
        bottomNavigationView.selectedItemId = R.id.navigation_dictionary
    }

    fun switchToTranslateFragment() {
        switchFragment(fragmentTranslate)
        bottomNavigationView.selectedItemId = R.id.navigation_translator
    }

    override fun onPause() {
        super.onPause()
        val sharedPreferences: SharedPreferences = getSharedPreferences("FragmentPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("lastActiveFragment", activeFragmentId)
        editor.apply()
    }
}

