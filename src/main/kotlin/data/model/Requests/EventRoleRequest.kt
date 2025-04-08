package com.example.data.model.Requests

import kotlinx.serialization.Serializable

@Serializable
data class EventRoleRequest(
    val eventId: String,
    val role : String,
)