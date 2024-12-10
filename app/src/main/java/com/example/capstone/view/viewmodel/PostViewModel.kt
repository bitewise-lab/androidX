package com.example.capstone.view.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.capstone.data.AppRepository
import com.example.capstone.data.Result
import com.example.capstone.data.pref.UserModel
import com.example.capstone.data.remote.response.PostResponse

class PostViewModel(private val repository: AppRepository): ViewModel() {
    var currentImage: Uri? = null
    private val _posts = MutableLiveData<Result<PostResponse>>()
    val posts: LiveData<Result<PostResponse>> = _posts
    val userSession: LiveData<UserModel> = repository.getSession().asLiveData()

    init {
        fetchPosts()
    }

    private fun fetchPosts() {
        repository.getPostList().observeForever {
            _posts.value = it
        }
    }

    fun post(description: String, image: Uri) = repository.post(description, image)

    fun refresh() {
        fetchPosts()
    }
}