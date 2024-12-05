package com.example.capstone.data

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.capstone.data.pref.UserModel
import com.example.capstone.data.pref.UserPref
import com.example.capstone.data.remote.response.SignInResponse
import com.example.capstone.data.remote.response.SignUpResponse
import com.example.capstone.data.remote.retrofit.ApiService
import com.example.capstone.data.remote.response.LoginRequest
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File


class AppRepository(
    private val apiService: ApiService,
    private val userPref: UserPref
) {
    fun register(
        name: String,
        username: String,
        age: Int,
        email: String,
        password: String,
        weight: Float,
        height: Float,
        bloodSugar: Float,
        bloodPressure: Float,
        bmi: Float,
        healthCondition: String,
        activityLevel: String,
        imageURL: Uri
    ): LiveData<Result<SignUpResponse>> = liveData {
        emit(Result.Loading)
        try {
            val nameBody = name.toRequestBody("text/plain".toMediaTypeOrNull())
            val usernameBody = username.toRequestBody("text/plain".toMediaTypeOrNull())
            val ageBody = age.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val emailBody = email.toRequestBody("text/plain".toMediaTypeOrNull())
            val passwordBody = password.toRequestBody("text/plain".toMediaTypeOrNull())
            val weightBody = weight.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val heightBody = height.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val bloodSugarBody =
                bloodSugar.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val bloodPressureBody =
                bloodPressure.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val bmiBody = bmi.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val healthConditionBody =
                healthCondition.toRequestBody("text/plain".toMediaTypeOrNull())
            val activityLevelBody = activityLevel.toRequestBody("text/plain".toMediaTypeOrNull())

            val file = File(imageURL.path!!)
            val requestBody = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData("imageFile", file.name, requestBody)

            val response = apiService.register(
                nameBody, usernameBody, ageBody, emailBody, passwordBody,
                weightBody, heightBody, bloodSugarBody, bloodPressureBody,
                bmiBody, healthConditionBody, activityLevelBody, imagePart
            )
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
            if (response.error == false) {
                response.loginResult?.let { loginResult ->
                    val userModel = UserModel(
                        name = loginResult.name ?: "",
                        username = loginResult.username ?: "",
                        email = loginResult.email ?: "",
                        weight = loginResult.weight ?: "0",
                        height = loginResult.height ?: "0",
                        blood_sugar = loginResult.bloodSugar ?: "0",
                        blood_pressure = loginResult.bloodPressure ?: "0",
                        bmi = loginResult.bmi ?: "0",
                        health_condition = loginResult.healthCondition ?: "",
                        activity_level = loginResult.activityLevel ?: "",
                        imageURL = loginResult.imageUrl ?: "",
                        isLoggedIn = true,
                        token = loginResult.accessToken ?: ""
                    )
                    saveSession(userModel)
                }
            }
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "An error occurred"))
        }
    }

    suspend fun saveToken(token: String) {
        userPref.saveToken(token)
    }

    suspend fun saveSession(user: UserModel) {
        Log.d("USER REPOSITORY", "Saving user session: $user")
        userPref.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPref.getSession()
    }

    suspend fun logout() {
        userPref.logout()
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