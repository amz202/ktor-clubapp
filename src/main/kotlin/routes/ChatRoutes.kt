package com.example.routes

import com.example.data.datasource.ChatDataSource
import com.example.data.model.Requests.EditMessageRequest
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get

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


fun Route.recentChat(chatDataSource: ChatDataSource){
    authenticate {
        get("/{groupId}/recentChat"){
            val groupId = call.parameters["groupId"]
            if (groupId != null) {
                val messages = chatDataSource.getRecentMessages(groupId)
                if (messages != null && messages.isNotEmpty()) {
                    call.respond(HttpStatusCode.OK, messages)
                } else {
                    call.respond(HttpStatusCode.NoContent, "No recent messages found for this group.")
                }
            } else {
                call.respond(HttpStatusCode.BadRequest, "Invalid group ID.")
            }
        }
    }
}

fun Route.deleteMessage(chatDataSource: ChatDataSource){
    authenticate {
        delete("/{id}/delete"){
            val id = call.parameters["id"]
            if (id != null) {
                val deleted = chatDataSource.deleteMessage(id)
                if (deleted) {
                    call.respond(HttpStatusCode.OK, "Message deleted successfully.")
                } else {
                    call.respond(HttpStatusCode.NotFound, "Message not found.")
                }
            } else {
                call.respond(HttpStatusCode.BadRequest, "Invalid message ID.")
            }
        }
    }
}

fun Route.editMessage(chatDataSource: ChatDataSource){
    authenticate {
        get("/edit"){
            val body = call.receive<EditMessageRequest>()
            val edited = chatDataSource.editMessage(body)
            if (edited) {
                call.respond(HttpStatusCode.OK, "Message edited successfully.")
            } else {
                call.respond(HttpStatusCode.NotFound, "Message not found.")
            }
        }
    }
}