package com.example.routes

import com.example.data.datasource.EventParticipantDataSource
import com.example.data.datasource.EventsDataSource
import com.example.data.model.MyAuthenticatedUser
import com.example.data.model.Requests.RoleRequest
import com.example.utils.getAuthenticatedUser
import com.example.utils.requireRole
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.time.LocalDateTime
import java.util.*

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


fun Route.joinEvent(eventParticipantDataSource: EventParticipantDataSource, eventDataSource: EventsDataSource) {
    authenticate {
        post("/events/{eventId}/join") {
            val eventId = try {
                call.parameters["eventId"]?.let { UUID.fromString(it) }
            } catch (e: IllegalArgumentException) {
                null
            }
            if (eventId == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid event ID")
                return@post
            }
            val event = eventDataSource.getEvent(eventId)
            if(event == null) {
                call.respond(HttpStatusCode.NotFound, "Event not found")
                return@post
            }
            val eventDateTime = event.dateTime.let { LocalDateTime.parse(it) }
            if (eventDateTime.isBefore(LocalDateTime.now())) {
                call.respond(HttpStatusCode.BadRequest, "Cannot join past events")
                return@post
            }
            event.capacity?.toInt()?.let {
                if (event.attendeeCount >= it) {
                    call.respond(HttpStatusCode.BadRequest, "Event has reached capacity")
                    return@post
                }
            }
            val userId = call.principal<MyAuthenticatedUser>()?.id
            if (userId == null) {
                call.respond(HttpStatusCode.Unauthorized, "User not authenticated")
                return@post
            }
            val role = if (eventParticipantDataSource.getEventParticipants(eventId).isEmpty()) "head" else "attendee"
            val result = eventParticipantDataSource.joinEvent(eventId, userId, role)
            if (result) {
                call.respond(HttpStatusCode.OK, "Successfully joined the event")
            } else {
                call.respond(HttpStatusCode.InternalServerError, "Failed to join the event")
            }
        }
    }
}

fun Route.leaveEvent(eventParticipantDataSource: EventParticipantDataSource, eventDataSource: EventsDataSource) {
    authenticate {
        post("/events/{eventId}/leave") {
            val userId = call.getAuthenticatedUser()?.id
            if (userId == null) {
                call.respond(HttpStatusCode.Unauthorized, "User not authenticated")
                return@post
            }
            val eventId = try {
                call.parameters["eventId"]?.let { UUID.fromString(it) }
            } catch (e: IllegalArgumentException) {
                null
            }
            if (eventId == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid event ID")
                return@post
            }
            val eventDateTime = eventDataSource.getEvent(eventId)?.dateTime
            if (eventDateTime != null) {
                val eventTime = try {
                    LocalDateTime.parse(eventDateTime)
                } catch (e: Exception) {
                    null
                }
                if (eventTime != null && eventTime.isBefore(LocalDateTime.now())) {
                    call.respond(HttpStatusCode.BadRequest, "Cannot leave an event that has already occurred")
                    return@post
                }
            }
            val result = eventParticipantDataSource.leaveEvent(eventId, userId)
            if (result) {
                call.respond(HttpStatusCode.OK, "Successfully left the event")
            } else {
                call.respond(HttpStatusCode.InternalServerError, "Failed to leave the event")
            }
        }
    }
}

fun Route.getEventParticipants(eventParticipantDataSource: EventParticipantDataSource) {
    authenticate {
        get("/events/{eventId}/participants") {
            val eventId = try {
                call.parameters["eventId"]?.let { UUID.fromString(it) }
            } catch (e: IllegalArgumentException) {
                null
            }
            if (eventId == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid event ID")
                return@get
            }
            val participants = eventParticipantDataSource.getEventParticipants(eventId)
            if (participants.isNotEmpty()) {
                call.respond(HttpStatusCode.OK, participants)
            } else {
                call.respond(HttpStatusCode.NotFound, "No participants found for this event")
            }
        }
    }
}

fun Route.getUserEvents(eventParticipantDataSource: EventParticipantDataSource) {
    authenticate {
        get("/user/{userId}/events") {
            val userId = call.parameters["userId"]
            if (userId == null) {
                call.respond(HttpStatusCode.BadRequest, "User ID is required")
                return@get
            }
            val events = eventParticipantDataSource.getUserEvents(userId)
            if (events.isNotEmpty()) {
                call.respond(HttpStatusCode.OK, events)
            } else {
                call.respond(HttpStatusCode.NotFound, "No events found for this user")
            }
        }
    }
}

fun Route.changeEventRole(eventParticipantDataSource: EventParticipantDataSource) {
    authenticate {
        post("/events/{eventId}/{userId}/change-role") {
            if(!call.requireRole("admin")){
                call.respond(HttpStatusCode.Forbidden, "You do not have permission to change roles")
                return@post
            }
            val eventId = try {
                call.parameters["eventId"]?.let { UUID.fromString(it) }
            } catch (e: IllegalArgumentException) {
                null
            }
            if (eventId == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid event ID")
                return@post
            }
            val userId = call.parameters["userId"]
            if (userId == null) {
                call.respond(HttpStatusCode.BadRequest, "User ID is required")
                return@post
            }
            val role = try {
                call.receive<RoleRequest>()
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, "Invalid request body")
                return@post
            }
            val result = eventParticipantDataSource.changeEventRole(eventId, userId, role.role)
            if (result) {
                call.respond(HttpStatusCode.OK, "Successfully changed role")
            } else {
                call.respond(HttpStatusCode.InternalServerError, "Failed to change role")
            }
        }
    }
}

fun Route.getEventRole(eventParticipantDataSource: EventParticipantDataSource){
    authenticate {
        get("/events/{eventId}/role") {
            val eventId = try {
                call.parameters["eventId"]?.let { UUID.fromString(it) }
            } catch (e: IllegalArgumentException) {
                null
            }
            val userId = call.principal<MyAuthenticatedUser>()?.id
            if (userId == null) {
                call.respond(HttpStatusCode.Unauthorized, "User not authenticated")
                return@get
            }
            if (eventId == null) {
                call.respond(HttpStatusCode.BadRequest, "Missing or invalid eventId or userId")
                return@get
            }
            val role = eventParticipantDataSource.getEventRole(eventId, userId.toString())
            if (role != null) {
                call.respond(HttpStatusCode.OK, role)
            } else {
                call.respond(HttpStatusCode.NotFound, "No role found for this user in the event")
            }
        }
    }
}