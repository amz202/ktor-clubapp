package com.example.data.model

import com.example.plugins.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class ClubMember(
    val userId: String,
    @Serializable(with = UUIDSerializer::class) val clubId: UUID,
    val clubRole: String = "member",
    val joinedOn: String
)