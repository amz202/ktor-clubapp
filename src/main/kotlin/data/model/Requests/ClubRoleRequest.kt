package com.example.data.model.Requests

import kotlinx.serialization.Serializable

@Serializable
data class ClubRoleRequest(
    val role: String
)