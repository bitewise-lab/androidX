package com.example.capstone.data.pref

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPref private constructor(private val dataStore: DataStore<Preferences>) {

    fun getToken(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[KEY_TOKEN]
        }
    }

    suspend fun saveToken(token: String) {
        dataStore.edit { preferences ->
            preferences[KEY_TOKEN] = token
            Log.d("DATASTORE", "Token saved: $token")
        }
    }

    suspend fun saveSession(user: UserModel){
        dataStore.edit { preferences ->
            preferences[KEY_NAME] = user.name
            preferences[KEY_USERNAME] = user.username
            preferences[KEY_EMAIL] = user.email
            preferences[KEY_WEIGHT] = user.weight
            preferences[KEY_HEIGHT] = user.height
            preferences[KEY_BLOOD_SUGAR] = user.blood_sugar
            preferences[KEY_BLOOD_PRESSURE] = user.blood_pressure
            preferences[KEY_BMI] = user.bmi
            preferences[KEY_HEALTH_CONDITION] = user.health_condition
            preferences[KEY_ACTIVITY_LEVEL] = user.activity_level
            preferences[KEY_IMAGE_URL] = user.imageURL
            preferences[KEY_IS_LOGGED_IN] = true
            preferences[KEY_TOKEN] = user.token
        }
    }

    suspend fun saveSessionImageUrl(name: String, image: String) {
        dataStore.edit { preferences ->
            preferences[KEY_NAME] = name
            preferences[KEY_IMAGE_URL] = image
        }
    }

    fun getSession(): Flow<UserModel>{
        return dataStore.data.map { preferences ->
            UserModel(
                preferences[KEY_NAME] ?: "",
                preferences[KEY_USERNAME] ?: "",
                preferences[KEY_EMAIL] ?: "",
                preferences[KEY_WEIGHT] ?: "0",
                preferences[KEY_HEIGHT] ?: "0",
                preferences[KEY_BLOOD_SUGAR] ?: "0",
                preferences[KEY_BLOOD_PRESSURE] ?: "0",
                preferences[KEY_BMI] ?: "0",
                preferences[KEY_HEALTH_CONDITION] ?: "",
                preferences[KEY_ACTIVITY_LEVEL] ?: "",
                preferences[KEY_IMAGE_URL] ?: "",
                preferences[KEY_IS_LOGGED_IN] ?: false,
                preferences[KEY_TOKEN] ?: ""
            )
        }
    }

    suspend fun logout() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    suspend fun clearToken() {
        dataStore.edit { preferences ->
            preferences.remove(KEY_TOKEN)
        }
    }

    companion object {
        private val KEY_TOKEN = stringPreferencesKey("token")
        @Volatile
        private var INSTANCE: UserPref? = null


        private val KEY_NAME = stringPreferencesKey("name")
        private val KEY_USERNAME = stringPreferencesKey("username")
        private val KEY_EMAIL = stringPreferencesKey("email")
        private val KEY_PASSWORD = stringPreferencesKey("password")
        private val KEY_WEIGHT = stringPreferencesKey("weight")
        private val KEY_HEIGHT = stringPreferencesKey("height")
        private val KEY_BLOOD_SUGAR = stringPreferencesKey("blood_sugar")
        private val KEY_BLOOD_PRESSURE = stringPreferencesKey("blood_pressure")
        private val KEY_BMI = stringPreferencesKey("bmi")
        private val KEY_HEALTH_CONDITION = stringPreferencesKey("health_condition")
        private val KEY_ACTIVITY_LEVEL = stringPreferencesKey("activity_level")
        private val KEY_IMAGE_URL = stringPreferencesKey("imageURL")
        private val KEY_IS_LOGGED_IN = booleanPreferencesKey("isLoggedIn")



        fun getInstance(dataStore: DataStore<Preferences>): UserPref {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPref(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}