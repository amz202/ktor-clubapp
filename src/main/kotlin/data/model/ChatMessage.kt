package com.example.data.model

import com.example.plugins.ObjectIdSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId

@Serializable
data class ChatMessage(
    @SerialName("id")
    @Serializable(with = ObjectIdSerializer::class)
    val id: ObjectId = ObjectId(),
    val sender: String,
    val message: String,
    val timeStamp: String,
    val groupId: String,
    val senderName: String,
)