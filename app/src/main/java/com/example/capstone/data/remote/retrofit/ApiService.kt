package com.example.capstone.data.remote.retrofit

import android.net.Uri
import com.example.capstone.BuildConfig
import com.example.capstone.data.remote.response.LoginRequest
import com.example.capstone.data.remote.response.RegisterRequest
import com.example.capstone.data.remote.response.SignInResponse
import com.example.capstone.data.remote.response.SignUpResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
@Multipart
@POST("register")
suspend fun register(
    @Part("name") name: RequestBody,
    @Part("username") username: RequestBody,
    @Part("age") age: RequestBody,
    @Part("email") email: RequestBody,
    @Part("password") password: RequestBody,
    @Part("weight") weight: RequestBody,
    @Part("height") height: RequestBody,
    @Part("blood_sugar") bloodSugar: RequestBody,
    @Part("blood_pressure") bloodPressure: RequestBody,
    @Part("bmi") bmi: RequestBody,
    @Part("health_condition") healthCondition: RequestBody,
    @Part("activity_level") activityLevel: RequestBody,
    @Part imageURL: MultipartBody.Part
): SignUpResponse


    @POST("login")
    suspend fun login(@Body loginRequest: LoginRequest): SignInResponse
}
