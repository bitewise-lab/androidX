package com.example.capstone.view.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.capstone.R
import com.example.capstone.data.pref.UserPref
import com.example.capstone.databinding.FragmentAccountBinding
import com.example.capstone.view.activities.WelcomeActivity
import com.example.capstone.view.adapters.MyPostAdapter
import com.example.capstone.view.viewmodel.AccountViewModel
import com.example.capstone.view.viewmodel.ViewModelFactory
import com.example.capstone.view.viewmodel.dataStore
import com.example.capstone.data.Result
import com.example.capstone.data.pref.UserModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

//
//class AccountFragment : Fragment() {
//
//    private val viewModel by viewModels<AccountViewModel> {
//        ViewModelFactory.getInstance(requireActivity())
//    }
//
//    private lateinit var myPostAdapter: MyPostAdapter
//    private var _binding: FragmentAccountBinding?= null
//    private val binding get() = _binding!!
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        // Inflate the layout for this fragment
//        _binding = FragmentAccountBinding.inflate(layoutInflater)
//        val root: View = binding.root
//        return root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        myPostAdapter = MyPostAdapter()
//        binding.rvPostPerAccount.layoutManager = GridLayoutManager(context, 3)
//        binding.rvPostPerAccount.adapter = myPostAdapter
//
//
//        viewModel.getSession().observe(requireActivity()) { user ->
//            if (!user.isLoggedIn) {
//                startActivity(Intent(activity, WelcomeActivity::class.java))
//                activity?.finish()
//            } else {
//                binding.TVUsername.text = user.username
//                binding.TVName.text = user.name
//                Glide.with(binding.ImageAvatar.context)
//                    .load(user.imageURL)
//                    .error(R.drawable.ic_image)
//                    .into(binding.ImageAvatar)
//
//                observePostList(user.username)
//                Log.d("AccountFragment", "Username: ${user.name}")
//            }
//        }
//        setupAction()
//    }
//
//    private fun observePostList(currentUsername: String) {
//        viewModel.getPostList().observe(viewLifecycleOwner) { posts ->
//            when (posts) {
//                is Result.Success -> {
//                    val postResponse = posts.data
//                    val filteredPosts = postResponse.post?.filter { post ->
//                        post?.name == currentUsername
//                    }
//                    Log.d("AccountFragment", "Post: $filteredPosts")
//                    myPostAdapter.submitList(filteredPosts)
//                }
//                is Result.Error -> {
//                    Toast.makeText(context, posts.error, Toast.LENGTH_SHORT).show()
//                }
//                is Result.Loading -> {
//                    // Optionally handle loading state
//                }
//            }
//        }
//    }
//
//    private fun setupAction() {
//        binding.btnLogout.setOnClickListener {
//            val builder = AlertDialog.Builder(requireContext())
//            builder.setTitle(R.string.sign_out)
//            builder.setMessage(R.string.sign_out_confirmation)
//            builder.setPositiveButton(R.string.yes) { _, _ ->
//                lifecycleScope.launch {
//                    val userPref = UserPref.getInstance(dataStore = requireContext().dataStore)
//                    Log.d("AccountFragment", "${userPref.getToken().first()}")
//                    userPref.clearToken()
//                    Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT)
//                        .show()
//
//                    parentFragmentManager.popBackStack()
//                    startActivity(Intent(requireContext(), WelcomeActivity::class.java))
//                }
//            }
//            builder.setNegativeButton(R.string.no) { dialog, _ ->
//                dialog.dismiss()
//            }
//            builder.show()
//        }
//    }
//}

class AccountFragment : Fragment() {

    private val viewModel by viewModels<AccountViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }

    private lateinit var myPostAdapter: MyPostAdapter
    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        myPostAdapter = MyPostAdapter()
        binding.rvPostPerAccount.layoutManager = GridLayoutManager(context, 3)
        binding.rvPostPerAccount.adapter = myPostAdapter

        viewModel.getSession().observe(viewLifecycleOwner) { user ->
            if (!user.isLoggedIn) {
                navigateToWelcomeActivity()
            } else {
                setupUserDetails(user)
                observePostList(user.username)
            }
        }

        setupAction()
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
                    val filteredPosts = posts.data.post?.filter { it?.name == currentUsername }
                    myPostAdapter.submitList(filteredPosts)
                }
                is Result.Error -> {
                    Toast.makeText(context, posts.error, Toast.LENGTH_SHORT).show()
                }
                is Result.Loading -> {
                    // Optional: Show loading indicator
                }
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
