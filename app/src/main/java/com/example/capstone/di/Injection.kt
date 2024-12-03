package com.example.capstone.di

import android.content.Context
import com.example.capstone.data.AppRepository
import com.example.capstone.data.pref.UserPref
import com.example.capstone.data.remote.retrofit.ApiConfig
import com.example.capstone.view.viewmodel.dataStore

object Injection {
    fun provideAppRepository(context: Context): AppRepository {
        val apiService = ApiConfig.getApiService()
        val userPref = UserPref.getInstance(context.dataStore)
        return AppRepository.getInstance(apiService, userPref)
    }
}