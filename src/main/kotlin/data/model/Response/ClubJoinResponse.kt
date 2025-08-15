package com.example.data.model.Response

import kotlinx.serialization.Serializable

@Serializable
data class ClubJoinResponse(
    val clubId: String,
    val userId: String,
    val status: String,
    val requestedOn: String
)