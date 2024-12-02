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

	@field:SerializedName("accessToken")
	val accessToken: String? = null,

	@field:SerializedName("userId")
	val userId: Int? = null,

	@field:SerializedName("username")
	val username: String? = null
)
