package com.prometheus.seniorcare.data.models

data class LoginRequest(
    val phone_number: String,
    val password: String
)
