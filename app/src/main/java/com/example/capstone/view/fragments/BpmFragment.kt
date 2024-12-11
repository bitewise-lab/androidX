package com.example.capstone.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.capstone.databinding.FragmentBpmBinding
import com.example.capstone.view.viewmodel.AccountViewModel
import com.example.capstone.view.viewmodel.ViewModelFactory


class BpmFragment : Fragment() {

    private val viewModel by viewModels<AccountViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }

    private var _binding: FragmentBpmBinding?= null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding =  FragmentBpmBinding.inflate(inflater, container, false)
        val root: View = binding.root


        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getSession().observe(requireActivity()) { user ->
            val BMI = user.bmi.toFloat()
            val BloodSugar = user.blood_sugar.toFloat()
            val BloodPressure = user.blood_pressure.toFloat()
            startBMIBar(BMI)
            startBloodSugarBar(BloodSugar)
            startBloodPressureBar(BloodPressure)
        }
    }

    private fun startBMIBar(bmi: Float) {
        binding.BMiBar.setMaxValues(30F)
        binding.BMiBar.setCurrentValues(bmi)
        if(bmi < 18.5F ){
            binding.BMiBar.setUnit("UnderWeight")
        } else if (18.5F <= bmi && bmi < 25F){
            binding.BMiBar.setUnit("Normal")
        } else if (25F <= bmi && bmi < 30F){
            binding.BMiBar.setUnit("OverWeight")
        } else {
            binding.BMiBar.setUnit("Obesity")
        }
    }

    private fun startBloodSugarBar(bloodSugar: Float){
        binding.BloodSugarBar.setMaxValues(240F)
        binding.BloodSugarBar.setCurrentValues(bloodSugar)
        if(54F <= bloodSugar  && bloodSugar < 70F ){
            binding.BloodSugarBar.setUnit("Low")
        } else if (70F <= bloodSugar && bloodSugar < 200F){
            binding.BloodSugarBar.setUnit("Normal")
        } else if (200F <= bloodSugar && bloodSugar < 240F){
            binding.BloodSugarBar.setUnit("High")
        } else {
            binding.BloodSugarBar.setUnit("Danger")
        }
    }

    private fun startBloodPressureBar(bloodPressure : Float){
        binding.BloodPressureBar.setMaxValues(180F)
        binding.BloodPressureBar.setCurrentValues(bloodPressure)
        if(bloodPressure < 90F ){
            binding.BloodPressureBar.setUnit("Hypotension")
        } else if (90F <= bloodPressure && bloodPressure < 140F){
            binding.BloodPressureBar.setUnit("Normal")
        } else if (140F <= bloodPressure && bloodPressure < 180F){
            binding.BloodPressureBar.setUnit("Hypertension")
        } else {
            binding.BloodPressureBar.setUnit("Crisis")
        }
    }
}