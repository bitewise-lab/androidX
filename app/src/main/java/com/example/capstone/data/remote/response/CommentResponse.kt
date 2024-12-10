package com.example.capstone.data.remote.response

data class CommentRequest(
	val commenterId: String = "",
	val commenterName: String = "",
	val comment: String = "",
	val timestamp: String = "",
	val imageUser: String = ""
)