package com.example.routes

import com.example.data.datasource.EventNewsDataSource
import com.example.data.datasource.EventsDataSource
import com.example.data.model.Event
import com.example.data.model.MyAuthenticatedUser
import com.example.data.model.Requests.EventNewsRequest
import com.example.data.model.Requests.EventRequest
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
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


fun Route.getEventNews(
    eventNewsDataSource: EventNewsDataSource,
    eventDataSource: EventsDataSource
) {
    authenticate {
        get("/events/{eventId}/news") {
            val eventId = call.parameters["eventId"]?.let { UUID.fromString(it) }
            if (eventId == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid event ID")
                return@get
            }
            val event = eventDataSource.getEvent(eventId)
            if (event == null) {
                call.respond(HttpStatusCode.NotFound, "Event not found")
                return@get
            }
            val news = eventNewsDataSource.getEventNews(eventId.toString())
            call.respond(HttpStatusCode.OK, news)
        }
    }
}

fun Route.createEventNews(
    eventNewsDataSource: EventNewsDataSource,
    eventDataSource: EventsDataSource
){
    authenticate {
        post("/events/{eventId}/news"){
            val eventId = call.parameters["eventId"]?.let { UUID.fromString(it) }
            if (eventId == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid event ID")
                return@post
            }
            val event = eventDataSource.getEvent(eventId)
            if (event == null) {
                call.respond(HttpStatusCode.NotFound, "Event not found")
                return@post
            }
            val eventNewsRequest = try {
                call.receive<EventNewsRequest>()
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, "Invalid request body")
                return@post
            }
            val userId = call.principal<MyAuthenticatedUser>()?.id
            if (userId == null) {
                call.respond(HttpStatusCode.Unauthorized, "User not authenticated")
                return@post
            }
            val result = eventNewsDataSource.createNews(eventNewsRequest)
            if (result) {
                call.respond(HttpStatusCode.Created, "Event news created successfully")
            } else {
                call.respond(HttpStatusCode.InternalServerError, "Failed to create event news")
            }
        }
    }
}

fun Route.deleteEventNews(
    eventNewsDataSource: EventNewsDataSource,
    eventDataSource: EventsDataSource
) {
    authenticate {
        delete("/events/{eventId}/news") {
            val eventId = call.parameters["eventId"]?.let { UUID.fromString(it) }
            if (eventId == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid event ID")
                return@delete
            }
            val event = eventDataSource.getEvent(eventId)
            if (event == null) {
                call.respond(HttpStatusCode.NotFound, "Event not found")
                return@delete
            }
            val eventNewsRequest = try {
                call.receive<EventNewsRequest>()
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, "Invalid request body")
                return@delete
            }
            val result = eventNewsDataSource.deleteEventNews(eventNewsRequest)
            if (result) {
                call.respond(HttpStatusCode.OK, "Event news deleted successfully")
            } else {
                call.respond(HttpStatusCode.InternalServerError, "Failed to delete event news")
            }
        }
    }
}

fun Route.getEventNewsById(
    eventNewsDataSource: EventNewsDataSource,
    eventDataSource: EventsDataSource
) {
    authenticate {
        get("/events/news/{eventNewsId}") {
            val eventNewsId = call.parameters["eventNewsId"]?.let { UUID.fromString(it) }
            if (eventNewsId == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid event news ID")
                return@get
            }
            val eventNews = eventNewsDataSource.getEventNewsById(eventNewsId.toString())
            if (eventNews == null) {
                call.respond(HttpStatusCode.NotFound, "Event news not found")
                return@get
            }
            call.respond(HttpStatusCode.OK, eventNews)
        }
    }
}