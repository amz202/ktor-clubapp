package com.example.data.model.Response

import kotlinx.serialization.Serializable

@Serializable
data class ClubResponse(
    val name: String,
    val description: String,
    val tags:String,
    val createdOn: String,
    val createdBy: String,
    val id: String,
    val memberCount: Int,
    val status: String
)
