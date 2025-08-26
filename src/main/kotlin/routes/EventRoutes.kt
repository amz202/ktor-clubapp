package com.example.routes

import com.example.data.datasource.EventParticipantDataSource
import com.example.data.datasource.EventsDataSource
import com.example.data.model.Event
import com.example.data.model.MyAuthenticatedUser
import com.example.data.model.Requests.EventRequest
import com.example.utils.getAuthenticatedUser
import com.example.utils.hasClubRole
import com.example.utils.hasEventRole
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


fun Route.getEvents(eventsDataSource: EventsDataSource) {
    get("/events") {
        val events = eventsDataSource.getEvents()
        call.respond(events)
    }
}

fun Route.getMyEvents(eventsDataSource: EventsDataSource){
    authenticate {
        get("/user/events"){
            val principal = call.getAuthenticatedUser()
            if (principal == null) {
                call.respond(HttpStatusCode.Unauthorized, "Unauthorized")
                return@get
            }
            val userId = principal.id
            val events = eventsDataSource.getMyEvents(userId)
            if (events == null) {
                call.respond(HttpStatusCode.NotFound, "No events found for this user")
            } else {
                call.respond(HttpStatusCode.OK, events)
            }
        }
    }
}

fun Route.getEvent(eventsDataSource: EventsDataSource) {
    get("/events/{id}") {
        val eventId = call.parameters["id"]?.let { UUID.fromString(it) }
        if (eventId == null) {
            call.respond(HttpStatusCode.BadRequest, "Invalid event ID")
            return@get
        }
        val event = eventsDataSource.getEvent(eventId)
        if (event == null) {
            call.respond(HttpStatusCode.NotFound, "Event not found")
        } else {
            call.respond(HttpStatusCode.OK, event)
        }
    }
}

fun Route.createEvent(eventsDataSource: EventsDataSource, eventParticipantDataSource: EventParticipantDataSource) {
    authenticate {
        post("/events") {
            try {
                val principal = call.getAuthenticatedUser()
                if (principal == null) {
                    call.respond(HttpStatusCode.Unauthorized, "User not authenticated")
                    return@post
                }
                val eventRequest = call.receive<EventRequest>()
                val event = Event(
                    name = eventRequest.name,
                    description = eventRequest.description,
                    clubId = eventRequest.clubId,
                    dateTime = eventRequest.dateTime,
                    location = eventRequest.location,
                    capacity = eventRequest.capacity,
                    organizedBy = eventRequest.organizedBy,
                    tags = eventRequest.tags
                )
                val eventDateTime = event.dateTime.let { LocalDateTime.parse(it) }
                if (eventDateTime.isBefore(LocalDateTime.now())) {
                    call.respond(HttpStatusCode.BadRequest, "Cannot create past events")
                    return@post
                }
                val result = eventsDataSource.createEvent(event) //this means that we call this function right away and store its value in variable
                if (result) {
                    call.respond(HttpStatusCode.Created, event)
                    eventParticipantDataSource.joinEvent(eventId = event.id, principal.id, "head" )
                } else {
                    call.respond(HttpStatusCode.InternalServerError, "Couldn't create event")
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, "Invalid request body: ${e.message}")
            }
        }
    }
}

fun Route.deleteEvent(eventsDataSource: EventsDataSource) {
    delete("/events/{id}") {
        val eventId = call.parameters["id"]?.let { UUID.fromString(it) }
        if (eventId == null) {
            call.respond(HttpStatusCode.BadRequest, "Invalid event ID")
            return@delete
        }
        if(!call.hasEventRole(eventId = eventId, role = "creator")){
            call.respond(HttpStatusCode.Forbidden, "You do not have permission to change roles")
            return@delete
        }
        val result = eventsDataSource.deleteEvent(eventId)
        if (result) {
            call.respond(HttpStatusCode.OK, "Event deleted")
        } else {
            call.respond(HttpStatusCode.NotFound, "Event not found")
        }
    }
}