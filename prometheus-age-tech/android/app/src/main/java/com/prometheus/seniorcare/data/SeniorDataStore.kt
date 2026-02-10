package com.prometheus.seniorcare.data

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

private val Context.dataStore by preferencesDataStore(name = "senior_preferences")

class SeniorDataStore(context: Context) {
    private val dataStore = context.dataStore

    companion object {
        private val AUTH_TOKEN = stringPreferencesKey("auth_token")
        private val USER_ID = stringPreferencesKey("user_id")
        private val USER_NAME = stringPreferencesKey("user_name")
        private val USER_PHONE = stringPreferencesKey("user_phone")
        private val LAST_DAILY_CHECK = longPreferencesKey("last_daily_check")
    }

    suspend fun saveAuthToken(token: String) {
        dataStore.edit { preferences ->
            preferences[AUTH_TOKEN] = token
        }
    }

    suspend fun saveUserId(id: String) {
        dataStore.edit { preferences ->
            preferences[USER_ID] = id
        }
    }

    suspend fun saveUserName(name: String) {
        dataStore.edit { preferences ->
            preferences[USER_NAME] = name
        }
    }

    suspend fun saveUserPhone(phone: String) {
        dataStore.edit { preferences ->
            preferences[USER_PHONE] = phone
        }
    }

    suspend fun saveLastDailyCheck(timestamp: Long) {
        dataStore.edit { preferences ->
            preferences[LAST_DAILY_CHECK] = timestamp
        }
    }

    fun getAuthToken(): String? {
        return runBlocking {
            dataStore.data.map { it[AUTH_TOKEN] }.first()
        }
    }

    fun getUserId(): String? {
        return runBlocking {
            dataStore.data.map { it[USER_ID] }.first()
        }
    }

    fun getUserName(): String? {
        return runBlocking {
            dataStore.data.map { it[USER_NAME] }.first()
        }
    }

    fun isLoggedIn(): Boolean {
        return getAuthToken() != null && getUserId() != null
    }

    suspend fun clearAuth() {
        dataStore.edit { preferences ->
            preferences.remove(AUTH_TOKEN)
            preferences.remove(USER_ID)
            preferences.remove(USER_NAME)
        }
    }
}
