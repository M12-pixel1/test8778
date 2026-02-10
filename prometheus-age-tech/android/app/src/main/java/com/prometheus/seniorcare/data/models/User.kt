package com.prometheus.seniorcare.data.models

/**
 * User data model representing a registered user in the system.
 */
data class User(
    val id: Int,
    val username: String,
    val email: String,
    val fullName: String,
    val age: Int,
    val isSenior: Boolean,
    val phone: String? = null,
    val createdAt: String? = null
)
