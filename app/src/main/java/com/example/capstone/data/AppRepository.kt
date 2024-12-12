package com.example.capstone.data

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.example.capstone.data.pref.UserModel
import com.example.capstone.data.pref.UserPref
import com.example.capstone.data.remote.response.CommentRequest
import com.example.capstone.data.remote.response.SignInResponse
import com.example.capstone.data.remote.response.SignUpResponse
import com.example.capstone.data.remote.retrofit.ApiService
import com.example.capstone.data.remote.response.LoginRequest
import com.example.capstone.data.remote.response.MealsResponse
import com.example.capstone.data.remote.response.PostResponse
import com.example.capstone.data.remote.response.PredictResponse
import com.example.capstone.data.remote.response.RecommendationsItem
import com.google.android.gms.tasks.Task
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class AppRepository(
    private val apiService: ApiService,
    private val userPref: UserPref
) {
    private val database: DatabaseReference = FirebaseDatabase.getInstance().getReference("posts")
    private val databaseMeal : DatabaseReference = FirebaseDatabase.getInstance().getReference("meals")

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

    fun observePostComments(postId: String): LiveData<Result<List<CommentRequest>>> {
        val liveData = MutableLiveData<Result<List<CommentRequest>>>()

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val commentsList = mutableListOf<CommentRequest>()
                for (data in snapshot.children) {
                    val comment = data.getValue(CommentRequest::class.java)
                    if (comment != null) {
                        commentsList.add(comment)
                    }
                }
                liveData.postValue(Result.Success(commentsList))
            }

            override fun onCancelled(error: DatabaseError) {
                liveData.postValue(Result.Error(error.message))
            }
        }

        database.child("posts").child(postId).child("comments").addValueEventListener(listener)

        return liveData
    }

    fun predictImage(
        email: String,
        image: Uri
    ): LiveData<Result<PredictResponse>> = liveData {
        emit(Result.Loading)
        try {
            val descriptionBody = email.toRequestBody("text/plain".toMediaTypeOrNull())

            val file = File(image.path!!)
            if (!file.exists()) {
                Log.e("IMG_PATH", "Image file does not exist")
            }
            val requestBody = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData("file", file.name, requestBody)

            val token = userPref.getToken().first()
            val response = apiService.predict("Bearer $token", descriptionBody, imagePart)
            Log.d("TOKEN_INFO", "Posting with token: $token")
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "An error occurred"))
            val token = userPref.getToken().first()
            Log.d("TOKEN_INFO", "Posting with token: $token")
        }
    }

    fun savePredictionResult(predictionResult: PredictResponse) {
        val database = FirebaseDatabase.getInstance().getReference("predictions")
        val predictionId = database.push().key ?: return
        database.child(predictionId).setValue(predictionResult)
            .addOnSuccessListener {
                Log.d("AppRepository", "Prediction result saved successfully")
            }
            .addOnFailureListener { e ->
                Log.e("AppRepository", "Failed to save prediction result", e)
            }
    }

    fun getRecommendations(): LiveData<Result<List<RecommendationsItem>>> = liveData {
        emit(Result.Loading)
        val database = FirebaseDatabase.getInstance().getReference("predictions")
        val recommendationsLiveData = MutableLiveData<Result<List<RecommendationsItem>>>()

        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val recommendationsList = mutableListOf<RecommendationsItem>()
                for (data in snapshot.children) {
                    val prediction = data.getValue(PredictResponse::class.java)
                    prediction?.recommendations?.let { recommendationsList.addAll(it) }
                }
                recommendationsLiveData.postValue(Result.Success(recommendationsList))
            }

            override fun onCancelled(error: DatabaseError) {
                recommendationsLiveData.postValue(Result.Error(error.message))
            }
        })

        emitSource(recommendationsLiveData)
    }

    fun deleteRecommendations() {
        val database = FirebaseDatabase.getInstance().getReference("predictions")
        database.removeValue()
            .addOnSuccessListener {
                Log.d("AppRepository", "Recommendations deleted successfully")
            }
            .addOnFailureListener { e ->
                Log.e("AppRepository", "Failed to delete recommendations", e)
            }
    }

    fun saveMeal(mealResponse: MealsResponse): Task<Void> {
        val mealRef = databaseMeal.push() // Generate a unique key using push()
        val mealId = mealRef.key // Get the generated key
        return mealRef.setValue(mealResponse.copy(mealsId = mealId)) // Set the value with the generated ID
    }

    fun fetchMealsByUsername(username: String): LiveData<List<MealsResponse>> {
        val mealsLiveData = MutableLiveData<List<MealsResponse>>()

        databaseMeal.orderByChild("user").equalTo(username).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val mealList = mutableListOf<MealsResponse>()
                for (mealSnapshot in snapshot.children) {
                    val meal = mealSnapshot.getValue(MealsResponse::class.java)
                    meal?.let { mealList.add(it) }
                }
                mealsLiveData.value = mealList // Update LiveData with the retrieved meals
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle possible errors
                mealsLiveData.value = emptyList() // Optionally handle error case
            }
        })

        return mealsLiveData
    }

    fun getTodaysCalories(username: String): LiveData<Float> {
        val totalCaloriesLiveData = MutableLiveData<Float>()
        val todayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        databaseMeal.orderByChild("user").equalTo(username).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var totalCalories = 0F
                for (mealSnapshot in snapshot.children) {
                    val meal = mealSnapshot.getValue(MealsResponse::class.java)
                    if (meal != null && meal.date == todayDate) {
                        totalCalories += meal.mealsCalories.toFloat() // Assuming MealsCalories is a String
                    }
                }
                totalCaloriesLiveData.value = totalCalories // Update LiveData with the total calories
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle possible errors
                totalCaloriesLiveData.value = 0F // Optionally handle error case
            }
        })

        return totalCaloriesLiveData
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