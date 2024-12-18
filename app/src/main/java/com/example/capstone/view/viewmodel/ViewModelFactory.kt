package com.example.capstone.view.viewmodel

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.capstone.data.AppRepository
import com.example.capstone.data.pref.UserPref
import com.example.capstone.di.Injection


val Context.dataStore by preferencesDataStore(name = "user_preferences")
class ViewModelFactory(private val repository: AppRepository, preferences: UserPref) :
    ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                RegisterViewModel(repository) as T
            }
            modelClass.isAssignableFrom(SignInViewModel::class.java) -> {
                SignInViewModel(repository) as T
            }
            modelClass.isAssignableFrom(PostViewModel::class.java) -> {
                PostViewModel(repository) as T
            }
            modelClass.isAssignableFrom(AccountViewModel::class.java) -> {
                AccountViewModel(repository) as T
            }
            modelClass.isAssignableFrom(CommentViewModel::class.java) -> {
                CommentViewModel(repository) as T
            }
            modelClass.isAssignableFrom(OcrViewModel::class.java) -> {
                OcrViewModel(repository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null
        fun getInstance(context: Context): ViewModelFactory =
            instance ?: synchronized(this) {
                val preferences = UserPref.getInstance(context.dataStore)
                instance ?: ViewModelFactory(
                    Injection.provideAppRepository(context),
                    preferences
                )
            }.also { instance = it }

    }
}