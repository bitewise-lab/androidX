package com.example.capstone.data.pref

data class UserModel (
    val name: String,
    val username: String,
    val email: String,
    val weight: String,
    val height: String,
    val blood_sugar: String,
    val blood_pressure: String,
    val bmi: String,
    val health_condition: String,
    val activity_level: String,
    val imageURL: String,
    val isLoggedIn: Boolean,
    val token: String
)