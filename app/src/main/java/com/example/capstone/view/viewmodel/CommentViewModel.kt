package com.example.capstone.view.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.capstone.data.AppRepository
import com.example.capstone.data.Result
import com.example.capstone.data.remote.response.CommentRequest

class CommentViewModel(private val repository: AppRepository) : ViewModel() {

    fun postComment(postId: String, commentRequest: CommentRequest): LiveData<Result<CommentRequest>> {
        return repository.postComment(postId, commentRequest)
    }

    fun refresh(postId: String) {
        getPostComments(postId)
    }

    fun getPostComments(postId: String): LiveData<Result<List<CommentRequest>>> {
        return repository.getPostComments(postId)
    }
}