package com.example.capstone.view.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.capstone.data.AppRepository
import com.example.capstone.data.Result
import com.example.capstone.data.pref.UserModel
import com.example.capstone.data.remote.response.MealsResponse
import com.example.capstone.data.remote.response.PostResponse
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.launch

class AccountViewModel(private val repository: AppRepository): ViewModel() {
    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun getPostList(): LiveData<Result<PostResponse>> {
        return repository.getPostList()
    }

    fun saveMeal(mealResponse: MealsResponse): Task<Void> {
        return repository.saveMeal(mealResponse)
    }

    private val _meals = MutableLiveData<List<MealsResponse>>()
    val meals: LiveData<List<MealsResponse>> get() = _meals

    fun fetchMealsByUsername(username: String) {
        repository.fetchMealsByUsername(username).observeForever { mealList ->
            _meals.value = mealList // Update ViewModel's LiveData with retrieved meals
        }
    }

    private val _todaysCalories = MutableLiveData<Float>()
    val todaysCalories: LiveData<Float> get() = _todaysCalories

    fun fetchTodaysCalories(username: String) {
        repository.getTodaysCalories(username).observeForever { calories ->
            _todaysCalories.value = calories // Update ViewModel's LiveData with retrieved calories
        }
    }

}