package com.example.data.model.Requests

import kotlinx.serialization.Serializable

@Serializable
data class UserRoleRequest(
    val role: String,
    val id:String
)