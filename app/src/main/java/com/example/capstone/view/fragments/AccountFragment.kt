package com.example.capstone.view.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.capstone.R
import com.example.capstone.databinding.ActivityMainBinding
import com.example.capstone.databinding.FragmentAccountBinding
import com.example.capstone.view.activities.WelcomeActivity
import com.example.capstone.view.viewmodel.AccountViewModel
import com.example.capstone.view.viewmodel.ViewModelFactory


class AccountFragment : Fragment() {

    private val viewModel by viewModels<AccountViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }

    private var _binding: FragmentAccountBinding?= null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAccountBinding.inflate(layoutInflater)
        val root: View = binding.root

        viewModel.getSession().observe(requireActivity()) { user ->
            if (!user.isLoggedIn) {
                startActivity(Intent(activity, WelcomeActivity::class.java))
                activity?.finish()
            } else {
                binding.TVUsername.text = user.username
                binding.TVName.text = user.name
                Glide.with(binding.ImageAvatar.context)
                    .load(user.imageURL)
                    .error(R.drawable.ic_image)
                    .into(binding.ImageAvatar)
            }
        }
        return root
    }
}