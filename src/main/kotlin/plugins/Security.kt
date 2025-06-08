package com.example.plugins

import com.example.data.datasource.UserDataSource
import com.example.data.model.MyAuthenticatedUser
import com.kborowy.authprovider.firebase.firebase
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File
import org.jetbrains.exposed.sql.*
import org.slf4j.event.*

fun Application.configureSecurity(userDataSource: UserDataSource, serviceAccountPath: String) {
    install(Authentication) {
        firebase {
            adminFile = File(serviceAccountPath)
            realm = "My Server"
            validate { token ->
                val user = userDataSource.getUser(token.uid)
                if (user != null) {
                    MyAuthenticatedUser(
                        id = token.uid, email = token.email ?: "", name = token.name, role = user.role, photoUrl = user.photoUrl
                    )
                }else{
                    MyAuthenticatedUser(
                        id = token.uid, email = token.email ?: "", name = token.name, role = "Student", photoUrl = token.picture
                    )
                }
            }
        }
    }
}
// The request first passes through Firebase authentication, validating the token and creating `MyAuthenticatedUser`.
// The authenticated user is then accessible in routes via "call.principal<MyAuthenticatedUser>()".
// We also attach the role here so that we can access it in all the routes with authenticate()