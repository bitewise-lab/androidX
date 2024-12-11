package com.example.capstone.view.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.capstone.R
import com.example.capstone.databinding.ActivityResultBinding
import com.example.capstone.view.viewmodel.OcrViewModel
import com.example.capstone.view.viewmodel.ViewModelFactory
import com.example.capstone.data.Result

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding

    private val viewModel by viewModels<OcrViewModel> {
        ViewModelFactory.getInstance(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val result = intent.getStringExtra(EXTRA_RESULT)
        val imageUri = intent.getStringExtra(EXTRA_IMAGE_URI)?.let { Uri.parse(it) }
        result?.let {
            showResult(it)
        } ?: run {
            observeViewModel()
        }

        imageUri?.let {
            binding.resultImage.setImageURI(it)
        }

        binding.backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun observeViewModel() {
        viewModel.predictions.observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.linearProgressBar.visibility = View.VISIBLE
                }
                is Result.Success -> {
                    binding.linearProgressBar.visibility = View.GONE
                    val predictResponse = result.data
                    predictResponse.healthRisk?.let { showResult(it) }
                }
                is Result.Error -> {
                    binding.linearProgressBar.visibility = View.GONE
                    result.error.let { binding.resultText.text = it }
                }

                else -> {
                    binding.linearProgressBar.visibility = View.GONE
                    binding.resultText.text = getString(R.string.error)
                }
            }
        }
    }

    private fun showResult(result: String) {
        binding.resultText.text = result
        binding.resultImage.setImageURI(viewModel.currentImage)
        when (result) {
            "Low" -> binding.resultDesc.text = getString(R.string.low)
            "Medium" -> binding.resultDesc.text = getString(R.string.medium)
            "High" -> binding.resultDesc.text = getString(R.string.high)
        }
    }

    companion object {
        const val EXTRA_RESULT = "extra_result"
        const val EXTRA_IMAGE_URI = "extra_image_uri"
    }
}