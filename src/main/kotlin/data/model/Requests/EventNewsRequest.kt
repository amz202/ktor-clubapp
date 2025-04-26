package com.example.data.model.Requests

import kotlinx.serialization.Serializable

@Serializable
data class EventNewsRequest(
    val news:String,
    val eventId:String,
)