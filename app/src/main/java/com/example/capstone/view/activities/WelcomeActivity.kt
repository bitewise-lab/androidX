package com.example.capstone.view.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import com.example.capstone.data.pref.UserPref
import com.example.capstone.databinding.ActivityWelcomeBinding
import com.example.capstone.view.fragments.RegisterFragment
import com.example.capstone.view.fragments.SignInFragment
import kotlinx.coroutines.launch

class WelcomeActivity : AppCompatActivity() {
    private val Context.dataStore by preferencesDataStore(name = "user_preferences")
    private lateinit var binding: ActivityWelcomeBinding
    private lateinit var userPref: UserPref

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        val sharedPreferences = getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)
        val isDarkMode = sharedPreferences.getBoolean("is_dark_mode", false)
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        checkLoginStatus()

        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.signInButton.setOnClickListener {
            showSignInFragment()
        }

        binding.registerButton.setOnClickListener {
            showRegisterFragment()
        }
    }

    private fun checkLoginStatus() {
        userPref = UserPref.getInstance(dataStore)
        lifecycleScope.launch {
            userPref.getToken().collect { token ->
                if (!token.isNullOrEmpty()) {
                    val intent = Intent(this@WelcomeActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }
    }

    private fun showRegisterFragment() {
        val registerFragment = RegisterFragment()
        registerFragment.show(supportFragmentManager, registerFragment.tag)
    }

    private fun showSignInFragment() {
        val signInFragment = SignInFragment()
        signInFragment.show(supportFragmentManager, signInFragment.tag)
    }
}