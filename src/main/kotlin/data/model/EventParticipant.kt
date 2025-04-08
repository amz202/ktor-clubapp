package com.example.data.model

import com.example.plugins.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.*


@Serializable
data class EventParticipant(
    val userId: String,
    @Serializable(with = UUIDSerializer::class) val eventId: UUID,
    val eventRole: String,
    val joinedOn: String
)