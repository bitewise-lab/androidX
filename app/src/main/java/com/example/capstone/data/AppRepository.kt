package com.example.capstone.data

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.capstone.data.pref.UserModel
import com.example.capstone.data.pref.UserPref
import com.example.capstone.data.remote.response.CommentRequest
import com.example.capstone.data.remote.response.SignInResponse
import com.example.capstone.data.remote.response.SignUpResponse
import com.example.capstone.data.remote.retrofit.ApiService
import com.example.capstone.data.remote.response.LoginRequest
import com.example.capstone.data.remote.response.PostResponse
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


class AppRepository(
    private val apiService: ApiService,
    private val userPref: UserPref
) {
    private val database: DatabaseReference = FirebaseDatabase.getInstance().getReference("posts")

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
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "An error occurred"))
        }
    }

    suspend fun saveToken(token: String) {
        userPref.saveToken(token)
    }

    suspend fun getToken(): String? {
        return userPref.getToken().first()
    }

    suspend fun saveSession(user: UserModel) {
        Log.d("USER REPOSITORY", "Saving user session: $user")
        userPref.saveSession(user)
    }

    suspend fun saveSessionImgUrl(name: String, image: String) {
        Log.d("USER REPOSITORY", "Saving user session: $image")
        userPref.saveSessionImageUrl(name, image)
    }

    fun getSession(): Flow<UserModel> {
        return userPref.getSession()
    }

    suspend fun logout() {
        userPref.logout()
    }

    fun post(
        description: String,
        image: Uri
    ): LiveData<Result<PostResponse>> = liveData {
        emit(Result.Loading)
        try {
            val descriptionBody = description.toRequestBody("text/plain".toMediaTypeOrNull())

            val file = File(image.path!!)
            if (!file.exists()) {
                Log.e("IMG_PATH", "Image file does not exist")
            }
            val requestBody = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData("imageFile", file.name, requestBody)

            val token = userPref.getToken().first()
            val response = apiService.post("Bearer $token", descriptionBody, imagePart)
            Log.d("TOKEN_INFO", "Posting with token: $token")
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "An error occurred"))
            val token = userPref.getToken().first()
            Log.d("TOKEN_INFO", "Posting with token: $token")
        }
    }

    fun getPostList(): LiveData<Result<PostResponse>> = liveData {
        emit(Result.Loading)
        try {
            val token = userPref.getToken().first()
            val response = apiService.getStories("Bearer $token")

            if (response.post.isNullOrEmpty()) {
                emit(Result.Error("No posts found"))
            } else {
                emit(Result.Success(response))
            }
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "An error occurred"))
            Log.e("API_ERROR", "Error saat mengambil data: ${e.message}")
        }
    }

    fun postComment(postId: String, commentRequest: CommentRequest): LiveData<Result<CommentRequest>> = liveData {
        emit(Result.Loading)
        try {
            val user = userPref.getSession().first()
            val commenterName = user.name
            val commenterImageUrl = user.imageURL

            val updatedCommentRequest = commentRequest.copy(
                commenterName = commenterName,
                imageUser = commenterImageUrl
            )

            val commentId = database.child("posts").child(postId).child("comments").push().key ?: ""
            database.child("posts").child(postId).child("comments").child(commentId).setValue(updatedCommentRequest)

            emit(Result.Success(updatedCommentRequest))
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "An error occurred"))
        }
    }

    fun getPostComments(postId: String): LiveData<Result<List<CommentRequest>>> = liveData {
        emit(Result.Loading)
        try {
            val comments = suspendCoroutine<List<CommentRequest>> { cont ->
                database.child("posts").child(postId).child("comments")
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val commentsList = mutableListOf<CommentRequest>()
                            for (data in snapshot.children) {
                                val comment = data.getValue(CommentRequest::class.java)
                                if (comment != null) {
                                    commentsList.add(comment)
                                }
                            }
                            cont.resume(commentsList)
                        }

                        override fun onCancelled(error: DatabaseError) {
                            cont.resumeWithException(Exception(error.message))
                        }
                    })
            }
            emit(Result.Success(comments))
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "An error occurred"))
        }
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