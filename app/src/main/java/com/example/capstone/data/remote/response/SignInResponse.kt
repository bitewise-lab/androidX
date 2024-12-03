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

	@field:SerializedName("kolestrol")
	val kolestrol: String? = null,

	@field:SerializedName("berat")
	val berat: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("tekanan")
	val tekanan: String? = null,

	@field:SerializedName("gulaDarah")
	val gulaDarah: String? = null,

	@field:SerializedName("accessToken")
	val accessToken: String? = null,

	@field:SerializedName("tinggi")
	val tinggi: String? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("username")
	val username: String? = null
)
