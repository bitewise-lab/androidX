package com.example.capstone.view.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.capstone.databinding.ActivityWelcomeBinding
import com.example.capstone.view.fragments.RegisterFragment
import com.example.capstone.view.fragments.SignInFragment

class WelcomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.signInButton.setOnClickListener {
//            startActivity(Intent(this, SignInActivity::class.java))
            showSignInFragment()
        }

        binding.registerButton.setOnClickListener {
//            startActivity(Intent(this, RegisterActivity::class.java))
            showRegisterFragment()
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