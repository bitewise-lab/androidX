package com.example.capstone.view.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.capstone.data.AppRepository
import com.example.capstone.data.Result
import com.example.capstone.data.pref.UserModel
import com.example.capstone.data.remote.response.PostResponse
import kotlinx.coroutines.launch

class AccountViewModel(private val repository: AppRepository): ViewModel() {
//    private val _posts = MutableLiveData<Result<PostResponse>>()
//    val posts: LiveData<Result<PostResponse>> = _posts

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    fun getPostList(): LiveData<Result<PostResponse>> {
        return repository.getPostList()
    }

//    init {
//        fetchPosts()
//    }
//
//    private fun fetchPosts() {
//        repository.getPostList().observeForever {
//            _posts.value = it
//        }
//    }
//
//    fun refresh() {
//        fetchPosts()
//    }
}