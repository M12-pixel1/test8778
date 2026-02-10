package com.prometheus.seniorcare

import com.prometheus.seniorcare.data.Contact
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Body

interface ApiService {

    @GET("contacts")
    suspend fun getEmergencyContacts(): List<Contact>

    @POST("daily-check")
    suspend fun submitDailyCheck(@Body status: Map<String, String>): Map<String, String>

    @POST("sos")
    suspend fun sendSOS(@Body location: Map<String, Double>): Map<String, String>
}
