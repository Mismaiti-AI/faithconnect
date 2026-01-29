package com.faithconnect.domain.model

/**
 * ChurchProfile domain model representing church information and contact details.
 *
 * This model contains all information about the church including contact information,
 * mission statement, service times, and social media links.
 */
data class ChurchProfile(
    val name: String = "",
    val logoURL: String = "",
    val welcomeMessage: String = "",
    val address: String = "",
    val phone: String = "",
    val website: String = "",
    val email: String = "",
    val mission: String = "",
    val serviceTimes: String = "",
    val socialFacebook: String = ""
)
