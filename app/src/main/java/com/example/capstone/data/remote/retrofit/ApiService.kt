package com.example.capstone.data.remote.retrofit

import com.example.capstone.BuildConfig
import com.example.capstone.data.remote.response.LoginRequest
import com.example.capstone.data.remote.response.RegisterRequest
import com.example.capstone.data.remote.response.SignInResponse
import com.example.capstone.data.remote.response.SignUpResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("register")
    suspend fun register(@Body registerRequest: RegisterRequest): SignUpResponse

    @POST("login")
    suspend fun login(@Body loginRequest: LoginRequest): SignInResponse
}
