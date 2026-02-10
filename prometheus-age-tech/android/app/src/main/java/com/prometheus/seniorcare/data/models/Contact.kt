package com.prometheus.seniorcare.data.models

/**
 * Emergency contact data model.
 */
data class Contact(
    val id: Int,
    val seniorId: Int,
    val name: String,
    val phone: String,
    val relationship: String
)
