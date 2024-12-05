package com.example.capstone.view.fragments

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity.RESULT_OK
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.example.capstone.R
import com.example.capstone.databinding.FragmentRegisterBinding
import com.example.capstone.view.viewmodel.RegisterViewModel
import com.example.capstone.view.viewmodel.ViewModelFactory
import com.example.capstone.data.Result
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.yalantis.ucrop.UCrop
import java.io.File

class RegisterFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentRegisterBinding

    private val viewModel by viewModels<RegisterViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(context, R.string.permission_granted, Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(context, R.string.permission_denied, Toast.LENGTH_LONG).show()
        }
    }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            requireContext(),
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)

        if (!allPermissionsGranted()) {
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

        dialog?.setOnShowListener {
            val bottomSheetDialog = it as BottomSheetDialog
            bottomSheetDialog.setCancelable(false)
            bottomSheetDialog.setCanceledOnTouchOutside(true)

            bottomSheetDialog.behavior.peekHeight = 1500
            bottomSheetDialog.behavior.maxHeight = 1500
        }

        return binding.root
    }

    private fun launchUCrop(sourceUri: Uri) {
        val destinationUri =
            Uri.fromFile(File(requireContext().cacheDir, "cropped_${System.currentTimeMillis()}.jpg"))
        UCrop.of(sourceUri, destinationUri)
            .start(requireContext(), this)
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
            Toast.makeText(requireContext(), "Crop error: ${cropError?.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupAction() {
        binding.btnRegister.setOnClickListener {
            val name = binding.editName.text.toString()
            val username = binding.editUsername.text.toString()
            val ageString = binding.editAge.text.toString()
            val email = binding.editEmail.text.toString()
            val password = binding.editPassword.text.toString()
            val weightString = binding.editBeratBadan.text.toString()
            val heightString = binding.editTinggiBadan.text.toString()
            val bloodSugarString = binding.editGulaDarah.text.toString()
            val bloodPressureString = binding.editTekananDarah.text.toString()
            val healthCondition = binding.spinnerHealthCondition.selectedItem.toString()
            val activityLevel = binding.spinnerActivityLevel.selectedItem.toString()
            val image = viewModel.currentImage

            if (name.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty() ||
                weightString.isEmpty() || heightString.isEmpty() || bloodSugarString.isEmpty() ||
                bloodPressureString.isEmpty() || healthCondition.isEmpty() || activityLevel.isEmpty() || image == null) {
                Toast.makeText(context, "Please fill all the fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val weight = weightString.toFloatOrNull()
            val height = heightString.toFloatOrNull()
            val bloodSugar = bloodSugarString.toFloatOrNull()
            val bloodPressure = bloodPressureString.toFloatOrNull()
            val age = ageString.toIntOrNull()

            if (weight == null || height == null || bloodSugar == null || bloodPressure == null || age == null) {
                Toast.makeText(context, "Please enter valid numeric values for weight, height, blood sugar, and blood pressure", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val heightInMeters = height / 100
            val bmiCalculated = weight / (heightInMeters * heightInMeters)

            viewModel.register(
                name, username, age, email, password,
                weight, height, bloodSugar, bloodPressure,
                bmiCalculated, healthCondition, activityLevel, image
            ).observe(viewLifecycleOwner) { result ->
                when (result) {
                    is Result.Error -> {
                        binding.linearProgressBar.visibility = View.GONE
                        Toast.makeText(context, result.error, Toast.LENGTH_SHORT).show()
                    }

                    is Result.Loading -> {
                        binding.linearProgressBar.visibility = View.VISIBLE
                    }

                    is Result.Success -> {
                        binding.linearProgressBar.visibility = View.GONE
                        Log.d("RegisterFragment", "Success $name")
                        if (result.data.error == true) {
                            Toast.makeText(context, result.data.message, Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, result.data.message, Toast.LENGTH_SHORT).show()
                            // Jika sukses, bisa menavigasi ke fragment lain atau activity
                            parentFragmentManager.popBackStack()
                            dismiss()
                        }
                    }
                }
            }
        }
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            requireActivity().window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            requireActivity().window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        requireActivity().actionBar?.hide()
    }

    companion object {
        const val REQUIRED_PERMISSION = android.Manifest.permission.CAMERA
    }
}
