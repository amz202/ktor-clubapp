package com.example.data.model.Requests

import kotlinx.serialization.Serializable

@Serializable
data class ClubRequest(
    val name: String,
    val description: String,
    val tags:String
)
