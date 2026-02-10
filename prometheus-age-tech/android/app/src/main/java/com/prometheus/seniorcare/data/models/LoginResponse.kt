package com.prometheus.seniorcare.data.models

data class LoginResponse(
    val access_token: String,
    val user: LoginUser
)

data class LoginUser(
    val id: String,
    val full_name: String
)
