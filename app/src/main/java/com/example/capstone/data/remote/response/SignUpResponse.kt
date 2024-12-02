package com.example.capstone.data.remote.response

import com.google.gson.annotations.SerializedName

 class SignUpResponse(

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)
