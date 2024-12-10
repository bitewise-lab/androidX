package com.example.capstone.view.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.capstone.data.AppRepository
import com.example.capstone.data.pref.UserModel
import kotlinx.coroutines.launch

class SignInViewModel(private val repository: AppRepository): ViewModel() {
    fun signIn(email: String, password: String) = repository.login(email, password)

    fun saveToken(token: String) {
        viewModelScope.launch {
            repository.saveToken(token)
        }
    }

     suspend fun saveSessionImageUrl(name:String, image: String) {
        repository.saveSessionImgUrl(name, image)
    }

    suspend fun getToken(): String? {
        return repository.getToken()
    }

    fun saveSession(userModel: UserModel) {
        viewModelScope.launch {
            repository.saveSession(userModel)
        }
    }
}