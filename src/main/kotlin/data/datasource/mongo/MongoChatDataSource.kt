package com.example.data.datasource.mongo

import com.example.data.datasource.ChatDataSource
import com.example.data.model.ChatMessage
import com.example.data.model.Requests.EditMessageRequest
import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.Updates
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.flow.toList

/*
 * Copyright 2025 Abdul Majid
 *
 * This file is part of the backend components developed for the ClubApp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


class MongoChatDataSource(db: MongoDatabase) : ChatDataSource {
    private val chats = db.getCollection<ChatMessage>("chatMessages")

    override suspend fun saveMessage(message: ChatMessage): Boolean {
        return chats.insertOne(message).wasAcknowledged()
    }

    override suspend fun getRecentMessages(groupId: String): List<ChatMessage>? {
        return chats.find(eq("groupId",groupId)).toList()
    }

    override suspend fun deleteMessage(id: String): Boolean {
        val deleteResult =  chats.deleteOne(eq("id",id))
        return deleteResult.deletedCount>0
    }

    override suspend fun editMessage(editMessageRequest: EditMessageRequest): Boolean {
        val updateResult = chats.updateOne(
            eq("id", editMessageRequest.id),
            Updates.set("message", editMessageRequest.newMessage)
        )
        return updateResult.modifiedCount > 0
    }
}