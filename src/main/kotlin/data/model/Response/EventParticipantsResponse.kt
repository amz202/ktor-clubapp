package com.example.data.model.Response

import kotlinx.serialization.Serializable

@Serializable
data class EventParticipantsResponse(
    val id: String,
    val email: String,
    val name: String,
    val eventRole: String
)