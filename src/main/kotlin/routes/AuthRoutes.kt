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
                    schoolName = principal.schoolName
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
