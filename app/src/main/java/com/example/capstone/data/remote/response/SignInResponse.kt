package com.example.capstone.data.remote.response

import com.google.gson.annotations.SerializedName

 class SignInResponse(

	@field:SerializedName("loginResult")
	val loginResult: LoginResult? = null,

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)

 class LoginResult(

	@field:SerializedName("activity_level")
	val activityLevel: String? = null,

	@field:SerializedName("imageUrl")
	val imageUrl: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("weight")
	val weight: String? = null,

	@field:SerializedName("blood_pressure")
	val bloodPressure: String? = null,

	@field:SerializedName("blood_sugar")
	val bloodSugar: String? = null,

	@field:SerializedName("accessToken")
	val accessToken: String? = null,

	@field:SerializedName("health_condition")
	val healthCondition: String? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("username")
	val username: String? = null,

	@field:SerializedName("height")
	val height: String? = null,

	@field:SerializedName("bmi")
	val bmi: String? = null
)
