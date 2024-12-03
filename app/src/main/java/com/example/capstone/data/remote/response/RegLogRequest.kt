package com.example.capstone.data.remote.response

data class RegisterRequest(
    val name: String,
    val username: String,
    val email: String,
    val password: String,
    val berat: Float,
    val tinggi: Float,
    val gula_darah: Float,
    val kolestrol: Float,
    val tekanan: Float
)

data class LoginRequest(
    val email: String,
    val password: String
)