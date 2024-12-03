package com.example.capstone.view.viewmodel

import androidx.lifecycle.ViewModel
import com.example.capstone.data.AppRepository

class RegisterViewModel(private val repository: AppRepository): ViewModel() {
    fun register(
        name: String, username: String, email: String, password: String,
        berat: Float, tinggi: Float, gulaDarah: Float, kolestrol: Float, tekanan: Float
    ) = repository.register(
        name, username, email, password, berat, tinggi,
        gulaDarah, kolestrol, tekanan
    )
}