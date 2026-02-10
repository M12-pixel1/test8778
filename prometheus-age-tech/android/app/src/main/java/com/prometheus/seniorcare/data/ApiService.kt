package com.prometheus.seniorcare.data

import com.prometheus.seniorcare.data.models.LoginRequest
import com.prometheus.seniorcare.data.models.LoginResponse
import com.prometheus.seniorcare.data.models.SOSRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("seniors/sos/alert")
    suspend fun sendSOSAlert(@Body request: SOSRequest): Response<Unit>
}
