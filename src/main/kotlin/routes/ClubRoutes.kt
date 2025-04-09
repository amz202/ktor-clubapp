package com.example.routes

import com.example.data.datasource.ClubDataSource
import com.example.data.datasource.ClubMemberDataSource
import com.example.data.model.Club
import com.example.data.model.MyAuthenticatedUser
import com.example.data.model.Requests.ClubEventsRequest
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*

fun Route.getClub(clubDataSource: ClubDataSource) {
    get("/clubs/{id}") {
        val clubId = call.parameters["id"]?.let { UUID.fromString(it) }
        if (clubId == null) {
            call.respond(HttpStatusCode.BadRequest, "Invalid club ID")
            return@get
        }
        val club = clubDataSource.getClub(clubId)
        if (club == null) {
            call.respond(HttpStatusCode.NotFound, "Club not found")
        } else {
            call.respond(HttpStatusCode.OK, club)
        }
    }
}
fun Route.getClubs(clubDataSource: ClubDataSource) {
    get("/clubs") {
        val clubs = clubDataSource.getClubs()
        call.respond(clubs)
    }
}

fun Route.createClub(clubDataSource: ClubDataSource, clubMemberDataSource: ClubMemberDataSource) {
    authenticate {
        post("/clubs") {
            val principal = call.principal<MyAuthenticatedUser>()
            if (principal == null) {
                call.respond(HttpStatusCode.Unauthorized, "Unauthorized")
                return@post
            }

            val clubRequest = try {
                call.receive<Club>()
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, "Invalid request body")
                return@post
            }

            if (clubRequest.name.isBlank() || clubRequest.description.isBlank()) {
                call.respond(HttpStatusCode.BadRequest, "Name and description cannot be blank")
                return@post
            }

            val club = Club(
                name = clubRequest.name,
                description = clubRequest.description
            )
            val result = principal.name?.let { clubDataSource.createClub(club, it) }
            if (result == true) {
                call.respond(HttpStatusCode.Created, club)
                // Add the creator as a member of the club
                clubMemberDataSource.joinClub(club.id, principal.id, "creator")
            } else {
                call.respond(HttpStatusCode.InternalServerError, "Couldn't create club")
            }

        }
    }
}
fun Route.deleteClub(clubDataSource: ClubDataSource) {
    delete("/clubs/{id}") {
        val clubId = call.parameters["id"]?.let { UUID.fromString(it) }
        if (clubId == null) {
            call.respond(HttpStatusCode.BadRequest, "Invalid club ID")
            return@delete
        }
        val result = clubDataSource.deleteClub(clubId)
        if (result) {
            call.respond(HttpStatusCode.OK, "Club deleted")
        } else {
            call.respond(HttpStatusCode.NotFound, "Club not found")
        }
    }
}

fun Route.getClubEvents(clubDataSource: ClubDataSource) {
    post("/clubs/events") {
        try {
            val request = call.receive<ClubEventsRequest>()
            val clubId = UUID.fromString(request.clubId) // ✅ Convert String to UUID

            val events = clubDataSource.getClubEvents(clubId)
            if (events == null) {
                call.respond(HttpStatusCode.NotFound, "No events found for the specified club")
            } else {
                call.respond(HttpStatusCode.OK, events)
            }
        } catch (e: IllegalArgumentException) {
            call.respond(HttpStatusCode.BadRequest, "Invalid UUID format")
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, "Something went wrong: ${e.message}")
        }
    }
}