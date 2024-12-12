package com.example.capstone.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.example.capstone.data.remote.response.MealsResponse
import com.example.capstone.databinding.AddMealBinding
import com.example.capstone.view.viewmodel.AccountViewModel
import com.example.capstone.view.viewmodel.ViewModelFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddMealFragment : DialogFragment() {
    private var _binding: AddMealBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<AccountViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = AddMealBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnAddMeal.setOnClickListener {
            val mealName = binding.MealText.text.toString()
            val calories = binding.CaloriesText.text.toString()
            val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) // Current date

            // Use LiveData observer
            viewModel.getSession().observe(viewLifecycleOwner) { user ->
                if (user != null) {
                    val username = user.username// Ensure user is not null

                    if (mealName.isNotEmpty() && calories.isNotEmpty()) {
                        // Create a MealsResponse object
                        val mealResponse = MealsResponse(
                            mealsName = mealName,
                            mealsCalories = calories,
                            date = date,
                            user = username
                        )

                        // Save meal data using the ViewModel
                        viewModel.saveMeal(mealResponse).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(requireContext(), "Meal added successfully!", Toast.LENGTH_SHORT).show()
                                dismiss() // Close the dialog after saving
                            } else {
                                Toast.makeText(requireContext(), "Failed to add meal", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clear the binding reference to avoid memory leaks
    }
}

