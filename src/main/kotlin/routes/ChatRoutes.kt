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

fun Route.recentChat(chatDataSource: ChatDataSource){
    authenticate {
        get("/{groupId}/recentChat"){
            val groupId = call.parameters["groupId"]
            if (groupId != null) {
                val messages = chatDataSource.getRecentMessages(groupId)
                if (messages.isNotEmpty()) {
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