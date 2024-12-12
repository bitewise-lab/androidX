package com.example.capstone.view.fragments

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.capstone.R
import com.example.capstone.data.Result
import com.example.capstone.data.pref.UserModel
import com.example.capstone.data.pref.UserPref
import com.example.capstone.databinding.FragmentAccountBinding
import com.example.capstone.view.activities.WelcomeActivity
import com.example.capstone.view.adapters.MyPostAdapter
import com.example.capstone.view.viewmodel.AccountViewModel
import com.example.capstone.view.viewmodel.ViewModelFactory
import com.example.capstone.view.viewmodel.dataStore
import kotlinx.coroutines.launch

class AccountFragment : Fragment() {

    private val viewModel by viewModels<AccountViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }

    private lateinit var myPostAdapter: MyPostAdapter
    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        sharedPreferences = requireContext().getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        myPostAdapter = MyPostAdapter()
        binding.rvPostPerAccount.layoutManager = GridLayoutManager(context, 3)
        binding.rvPostPerAccount.adapter = myPostAdapter

        val isDarkMode = sharedPreferences.getBoolean("is_dark_mode", false)
        updateThemeIcon(isDarkMode)

        viewModel.getSession().observe(viewLifecycleOwner) { user ->
            if (!user.isLoggedIn) {
                navigateToWelcomeActivity()
            } else {
                setupUserDetails(user)
                observePostList(user.username)
            }
        }

        setupAction()
        setupThemeToggle()
    }

    private fun setupUserDetails(user: UserModel) {
        binding.TVUsername.text = user.username
        binding.TVName.text = user.name
        Glide.with(binding.ImageAvatar.context)
            .load(user.imageURL)
            .error(R.drawable.ic_image)
            .into(binding.ImageAvatar)
    }

    private fun observePostList(currentUsername: String) {
        viewModel.getPostList().observe(viewLifecycleOwner) { posts ->
            when (posts) {
                is Result.Success -> {
                    if(posts.data.post.isNullOrEmpty()) {
                        binding.tvNoPost.visibility = View.VISIBLE
                        binding.rvPostPerAccount.visibility = View.GONE
                    } else {
                        binding.tvNoPost.visibility = View.GONE
                        binding.rvPostPerAccount.visibility = View.VISIBLE
                        val filteredPosts = posts.data.post.filter { it?.name == currentUsername }
                        myPostAdapter.submitList(filteredPosts)
                    }
                }
                is Result.Error -> {
                    binding.tvNoPost.visibility = View.VISIBLE
                    binding.rvPostPerAccount.visibility = View.GONE
                    Log.d("AccountFragment", "Error: ${posts.error}")
                }
                is Result.Loading -> { }
            }
        }
    }

    private fun setupAction() {
        binding.btnLogout.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle(R.string.sign_out)
                .setMessage(R.string.sign_out_confirmation)
                .setPositiveButton(R.string.yes) { _, _ ->
                    logoutUser()
                }
                .setNegativeButton(R.string.no) { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }

    private fun setupThemeToggle() {
        binding.btnToggleTheme.setOnClickListener {
            val isDarkMode = sharedPreferences.getBoolean("is_dark_mode", false)
            val newMode = if (isDarkMode) {
                Log.d("ThemeToggle", "Switching to Dark Mode")
                AppCompatDelegate.MODE_NIGHT_NO
            } else {
                Log.d("ThemeToggle", "Switching to Light Mode")
                AppCompatDelegate.MODE_NIGHT_YES
            }

            sharedPreferences.edit().putBoolean("is_dark_mode", !isDarkMode).apply()
            updateThemeIcon(!isDarkMode)
            AppCompatDelegate.setDefaultNightMode(newMode)

            parentFragmentManager.beginTransaction().detach(this).attach(this).commit()
            startActivity(requireActivity().intent)
            Log.d("ThemeToggle", "Activity restarted")
        }
    }

    private fun updateThemeIcon(isDarkMode: Boolean) {
        if (isDarkMode) {
            binding.btnToggleTheme.setIconResource(R.drawable.dark_mode)
        } else {
            binding.btnToggleTheme.setIconResource(R.drawable.light_mode)
        }
    }

    private fun logoutUser() {
        lifecycleScope.launch {
            val userPref = UserPref.getInstance(requireContext().dataStore)
            userPref.clearToken()
            Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show()
            navigateToWelcomeActivity()
        }
    }

    private fun navigateToWelcomeActivity() {
        startActivity(Intent(requireContext(), WelcomeActivity::class.java))
        activity?.finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}