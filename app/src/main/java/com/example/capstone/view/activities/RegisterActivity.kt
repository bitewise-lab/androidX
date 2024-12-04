package com.example.capstone.view.activities

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.capstone.R
import com.example.capstone.databinding.ActivityRegisterBinding
import com.example.capstone.view.viewmodel.RegisterViewModel
import com.example.capstone.view.viewmodel.ViewModelFactory
import com.example.capstone.data.Result
import com.yalantis.ucrop.UCrop
import java.io.File

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    private val viewModel by viewModels<RegisterViewModel> {
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

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(!allPermissionsGranted()){
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }

        viewModel.currentImage?.let {
            showImage()
        }

        binding.imageItemWidget.setOnClickListener {
            startGallery()
        }

        setupView()
        setupAction()
//        playAnimation()
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

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun showImage() {
        viewModel.currentImage?.let {
            binding.imageItemWidget.setImageURI(it)
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

    private fun setupAction(){
        binding.btnRegister.setOnClickListener {
            val name = binding.editName.text.toString()
            val username = binding.editUsername.text.toString()
            val age = binding.editAge.text.toString().toInt()
            val email = binding.editEmail.text.toString()
            val password = binding.editPassword.text.toString()
            val weight = binding.editBeratBadan.text.toString().toFloat()
            val height = binding.editTinggiBadan.text.toString().toFloat()
            val bloodSugar = binding.editGulaDarah.text.toString().toFloat()
            val bloodPressure = binding.editTekananDarah.text.toString().toFloat()
            val healthCondition = binding.spinnerHealthCondition.selectedItem.toString()
            val activityLevel = binding.spinnerActivityLevel.selectedItem.toString()
            val image = viewModel.currentImage

            val heightInMeters = height / 100
            val bmiCalculated = weight / (heightInMeters * heightInMeters)

            if (image != null) {
                viewModel.register(
                    name, username, age, email, password,
                    weight, height, bloodSugar, bloodPressure,
                    bmiCalculated, healthCondition, activityLevel, image).observe(this) { result ->
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

    companion object {
        const val REQUIRED_PERMISSION = android.Manifest.permission.CAMERA
    }
}