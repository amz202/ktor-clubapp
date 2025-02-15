package com.example.data.model.Requests

import kotlinx.serialization.Serializable

@Serializable
data class EventRequest(
    val name: String,
    val description: String,
    val clubId: String
)
