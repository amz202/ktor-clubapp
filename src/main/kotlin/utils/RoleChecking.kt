package com.example.utils

import com.example.data.model.MyAuthenticatedUser
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.principal

fun ApplicationCall.hasRole(role: String): Boolean {
    val principal = this.principal<MyAuthenticatedUser>()
    return principal?.role == role
}

fun ApplicationCall.requireRole(role: String): Boolean {
    return hasRole(role)
}

fun ApplicationCall.getAuthenticatedUser(): MyAuthenticatedUser? {
    return this.principal<MyAuthenticatedUser>()
}