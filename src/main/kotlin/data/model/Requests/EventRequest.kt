package com.example.data.model.Requests

import kotlinx.serialization.Serializable

@Serializable
data class EventRequest(
    val name: String,
    val description: String,
    val clubId: String?,
    val dateTime : String,
    val location: String,
    val capacity: String?,
    val organizedBy:String
)
