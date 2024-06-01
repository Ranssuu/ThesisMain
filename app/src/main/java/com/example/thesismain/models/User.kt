package com.example.thesismain.models

data class User(
    val id: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phoneNumber: String,
    val password: String? = null,
    val profilePictureUrl: String?
)
