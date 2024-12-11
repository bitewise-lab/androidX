package com.example.capstone.view.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.capstone.R
import com.example.capstone.databinding.ActivityOcrBinding
import com.example.capstone.helper.getImageUri
import com.example.capstone.view.viewmodel.OcrViewModel
import com.example.capstone.data.Result
import com.example.capstone.data.pref.UserPref
import com.example.capstone.view.viewmodel.ViewModelFactory
import com.example.capstone.view.viewmodel.dataStore
import com.google.android.material.snackbar.Snackbar
import com.yalantis.ucrop.UCrop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File

class OcrActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOcrBinding
    private lateinit var userPref: UserPref
    private val viewModel by viewModels<OcrViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private val requestPermissionLauncher =  registerForActivityResult(
        ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(this, R.string.permission_granted, Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_LONG).show()
        }
    }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            this,
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOcrBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        userPref = UserPref.getInstance(this.dataStore)

        if(!allPermissionsGranted()){
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }

        viewModel.currentImage?.let {
            showImage()
        }

        binding.btnCamera.setOnClickListener { startCamera() }
        binding.btnGallery.setOnClickListener { startGallery() }
        binding.btnAnalyze.setOnClickListener {
            viewModel.currentImage?.let {
                lifecycleScope.launch {
                    analyzeImage()
                }
            } ?: run {
                Toast.makeText(this, "Image cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private suspend fun analyzeImage() {
        val image = viewModel.currentImage
        if (image == null) {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
            return
        }

        val user = userPref.getSession().first()
        val email = user.email
        Log.d("Email", "analyzeImage: $email")

        viewModel.predictImage(email, image).observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.tvInfoScanning.visibility = View.VISIBLE
                }
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.tvInfoScanning.visibility = View.GONE
                    val jsonResult = result.data.healthRisk
                    val recomendations = result.data.recommendations
                    viewModel.setRecommendations(recomendations)
                    val intent = Intent(this, ResultActivity::class.java)
                    intent.putExtra(ResultActivity.EXTRA_RESULT, jsonResult.toString())
                    intent.putExtra(ResultActivity.EXTRA_IMAGE_URI, image.toString())
                    startActivity(intent)
                    Log.d("Result", "analyzeImage: $jsonResult")
                    Log.d("Recommendations", "analyzeImage: $recomendations")

                    viewModel.savePredictionResult(result.data)
                }
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.tvInfoScanning.visibility = View.GONE
                    if (result.error.contains("500")) {
                        Snackbar.make(binding.root, "Image is too close", Snackbar.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this, result.error, Toast.LENGTH_SHORT).show()
                    }
                    Log.d("Error", "analyzeImage: ${result.error}")
                }
                else -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, "An unexpected error happened", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun launchUCrop(sourceUri: Uri) {
        val destinationUri =
            Uri.fromFile(File(cacheDir, "cropped_${System.currentTimeMillis()}.jpg"))
        UCrop.of(sourceUri, destinationUri)
            .start(this)
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            launchUCrop(uri)
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private val launcherCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            viewModel.currentImage?.let { uri ->
                launchUCrop(uri)
            }
        } else {
            viewModel.currentImage = null
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun startCamera() {
        viewModel.currentImage = getImageUri(this)
        viewModel.currentImage?.let { launcherCamera.launch(it) }
    }

    private fun showImage() {
        viewModel.currentImage?.let {
            binding.previewImage.setImageURI(it)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            val resultUri = UCrop.getOutput(data!!)
            resultUri?.let {
                viewModel.currentImage = it
                showImage()
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            val cropError = UCrop.getError(data!!)
            Toast.makeText(this, "Crop error: ${cropError?.message}", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}