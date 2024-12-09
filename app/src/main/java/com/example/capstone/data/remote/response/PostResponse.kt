package com.example.capstone.data.remote.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

class PostResponse(

	@field:SerializedName("listpost")
	val post: List<Post?>? = null,

	@field:SerializedName("message")
	val message: String? = null
)

@Parcelize
 class Post(

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("photoUrl")
	val photoUrl: String? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

 	@field:SerializedName("imageuser")
	val imageUser: String? = null
): Parcelable
