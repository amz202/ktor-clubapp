package com.example.data.model.Requests

import kotlinx.serialization.Serializable

@Serializable
data class EditMessageRequest(
    val id: String,
    val newMessage: String
)