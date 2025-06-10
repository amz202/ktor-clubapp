package com.example.routes

import com.example.data.datasource.UserDataSource
import com.example.data.model.Club
import com.example.data.model.MyAuthenticatedUser
import com.example.data.model.Requests.RoleRequest
import com.example.data.model.Requests.UserRoleRequest
import com.example.data.model.User
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

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


fun Route.login(userDataSource: UserDataSource) {
    authenticate {
        post("/login") {
            val principal = call.principal<MyAuthenticatedUser>()
            if (principal == null) {
                call.respond(HttpStatusCode.Unauthorized, TextContent("Invalid token", ContentType.Text.Plain))
                return@post
            }
            val existingUser = userDataSource.getUser(principal.id)

            if (existingUser == null) {
                val newUser = User(
                    id = principal.id,
                    email = principal.email,
                    name = principal.email.split("@")[0],
                    role = "student",
                    schoolName = principal.schoolName,
                    photoUrl = principal.photoUrl
                )
                userDataSource.createUser(newUser)
                call.respond(HttpStatusCode.Created, newUser)
            } else {
                call.respond(HttpStatusCode.OK, existingUser)
            }
        }
    }
}

fun Route.changeRole(userDataSource: UserDataSource) {
    post("/change-role") {
        val request = try {
            call.receive<UserRoleRequest>()
        } catch (e: Exception) {
            call.respond(HttpStatusCode.BadRequest, "Invalid request body")
            return@post
        }
        val id = request.id
        val role = request.role
        val changeRole = userDataSource.changeRole(id = id, role = role)
        if (changeRole) {
            call.respond(HttpStatusCode.OK, "Role changed")
        } else {
            call.respond(HttpStatusCode.NotFound, "User not found")
        }
    }
}
