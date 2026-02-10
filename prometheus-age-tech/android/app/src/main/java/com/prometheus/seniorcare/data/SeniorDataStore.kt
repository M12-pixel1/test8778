package com.prometheus.seniorcare.data

import android.content.Context
import android.content.SharedPreferences

/**
 * Local data store for caching senior data and preferences.
 */
class SeniorDataStore(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("prometheus_senior_prefs", Context.MODE_PRIVATE)

    fun saveAuthToken(token: String) {
        prefs.edit().putString(KEY_AUTH_TOKEN, token).apply()
    }

    fun getAuthToken(): String? {
        return prefs.getString(KEY_AUTH_TOKEN, null)
    }

    fun saveSeniorId(id: Int) {
        prefs.edit().putInt(KEY_SENIOR_ID, id).apply()
    }

    fun getSeniorId(): Int {
        return prefs.getInt(KEY_SENIOR_ID, -1)
    }

    fun saveUserId(id: String) {
        prefs.edit().putString(KEY_USER_ID, id).apply()
    }

    fun getUserId(): String {
        return prefs.getString(KEY_USER_ID, "") ?: ""
    }

    fun saveSeniorName(name: String) {
        prefs.edit().putString(KEY_SENIOR_NAME, name).apply()
    }

    fun getSeniorName(): String? {
        return prefs.getString(KEY_SENIOR_NAME, null)
    }

    fun saveUserName(name: String) {
        prefs.edit().putString(KEY_USER_NAME, name).apply()
    }

    fun getUserName(): String? {
        return prefs.getString(KEY_USER_NAME, null)
    }

    fun isLoggedIn(): Boolean {
        return getAuthToken() != null
    }

    fun getUserName(): String? {
        return getSeniorName()
    }

    fun clearAuth() {
        clearAll()
    }

    fun clearAll() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val KEY_AUTH_TOKEN = "auth_token"
        private const val KEY_SENIOR_ID = "senior_id"
        private const val KEY_SENIOR_NAME = "senior_name"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_NAME = "user_name"
    }
}
