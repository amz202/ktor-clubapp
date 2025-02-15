package com.example.data.model

import com.example.plugins.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class Event(
    @Serializable(with = UUIDSerializer::class) val id: UUID = UUID.randomUUID(),
    val name: String,
    val description: String,
    val clubId: String
)
