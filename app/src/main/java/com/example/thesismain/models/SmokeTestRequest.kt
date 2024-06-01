package com.example.thesismain.models

data class SmokeTestRequest(
    val opacity: Double,
    val smoke_result: String,
    val createdAt: Long,
    val ownerId: String
)