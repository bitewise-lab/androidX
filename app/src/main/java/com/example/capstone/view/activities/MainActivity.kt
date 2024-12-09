package com.example.capstone.view.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.capstone.R
import com.example.capstone.data.pref.UserPref
import com.example.capstone.databinding.ActivityMainBinding
import com.example.capstone.view.fragments.HomeFragment
import com.example.capstone.view.viewmodel.dataStore
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

val Context.datastore by preferencesDataStore(name = "user_preferences")
class MainActivity : AppCompatActivity() {
//    private val Context.datastore by preferencesDataStore(name = "user_preferences")
    private lateinit var binding: ActivityMainBinding
    private lateinit var userPref: UserPref

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userPref = UserPref.getInstance(applicationContext.datastore)
        checkLoginStatus()

        val navView: BottomNavigationView = binding.bottomNavigationView
        navView.background = null
        navView.menu.getItem(2).isEnabled = false

        val navHost = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        val navController = navHost.navController
        navView.setupWithNavController(navController)

        binding.fab.setOnClickListener {
            val intent = Intent(this, OcrActivity::class.java)
            startActivity(intent)
        }
    }

//    private fun checkLoginStatus() {
//        userPref = UserPref.getInstance(dataStore)
//        lifecycleScope.launch {
//            userPref.getToken().collect { token ->
//                if (!token.isNullOrEmpty()) {
//                    val navHostFragment =
//                        supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
//                    val navController = navHostFragment.navController
//
//                    navController.navigate(R.id.homeFragment)
//                }
//            }
//        }
//    }

    private fun checkLoginStatus() {
        lifecycleScope.launch {
            userPref.getToken().collect { token ->
                if (!token.isNullOrEmpty()) {
                    val navHostFragment =
                        supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
                    val navController = navHostFragment.navController
                    navController.navigate(R.id.homeFragment)
                }
            }
        }
    }

}