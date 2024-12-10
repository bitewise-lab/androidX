package com.example.capstone.view.fragments

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.capstone.R
import com.example.capstone.databinding.FragmentHomePageBinding
import com.example.capstone.view.fragments.RegisterFragment.Companion.REQUIRED_PERMISSION
import com.example.capstone.view.viewmodel.PostViewModel
import com.example.capstone.view.viewmodel.ViewModelFactory
import com.example.capstone.data.Result
import com.example.capstone.view.adapters.PostAdapter
import com.yalantis.ucrop.UCrop
import java.io.File

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomePageBinding
    private lateinit var postAdapter: PostAdapter

    private val viewModel by viewModels<PostViewModel> {
        ViewModelFactory.getInstance(requireActivity())
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
        // Inflate the layout for this fragment
        binding = FragmentHomePageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postAdapter = PostAdapter()
        binding.rvPost.adapter = postAdapter
        binding.rvPost.layoutManager = LinearLayoutManager(requireContext())


        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }

        viewModel.currentImage?.let {
            showImage()
        }

        binding.IVImage.setOnClickListener {
            startGallery()
        }

        observeViewModel()
        setupView()
        setupAction()

        viewModel.userSession.observe(viewLifecycleOwner) { userSession ->
            val imageUrl = userSession?.imageURL
            if (!imageUrl.isNullOrEmpty()) {
                Glide.with(requireContext())
                    .load(imageUrl)
                    .error(R.drawable.ic_image)
                    .into(binding.IVAccount)
            } else {
                Log.d("UserSession", "No Image URL found")
            }
        }
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
            binding.IVImage.setImageURI(it)
        }
    }

    @Deprecated("Deprecated in Java")
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

    private fun observeViewModel() {
        viewModel.posts.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.linearProgressBar.visibility = View.VISIBLE
                }
                is Result.Success -> {
                    binding.linearProgressBar.visibility = View.GONE
                    val posts = result.data.post?.filterNotNull()
                    if (posts.isNullOrEmpty()) {
                        Toast.makeText(context, "No posts available", Toast.LENGTH_SHORT).show()
                    } else {
                        postAdapter.submitList(posts)
                        Log.d("HomeFragment", "Data fetched: $posts")
                    }
                }
                is Result.Error -> {
                    binding.linearProgressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), "Error: ${result.error}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun setupAction(){
        binding.btnPost.setOnClickListener {
            val desc = binding.editText.text.toString()
            if (desc.isEmpty()) {
                Toast.makeText(context, "Please enter a description", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val image = viewModel.currentImage


            if (image != null) {
                viewModel.post(desc, image).observe(viewLifecycleOwner) { result ->
                    when (result) {
                        is Result.Loading -> {
                            binding.linearProgressBar.visibility = View.VISIBLE
                        }
                        is Result.Error -> {
                            binding.linearProgressBar.visibility = View.GONE
                            Log.e("Post Error", result.error)
                            Toast.makeText(context, "Post failed", Toast.LENGTH_SHORT).show()
                        }
                        is Result.Success -> {
                            binding.linearProgressBar.visibility = View.GONE
                            Toast.makeText(context, "Post success", Toast.LENGTH_SHORT).show()
                            binding.editText.text.clear()
                            binding.IVImage.setImageResource(R.drawable.ic_image)
                            viewModel.currentImage = null
                            viewModel.refresh()
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

    override fun onResume() {
        super.onResume()
        observeViewModel()
    }
}