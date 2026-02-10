package com.prometheus.seniorcare.data.models

/**
 * Emergency contact data model.
 */
data class Contact(
    val id: String,
    val name: String,
    val phone: String,
    val relationship: String,
    val is_primary: Boolean
)
