package com.prometheus.seniorcare.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

/**
 * API client for communicating with the Prometheus AgeTech backend.
 */
object ApiClient {

    private const val BASE_URL = "http://10.0.2.2:8000"
    private var authToken: String? = null

    suspend fun login(username: String, password: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL("$BASE_URL/auth/login")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true

                val body = """{"username":"$username","password":"$password"}"""
                OutputStreamWriter(connection.outputStream).use { it.write(body) }

                if (connection.responseCode == 200) {
                    val response = connection.inputStream.bufferedReader().readText()
                    // Parse token from response (simplified)
                    authToken = parseToken(response)
                    true
                } else {
                    false
                }
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

    suspend fun sendSOSAlert(
        latitude: Double? = null,
        longitude: Double? = null,
        message: String = "Emergency SOS Alert"
    ): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL("$BASE_URL/seniors/sos/alert")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.setRequestProperty("Authorization", "Bearer $authToken")
                connection.doOutput = true

                val body = buildString {
                    append("{\"message\":\"$message\"")
                    latitude?.let { append(",\"latitude\":$it") }
                    longitude?.let { append(",\"longitude\":$it") }
                    append("}")
                }
                OutputStreamWriter(connection.outputStream).use { it.write(body) }

                connection.responseCode == 201
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

    suspend fun dailyCheckIn(seniorId: Int, mood: String, notes: String? = null): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL("$BASE_URL/seniors/$seniorId/checkin")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.setRequestProperty("Authorization", "Bearer $authToken")
                connection.doOutput = true

                val body = buildString {
                    append("{\"mood\":\"$mood\"")
                    notes?.let { append(",\"notes\":\"$it\"") }
                    append("}")
                }
                OutputStreamWriter(connection.outputStream).use { it.write(body) }

                connection.responseCode == 200
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

    private fun parseToken(response: String): String? {
        // Simple JSON parsing for access_token field
        val tokenRegex = """"access_token"\s*:\s*"([^"]+)"""".toRegex()
        return tokenRegex.find(response)?.groupValues?.get(1)
    }
}
