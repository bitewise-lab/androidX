package com.example.capstone.view.activities

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.capstone.databinding.ActivityRegisterBinding
import com.example.capstone.view.viewmodel.RegisterViewModel
import com.example.capstone.view.viewmodel.ViewModelFactory
import com.example.capstone.data.Result

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    private val viewModel by viewModels<RegisterViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupView()
        setupAction()
//        playAnimation()
    }

    private fun setupAction(){
        binding.btnRegister.setOnClickListener {
            val name = binding.editName.text.toString()
            val username = binding.editUsername.text.toString()
            val email = binding.editEmail.text.toString()
            val password = binding.editPassword.text.toString()
            val berat = binding.editBeratBadan.text.toString().toInt()
            val tinggi = binding.editTinggiBadan.text.toString().toInt()
            val gulaDarah = binding.editGulaDarah.text.toString().toFloat()
            val kolestrol = binding.editKolesterol.text.toString().toFloat()
            val tekanan = binding.editTekananDarah.text.toString().toFloat()

            viewModel.register(name, username, email, password, berat, tinggi, gulaDarah, kolestrol, tekanan).observe(this) { result ->
                when (result) {
                    is Result.Error -> {
                        binding.linearProgressBar.visibility = View.GONE
                        Toast.makeText(this, result.error, Toast.LENGTH_SHORT).show()
                    }
                    is Result.Loading -> {
                        binding.linearProgressBar.visibility = View.VISIBLE
                    }
                    is Result.Success -> {
                        binding.linearProgressBar.visibility = View.GONE
                        Log.d("RegisterActivity", "Success $name")
                        if (result.data.error == true) {
                            Toast.makeText(this, result.data.message, Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, result.data.message, Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    }
                }
            }
        }
    }

    private fun setupView(){
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

//    private fun playAnimation(){
//        val tvRegister = ObjectAnimator.ofFloat(binding.tvRegister, View.ALPHA, 1f).setDuration(200)
//        val detail = ObjectAnimator.ofFloat(binding.tvEnterDetails, View.ALPHA, 1f).setDuration(200)
//        val name = ObjectAnimator.ofFloat(binding.editName, View.ALPHA, 1f).setDuration(125)
//        val username = ObjectAnimator.ofFloat(binding.editUsername, View.ALPHA, 1f).setDuration(125)
//        val email = ObjectAnimator.ofFloat(binding.editEmail, View.ALPHA, 1f).setDuration(125)
//        val password = ObjectAnimator.ofFloat(binding.editPassword, View.ALPHA, 1f).setDuration(125)
//        val berat = ObjectAnimator.ofFloat(binding.editBeratBadan, View.ALPHA, 1f).setDuration(125)
//        val tinggi = ObjectAnimator.ofFloat(binding.editTinggiBadan, View.ALPHA, 1f).setDuration(125)
//        val gulaDarah = ObjectAnimator.ofFloat(binding.editGulaDarah, View.ALPHA, 1f).setDuration(125)
//        val kolestrol = ObjectAnimator.ofFloat(binding.editKolesterol, View.ALPHA, 1f).setDuration(125)
//        val tekanan = ObjectAnimator.ofFloat(binding.editTekananDarah, View.ALPHA, 1f).setDuration(125)
//        val register = ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 1f).setDuration(125)
//
//        AnimatorSet().apply {
//            playSequentially(
//                tvRegister,
//                detail,
//                name,
//                username,
//                email,
//                password,
//                berat,
//                tinggi,
//                gulaDarah,
//                kolestrol,
//                tekanan,
//                register
//            )
//            startDelay = 200
//        }.start()
//    }

}