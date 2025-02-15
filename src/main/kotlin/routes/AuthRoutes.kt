package com.example.routes

import com.example.data.datasource.UserDataSource
import com.example.data.model.MyAuthenticatedUser
import com.example.data.model.User
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.login(userDataSource: UserDataSource) {
    authenticate {
        get("/login") {
            val principal = call.principal<MyAuthenticatedUser>()
            if (principal == null) {
                call.respond(HttpStatusCode.Unauthorized, TextContent("Invalid token", ContentType.Text.Plain))
                return@get
            }

            val user = User(id = principal.id, email = principal.email, name = principal.email.split("@")[0])
            userDataSource.createUser(user) // Save user if first login
            call.respond(HttpStatusCode.OK, user)
        }
    }
}
