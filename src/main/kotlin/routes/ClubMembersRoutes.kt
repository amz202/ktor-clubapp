package com.example.routes

import com.example.data.datasource.ClubMemberDataSource
import com.example.data.model.MyAuthenticatedUser
import com.example.data.model.Requests.RoleRequest
import com.example.utils.getAuthenticatedUser
import com.example.utils.requireRole
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


fun Route.getClubsMembers(clubMemberDataSource: ClubMemberDataSource) {
    authenticate {
        get("/club/{clubId}/members") {
            val clubId = try {
                call.parameters["clubId"]?.let { UUID.fromString(it) }
            } catch (e: IllegalArgumentException) {
                null
            }
            if (clubId == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid club ID")
                return@get
            }
            val members = clubMemberDataSource.getClubsMembers(clubId)
            if (members.isEmpty()) {
                call.respond(HttpStatusCode.NotFound, "No members found for this club")
            } else {
                call.respond(HttpStatusCode.OK, members)
            }
        }
    }
}

fun Route.joinClub(clubMemberDataSource: ClubMemberDataSource) {
    authenticate {
        post("/club/{clubId}/join") {
            val clubId = try {
                call.parameters["clubId"]?.let { UUID.fromString(it) }
            } catch (e: IllegalArgumentException) {
                null
            }
            if (clubId == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid club ID")
                return@post
            }
            val userId = call.principal<MyAuthenticatedUser>()?.id
            if (userId == null) {
                call.respond(HttpStatusCode.Unauthorized, "User not authenticated")
                return@post
            }
            val role = if (clubMemberDataSource.getClubsMembers(clubId).isEmpty()) "admin" else "member"
            val result = clubMemberDataSource.joinClub(clubId, userId, role)
            if (result) {
                call.respond(HttpStatusCode.OK, "Successfully joined club")
            } else {
                call.respond(HttpStatusCode.InternalServerError, "Failed to join club")
            }
        }
    }
}

fun Route.leaveClub(clubMemberDataSource: ClubMemberDataSource) {
    authenticate {
        post("/club/{clubId}/leave") {
            val clubId = try {
                call.parameters["clubId"]?.let { UUID.fromString(it) }
            } catch (e: IllegalArgumentException) {
                null
            }
            if (clubId == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid club ID")
                return@post
            }
            val userId = call.getAuthenticatedUser()?.id
            if (userId == null) {
                call.respond(HttpStatusCode.Unauthorized, "User not authenticated")
                return@post
            }
            val result = clubMemberDataSource.leaveClub(clubId, userId)
            if (result) {
                call.respond(HttpStatusCode.OK, "Successfully left club")
            } else {
                call.respond(HttpStatusCode.InternalServerError, "Failed to leave club")
            }
        }
    }
}

fun Route.getUsersClubs(clubMemberDataSource: ClubMemberDataSource) {
    authenticate {
        get("/user/{userId}/clubs") {
            val userId = call.parameters["userId"]
            if (userId == null) {
                call.respond(HttpStatusCode.BadRequest, "User ID is required")
                return@get
            }
            val clubs = clubMemberDataSource.getUsersClubs(userId)
            if (clubs.isEmpty()) {
                call.respond(HttpStatusCode.NotFound, "No clubs found for this user")
            } else {
                call.respond(HttpStatusCode.OK, clubs)
            }
        }
    }
}

fun Route.changeClubMemberRole(clubMemberDataSource: ClubMemberDataSource) {
    authenticate {
        post("/club/{clubId}/{userId}/change-role") {
            if(!call.requireRole("admin")){
                call.respond(HttpStatusCode.Forbidden, "You do not have permission to change roles")
                return@post
            }
            val clubId = try {
                call.parameters["clubId"]?.let { UUID.fromString(it) }
            } catch (e: IllegalArgumentException) {
                null
            }
            val userId = call.parameters["userId"]

            if (clubId == null || userId == null) {
                call.respond(HttpStatusCode.BadRequest, "Missing or invalid clubId or userId")
                return@post
            }
            val request = try {
                call.receive<RoleRequest>()
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, "Invalid request body")
                return@post
            }

            val result = clubMemberDataSource.changeRole(
                clubId = clubId,
                userId = userId,
                role = request.role
            )

            if (result) {
                call.respond(HttpStatusCode.OK, "Role updated successfully")
            } else {
                call.respond(HttpStatusCode.InternalServerError, "Failed to update role")
            }
        }
    }
}

fun Route.getClubRole(clubMemberDataSource: ClubMemberDataSource){
    authenticate {
        get("/club/{clubId}/role") {
            val clubId = try {
                call.parameters["clubId"]?.let { UUID.fromString(it) }
            } catch (e: IllegalArgumentException) {
                null
            }
            val userId = call.principal<MyAuthenticatedUser>()?.id
            if (userId == null) {
                call.respond(HttpStatusCode.Unauthorized, "User not authenticated")
                return@get
            }
            if (clubId == null) {
                call.respond(HttpStatusCode.BadRequest, "Missing or invalid clubId or userId")
                return@get
            }
            val role = clubMemberDataSource.getClubRole(clubId, userId.toString())
            if (role != null) {
                call.respond(HttpStatusCode.OK, role)
            } else {
                call.respond(HttpStatusCode.NotFound, "Role not found")
            }
        }
    }
}