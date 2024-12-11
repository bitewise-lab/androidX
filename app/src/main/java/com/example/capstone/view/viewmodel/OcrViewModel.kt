package com.example.capstone.view.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.capstone.data.AppRepository
import com.example.capstone.data.remote.response.PredictResponse
import com.example.capstone.data.Result
import com.example.capstone.data.remote.response.RecommendationsItem

class OcrViewModel(private val repository: AppRepository): ViewModel() {
    var currentImage: Uri? = null
    private val _predictions = MutableLiveData<Result<PredictResponse>>()
    val predictions: LiveData<Result<PredictResponse>> = _predictions

    private val _recommendations = MutableLiveData<List<RecommendationsItem>?>()
    val recommendations: MutableLiveData<List<RecommendationsItem>?> get() = _recommendations

    fun predictImage(email: String, image: Uri) = repository.predictImage(email, image)

    fun setRecommendations(recommendations: List<RecommendationsItem>?) {
        _recommendations.value = recommendations
        Log.d("OcrViewModel", "Recommendations updated: $recommendations")
    }

    fun savePredictionResult(predictionResult: PredictResponse) {
        repository.savePredictionResult(predictionResult)
    }

    fun fetchRecommendations(): LiveData<Result<List<RecommendationsItem>>> {
        return repository.getRecommendations()
    }

    fun deleteRecommendations() {
        repository.deleteRecommendations()
    }
}