package com.example.data.model.Requests

import kotlinx.serialization.Serializable

@Serializable
data class RoleRequest(val id: String, val role: String)

