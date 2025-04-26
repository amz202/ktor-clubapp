package com.example.data.model

import com.example.plugins.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.UUID

data class EventNews(
    val eventId: String,
    val news: String,
    @Serializable(with = UUIDSerializer::class) val id: UUID = UUID.randomUUID(),
    val createdOn: String,
)