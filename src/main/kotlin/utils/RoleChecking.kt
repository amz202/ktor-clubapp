package com.example.utils

import com.example.data.database.ClubMembers
import com.example.data.database.EventParticipants
import com.example.data.model.MyAuthenticatedUser
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.principal
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SqlExpressionBuilder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

fun ApplicationCall.hasClubRole(clubId: UUID, role: String): Boolean {
    val principal = this.principal<MyAuthenticatedUser>() ?: return false

    return transaction {
        ClubMembers.selectAll().where { (ClubMembers.userId eq principal.id) and (ClubMembers.clubId eq clubId) }.singleOrNull()?.let { row ->
            row[ClubMembers.clubRole] == role
        } ?: false
    }
}

fun ApplicationCall.hasEventRole(eventId: UUID, role: String): Boolean {
    val principal = this.principal<MyAuthenticatedUser>() ?: return false

    return transaction {
        EventParticipants.selectAll()
            .where { (EventParticipants.userId eq principal.id) and (EventParticipants.eventId eq eventId) }.singleOrNull()?.let { row ->
            row[EventParticipants.eventRole] == role
        } ?: false
    }
}

fun ApplicationCall.getAuthenticatedUser(): MyAuthenticatedUser? {
    return this.principal<MyAuthenticatedUser>()
}