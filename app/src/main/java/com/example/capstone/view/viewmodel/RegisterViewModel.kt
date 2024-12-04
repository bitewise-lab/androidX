package com.example.capstone.view.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.capstone.data.AppRepository

class RegisterViewModel(private val repository: AppRepository): ViewModel() {

    fun register(
        name: String, username: String, age: Int, email: String, password: String,
        weight: Float, height: Float, bloodSugar: Float, bloodPressure: Float, bmi: Float,
        healthCondition: String,
        activityLevel: String, imageUrl: Uri
    ) = repository.register(
        name, username, age, email, password, weight, height, bloodSugar, bloodPressure, bmi,
        healthCondition, activityLevel, imageUrl
    )

    var currentImage: Uri? = null

}