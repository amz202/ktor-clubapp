package com.example.data.model.Response

import kotlinx.serialization.Serializable

@Serializable
data class EventNewsResponse(
    val id: String,
    val news: String,
    val createdOn: String,
    val eventId: String
)
