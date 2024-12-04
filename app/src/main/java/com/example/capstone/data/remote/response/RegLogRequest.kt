package com.example.capstone.data.remote.response

import android.net.Uri

data class RegisterRequest(
    val name: String,
    val username: String,
    val age: Int,
    val email: String,
    val password: String,
    val weight: Float,
    val height: Float,
    val blood_sugar: Float,
    val blood_pressure: Float,
    val bmi: Float,
    val health_condition: String,
    val activity_level: String,
    val imageURL: String
)

data class LoginRequest(
    val email: String,
    val password: String
)