package com.example.routes

import com.example.data.datasource.ClubDataSource
import com.example.data.datasource.ClubMemberDataSource
import com.example.data.datasource.GroupDataSource
import com.example.data.model.Club
import com.example.data.model.ClubGroup
import com.example.data.model.MyAuthenticatedUser
import com.example.data.model.Requests.ClubRequest
import com.example.utils.getAuthenticatedUser
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.bson.types.ObjectId
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
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

fun Route.createClub(clubDataSource: ClubDataSource, clubMemberDataSource: ClubMemberDataSource, groupDataSource: GroupDataSource) {
    authenticate {
        post("/clubs") {
            val principal = call.getAuthenticatedUser()
            if (principal == null) {
                call.respond(HttpStatusCode.Unauthorized, "Unauthorized")
                return@post
            }

            val clubRequest = try {
                call.receive<ClubRequest>()
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, "Invalid request body")
                return@post
            }

            if (clubRequest.name.isBlank() || clubRequest.description.isBlank()) {
                call.respond(HttpStatusCode.BadRequest, "Name and description cannot be blank")
                return@post
            }

            val club = principal.name?.let {
                Club(
                    name = clubRequest.name,
                    description = clubRequest.description,
                    tags = clubRequest.tags,
                    createdBy = it,
                    createdOn = LocalDateTime.now().toString()
                )
            }
            val result = club?.let { clubDataSource.createClub(it) }
            if (result == true) {
                call.respond(HttpStatusCode.Created, club)
                clubMemberDataSource.approveMember(club.id, principal.id, "creator")
                groupDataSource.createGroup(
                    ClubGroup(
                        name = club.name,
                        clubId = club.id.toString()
                    )
                )
            } else {
                call.respond(HttpStatusCode.InternalServerError, "Couldn't create club")
            }

        }
    }
}

fun Route.deleteClub(clubDataSource: ClubDataSource, groupDataSource: GroupDataSource) {
    authenticate {
        delete("/clubs/{id}") {
//            if(!call.requireRole("creator")){
//                call.respond(HttpStatusCode.Forbidden, "You do not have permission to delete clubs")
//                return@delete
//            }
            val clubId = call.parameters["id"]?.let { UUID.fromString(it) }
            if (clubId == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid club ID")
                return@delete
            }
            val result = clubDataSource.deleteClub(clubId)
            if (result) {
                groupDataSource.deleteGroup(clubId = clubId.toString())
                call.respond(HttpStatusCode.OK, "Club deleted")
            } else {
                call.respond(HttpStatusCode.NotFound, "Club not found")
            }
        }
    }
}

fun Route.getMyClubs(clubDataSource: ClubDataSource) {
    authenticate {
        get("/user/clubs") {
            val principal = call.getAuthenticatedUser()
            if (principal == null) {
                call.respond(HttpStatusCode.Unauthorized, "User not authenticated")
                return@get
            }

            val userId = principal.id
            val clubs = clubDataSource.getMyClubs(userId)
            if (clubs != null) {
                call.respond(HttpStatusCode.OK, clubs)
            } else {
                call.respond(HttpStatusCode.NotFound, "No clubs found for this user")
            }
        }
    }
}

fun Route.getClubEvents(clubDataSource: ClubDataSource) {
    get("/clubs/{id}/events") {
        try {
            val clubId = call.parameters["id"]?.let { UUID.fromString(it) }
            if (clubId == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid club ID")
                return@get
            }

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

fun Route.getClubGroup(groupDataSource: GroupDataSource){
    authenticate {
        get("/{clubId}/group") {
            val clubId = call.parameters["clubId"]
            if (clubId == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid club ID")
                return@get
            }
            val group = groupDataSource.getGroupById(clubId)
            if (group == null) {
                call.respond(HttpStatusCode.NotFound, "No groups found for this club")
            } else {
                call.respond(HttpStatusCode.OK, group)
            }
        }
    }
}

fun Route.openClub(clubDataSource: ClubDataSource) {
    authenticate {
        post("/clubs/{id}/open") {
            val clubId = call.parameters["id"]?.let { UUID.fromString(it) }
            if (clubId == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid club ID")
                return@post
            }
            val result = clubDataSource.openClub(clubId)
            if (result) {
                call.respond(HttpStatusCode.OK, "Club opened successfully")
            } else {
                call.respond(HttpStatusCode.InternalServerError, "Failed to open club")
            }
        }
    }
}

fun Route.closeClub(clubDataSource: ClubDataSource) {
    authenticate {
        post("/clubs/{id}/close") {
            val clubId = call.parameters["id"]?.let { UUID.fromString(it) }
            if (clubId == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid club ID")
                return@post
            }
            val result = clubDataSource.closeClub(clubId)
            if (result) {
                call.respond(HttpStatusCode.OK, "Club closed successfully")
            } else {
                call.respond(HttpStatusCode.InternalServerError, "Failed to close club")
            }
        }
    }
}