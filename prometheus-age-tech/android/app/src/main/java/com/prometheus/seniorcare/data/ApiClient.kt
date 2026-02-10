package com.prometheus.seniorcare.data

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

object ApiClient {
    private const val BASE_URL = "http://10.0.2.2:8000/" // Localhost for emulator
    // For real device: "http://YOUR_SERVER_IP:8000/"

    fun createService(token: String? = null): ApiService {
        val interceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val clientBuilder = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)

        if (token != null) {
            clientBuilder.addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
                chain.proceed(request)
            }
        }

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(clientBuilder.build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}

interface ApiService {
    @POST("api/v1/auth/login")
    suspend fun login(@Body request: LoginRequest): retrofit2.Response<LoginResponse>

    @GET("api/v1/users/me")
    suspend fun getProfile(): retrofit2.Response<UserProfile>

    @GET("api/v1/seniors/{senior_id}/dashboard")
    suspend fun getSeniorDashboard(@Path("senior_id") seniorId: String): retrofit2.Response<DashboardResponse>

    @POST("api/v1/sos/alert")
    suspend fun sendSOSAlert(@Body request: SOSRequest): retrofit2.Response<SOSResponse>

    @POST("api/v1/seniors/daily-check")
    suspend fun sendDailyCheck(@Body request: DailyCheckRequest): retrofit2.Response<DailyCheckResponse>
}

// Data classes
data class LoginRequest(
    val phone_number: String,
    val password: String
)

data class LoginResponse(
    val access_token: String,
    val token_type: String,
    val user: UserResponse
)

data class UserResponse(
    val id: String,
    val phone_number: String,
    val role: String,
    val full_name: String,
    val status: String,
    val created_at: String
)

data class UserProfile(
    val id: String,
    val phone_number: String,
    val role: String,
    val full_name: String,
    val status: String,
    val created_at: String
)

data class DashboardResponse(
    val senior_id: String,
    val status: String,
    val last_check_in: String? = null
)

data class SOSRequest(
    val senior_id: String,
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long
)

data class SOSResponse(
    val id: String,
    val status: String,
    val message: String? = null
)

data class DailyCheckRequest(
    val senior_id: String,
    val status: String,
    val timestamp: Long
)

data class DailyCheckResponse(
    val id: String,
    val status: String,
    val message: String? = null
)
