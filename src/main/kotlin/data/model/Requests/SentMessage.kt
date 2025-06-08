package com.example.data.model.Requests

import kotlinx.serialization.Serializable

@Serializable
data class SentMessage(
    val sender: String,
    val message: String,
    val groupId: String,
    val senderName: String
)
