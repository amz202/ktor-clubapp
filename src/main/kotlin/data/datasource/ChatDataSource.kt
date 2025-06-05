package com.example.data.datasource

import com.example.data.model.ChatMessage
import com.example.data.model.Requests.EditMessageRequest

interface ChatDataSource {
    suspend fun saveMessage(message: ChatMessage): Boolean
    suspend fun getRecentMessages(groupId:String): List<ChatMessage>
    suspend fun deleteMessage(id: String): Boolean
    suspend fun editMessage(editMessageRequest: EditMessageRequest): Boolean
}