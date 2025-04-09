package com.example.routes

import com.example.data.datasource.AzureEventDataSource
import com.example.data.datasource.EventParticipantDataSource
import com.example.data.datasource.EventsDataSource
import com.example.data.model.MyAuthenticatedUser
import com.example.data.model.Requests.RoleRequest
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.time.LocalDateTime
import java.util.*

fun Route.joinEvent(eventParticipantDataSource: EventParticipantDataSource) {
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
            val eventId = try {
                call.parameters["eventId"]?.let { UUID.fromString(it) }
            } catch (e: IllegalArgumentException) {
                null
            }
            if (eventId == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid event ID")
                return@post
            }
            val userId = call.principal<MyAuthenticatedUser>()?.id
            if (userId == null) {
                call.respond(HttpStatusCode.Unauthorized, "User not authenticated")
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
        post("/events/{eventId}/change-role") {
            val eventId = try {
                call.parameters["eventId"]?.let { UUID.fromString(it) }
            } catch (e: IllegalArgumentException) {
                null
            }
            if (eventId == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid event ID")
                return@post
            }
            val userId = call.principal<MyAuthenticatedUser>()?.id
            if (userId == null) {
                call.respond(HttpStatusCode.Unauthorized, "User not authenticated")
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