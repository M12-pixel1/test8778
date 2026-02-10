package com.prometheus.seniorcare.data.models

data class SOSRequest(
    val senior_id: String,
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long
)
