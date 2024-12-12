package com.example.capstone.view.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.capstone.databinding.FragmentRecomendationBinding
import com.example.capstone.view.adapters.RecomendationAdapter
import com.example.capstone.view.viewmodel.OcrViewModel
import com.example.capstone.view.viewmodel.ViewModelFactory
import com.example.capstone.data.pref.UserPref
import com.example.capstone.view.viewmodel.dataStore
import com.example.capstone.data.Result

class RecomendationFragment : Fragment() {
    private lateinit var binding: FragmentRecomendationBinding
    private lateinit var adapter: RecomendationAdapter

    private lateinit var userPref: UserPref

    private val viewModel by viewModels<OcrViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecomendationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userPref = UserPref.getInstance(requireContext().dataStore)

        adapter = RecomendationAdapter()
        binding.rvRecomendation.adapter = adapter
        binding.rvRecomendation.layoutManager = LinearLayoutManager(requireContext())

        observeViewModel()
        viewModel.recommendations.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

    }

    private fun observeViewModel() {
        viewModel.fetchRecommendations().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    val data = result.data
                    if(data.isEmpty()) {
                        binding.tvNoRecomendation.visibility = View.VISIBLE
                        binding.rvRecomendation.visibility = View.GONE
                    } else {
                        binding.tvNoRecomendation.visibility = View.GONE
                        binding.rvRecomendation.visibility = View.VISIBLE
                        adapter.submitList(data)
                    }
                }
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.tvNoRecomendation.visibility = View.VISIBLE
                    binding.rvRecomendation.visibility = View.GONE
                    Log.d("RecomendationFragment", "Error: ${result.error}")
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}