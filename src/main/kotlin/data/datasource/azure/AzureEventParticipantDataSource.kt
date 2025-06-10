package com.example.data.datasource.azure

import com.example.data.database.EventParticipants
import com.example.data.database.Users
import com.example.data.datasource.EventParticipantDataSource
import com.example.data.model.EventParticipant
import com.example.data.model.Response.EventParticipantsResponse
import com.example.data.model.Response.RoleResponse
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
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


class AzureEventParticipantDataSource(private val database: Database) : EventParticipantDataSource {
    override suspend fun getEventParticipants(eventId: UUID): List<EventParticipantsResponse> = newSuspendedTransaction(db = database) {
        (EventParticipants innerJoin Users).selectAll().where{EventParticipants.eventId eq eventId}
            .map { row ->
                EventParticipantsResponse(
                    id = row[Users.id],
                    name = row[Users.name],
                    email = row[Users.email],
                    eventRole = row[EventParticipants.eventRole],
                )
            }
    }

    override suspend fun getUserEvents(userId: String): List<EventParticipant> = newSuspendedTransaction(db = database) {
        EventParticipants.selectAll().where { EventParticipants.userId eq userId }
            .map { rowToEventParticipant(it) }
    }

    override suspend fun joinEvent(eventId: UUID, userId: String, role: String): Boolean = newSuspendedTransaction(db = database) {
        val result = EventParticipants.insert {
            it[EventParticipants.eventId] = eventId
            it[EventParticipants.userId] = userId
            it[eventRole] = role
            it[joinedOn] = CurrentDateTime
        }
        result.insertedCount > 0
    }

    override suspend fun leaveEvent(eventId: UUID, userId: String): Boolean = newSuspendedTransaction(db = database) {
        val deleted = EventParticipants.deleteWhere { (EventParticipants.eventId eq eventId) and (EventParticipants.userId eq userId) }
        deleted > 0
    }

    override suspend fun changeEventRole(eventId: UUID, userId: String, role: String): Boolean = newSuspendedTransaction(db = database) {
        val updated = EventParticipants.update({ (EventParticipants.eventId eq eventId) and (EventParticipants.userId eq userId) }) {
            it[eventRole] = role
        }
        updated > 0
    }

    override suspend fun getEventRole(eventId: UUID, userId: String): RoleResponse? = newSuspendedTransaction(db = database){
        val role = EventParticipants.selectAll().where { (EventParticipants.eventId eq eventId) and (EventParticipants.userId eq userId) }
            .map { it[EventParticipants.eventRole] }
            .singleOrNull()
        role?.let { RoleResponse(it) }
    }

    private fun rowToEventParticipant(row: ResultRow): EventParticipant {
        return EventParticipant(
            userId = row[EventParticipants.userId],
            eventId = row[EventParticipants.eventId],
            eventRole = row[EventParticipants.eventRole],
            joinedOn = row[EventParticipants.joinedOn].toString()
        )
    }
}