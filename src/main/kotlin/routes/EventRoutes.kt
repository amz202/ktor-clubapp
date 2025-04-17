package com.example.routes

import com.example.data.datasource.EventsDataSource
import com.example.data.model.Event
import com.example.data.model.MyAuthenticatedUser
import com.example.data.model.Requests.EventRequest
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*

fun Route.getEvents(eventsDataSource: EventsDataSource) {
    get("/events") {
        val events = eventsDataSource.getEvents()
        call.respond(events)
    }
}

fun Route.getMyEvents(eventsDataSource: EventsDataSource){
    authenticate {
        get("/user/events"){
            val principal = call.principal<MyAuthenticatedUser>()
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

fun Route.createEvent(eventsDataSource: EventsDataSource) {
    post("/events") {
        try {
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
            val result = eventsDataSource.createEvent(event) //this means that we call this function right away and store its value in variable
            if (result) {
                call.respond(HttpStatusCode.Created, event)
            } else {
                call.respond(HttpStatusCode.InternalServerError, "Couldn't create event")
            }
        } catch (e: Exception) {
            call.respond(HttpStatusCode.BadRequest, "Invalid request body: ${e.message}")
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
        val result = eventsDataSource.deleteEvent(eventId)
        if (result) {
            call.respond(HttpStatusCode.OK, "Event deleted")
        } else {
            call.respond(HttpStatusCode.NotFound, "Event not found")
        }
    }
}