package com.example.data.model.Response

import kotlinx.serialization.Serializable
import org.bson.types.ObjectId

@Serializable
data class ClubGroupResponse(
    val id: String,
    val name: String,
    val clubId: String
)