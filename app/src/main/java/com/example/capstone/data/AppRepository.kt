package com.example.capstone.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.capstone.data.pref.UserPref
import com.example.capstone.data.remote.response.SignInResponse
import com.example.capstone.data.remote.response.SignUpResponse
import com.example.capstone.data.remote.retrofit.ApiService
import com.example.capstone.data.remote.response.LoginRequest
import com.example.capstone.data.remote.response.RegisterRequest


class AppRepository(
    private val apiService: ApiService,
    private val userPref: UserPref
) {
    fun register(
        name: String,
        username: String,
        email: String,
        password: String,
        berat: Float,
        tinggi: Float,
        gulaDarah: Float,
        kolestrol: Float,
        tekanan: Float
    ): LiveData<Result<SignUpResponse>> = liveData {
        emit(Result.Loading)
        try {
            Log.d("API_REQUEST", "Registering with: $name, $username, $email, $berat, $tinggi, $gulaDarah, $kolestrol, $tekanan")

            val registerRequest = RegisterRequest(
                name,
                username,
                email,
                password,
                berat,
                tinggi,
                gulaDarah,
                kolestrol,
                tekanan
            )

            val response = apiService.register(registerRequest)
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "An error occurred"))
        }
    }

    fun login(email: String, password: String): LiveData<Result<SignInResponse>> = liveData {
        emit(Result.Loading)
        try {
            Log.d("API_REQUEST", "Logging in with: $email, $password")
            val loginRequest = LoginRequest(email, password)
            val response = apiService.login(loginRequest)
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "An error occurred"))
        }
    }

    suspend fun saveToken(token: String) {
        userPref.saveToken(token)
    }




    companion object {
        @Volatile
        private var instance: AppRepository? = null

        fun getInstance(apiService: ApiService, userPref: UserPref): AppRepository =
            instance ?: synchronized(this) {
                instance ?: AppRepository(apiService, userPref).also { instance = it }
            }
    }
}