package com.prometheus.seniorcare.data

import com.prometheus.seniorcare.data.models.Contact
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Retrofit API service interface for communicating with the backend.
 */
interface ApiService {

    @GET("seniors/{seniorId}/contacts")
    suspend fun getEmergencyContacts(@Path("seniorId") seniorId: Int): List<Contact>
}
