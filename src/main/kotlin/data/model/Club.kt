package com.example.data.model

import com.example.plugins.UUIDSerializer
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.util.*

@Serializable
data class Club(
    @Serializable(with = UUIDSerializer::class) val id: UUID = UUID.randomUUID(),
    val name: String,
    val description: String,
//    val createdBy: String,
    val createdOn: String = LocalDateTime.now().toString()
)
