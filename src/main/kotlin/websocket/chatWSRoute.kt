package com.example.websocket

import com.example.data.datasource.ChatDataSource
import com.example.data.datasource.ClubMemberDataSource
import com.example.data.model.ChatMessage
import com.example.data.model.MyAuthenticatedUser
import com.example.data.model.Requests.SentMessage
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.principal
import io.ktor.server.routing.Route
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.CloseReason
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.serialization.json.Json
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

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


fun Route.chatWSRoute(chatDataSource: ChatDataSource, clubMemberDataSource: ClubMemberDataSource){
    authenticate {
        webSocket("/chat/{clubId}/{groupId}"){
            val principal = call.principal<MyAuthenticatedUser>()
            if (principal == null) {
                close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "Unauthorized"))
                return@webSocket
            }
            val groupId = call.parameters["groupId"]
            if (groupId == null) {
                close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "Invalid group ID"))
                return@webSocket
            }
            val clubId = call.parameters["clubId"]?.let { UUID.fromString(it) }
            if (clubId == null) {
                close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "Invalid club ID"))
                return@webSocket
            }
            val userId = principal.id
            val isMember = clubMemberDataSource.getClubRole(clubId, userId) != null
            if (!isMember) {
                close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "Access denied"))
                return@webSocket
            }
            ChatSessionManager.register(groupId = groupId, this)
            try {
                for (frame in incoming){
                    if(frame is Frame.Text){
                        val raw = frame.readText()
                        val sentMessage = try {
                            Json.decodeFromString< SentMessage>(raw)
                        } catch (_: Exception) {
                            continue
                        }
                        val chatMessage = ChatMessage(
                            sender = userId,
                            message = sentMessage.message,
                            timeStamp = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME),
                            groupId = sentMessage.groupId,
                            senderName = sentMessage.senderName                       )
                        chatDataSource.saveMessage(chatMessage)
                        ChatSessionManager.broadcast(groupId.toString(), chatMessage)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                ChatSessionManager.unregister(groupId.toString(),this)
            }
        }
    }
}