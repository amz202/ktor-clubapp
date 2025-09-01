package com.example.data.datasource.azure

import com.example.data.database.Clubs
import com.example.data.database.EventParticipants
import com.example.data.database.Events
import com.example.data.datasource.EventsDataSource
import com.example.data.model.Event
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.util.UUID
import com.example.data.datasource.helpers.rowToEvent
import com.example.data.model.Response.EventResponse
import org.jetbrains.exposed.sql.selectAll
import kotlin.text.get

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


class AzureEventDataSource(private val database: Database) : EventsDataSource {

    override suspend fun getEvent(id: UUID): EventResponse? = newSuspendedTransaction(db = database) {
        val event = Events.selectAll().where { Events.id eq id }
            .map { rowToEvent(it) }
            .singleOrNull()

        event?.let {
            val attendeeCount = calculateAttendeeCount(id)
            val clubName = if (!it.clubId.isNullOrBlank()) {
                val clubId = UUID.fromString(it.clubId)
                Clubs.selectAll().where { Clubs.id eq clubId }
                    .map { clubRow -> clubRow[Clubs.name] }
                    .singleOrNull()
            } else {
                null
            }
            EventResponse(
                name = it.name,
                description = it.description,
                clubId = it.clubId,
                dateTime = it.dateTime,
                location = it.location,
                capacity = it.capacity,
                organizedBy = it.organizedBy,
                id = it.id.toString(),
                attendeeCount = attendeeCount,
                tags = it.tags,
                clubName = clubName
            )
        }
    }

    override suspend fun getEvents(): List<EventResponse> = newSuspendedTransaction(db = database) {
        Events.selectAll()
            .map { rowToEvent(it) }
            .map { event ->
                val attendeeCount = calculateAttendeeCount(event.id)
                val clubName = if (!event.clubId.isNullOrBlank()) {
                    val clubId = UUID.fromString(event.clubId)
                    Clubs.selectAll().where { Clubs.id eq clubId }
                        .map { clubRow -> clubRow[Clubs.name] }
                        .singleOrNull()
                } else {
                    null
                }
                EventResponse(
                    name = event.name,
                    description = event.description,
                    clubId = event.clubId,
                    dateTime = event.dateTime,
                    location = event.location,
                    capacity = event.capacity,
                    organizedBy = event.organizedBy,
                    id = event.id.toString(),
                    attendeeCount = attendeeCount,
                    tags = event.tags,
                    clubName = clubName
                )
            }
    }

    override suspend fun getMyEvents(userId: String): List<EventResponse>? = newSuspendedTransaction(db = database){
        val events = EventParticipants.selectAll().where{EventParticipants.userId eq userId}
            .mapNotNull { row ->
                val eventId = row[EventParticipants.eventId]
                val attendeeCount = calculateAttendeeCount(eventId)
                Events.selectAll().where{Events.id eq eventId}
                    .map { eventRow ->
                        val clubName = if (eventRow[Events.clubId] != null) {
                            val clubId = eventRow[Events.clubId]!!
                            Clubs.selectAll().where { Clubs.id eq clubId }
                                .map { clubRow -> clubRow[Clubs.name] }
                                .singleOrNull()
                        } else {
                            null
                        }
                        EventResponse(
                            name = eventRow[Events.name],
                            description = eventRow[Events.description],
                            clubId = eventRow[Events.clubId]?.toString(),
                            dateTime = eventRow[Events.dateTime],
                            location = eventRow[Events.location],
                            capacity = eventRow[Events.capacity]?.toString(),
                            organizedBy = eventRow[Events.organizedBy],
                            id = eventRow[Events.id].toString(),
                            attendeeCount = attendeeCount,
                            tags = eventRow[Events.tags],
                            clubName = clubName
                        )
                    }.singleOrNull()
            }
        events.ifEmpty { null }
    }

    override suspend fun createEvent(event: Event): Boolean = newSuspendedTransaction(db = database) {
        val result = Events.insert {
            it[id] = event.id
            it[name] = event.name
            it[description] = event.description
            it[clubId] = event.clubId?.takeIf {
                it.isNotBlank()
            }?.let { UUID.fromString(it) }
            it[dateTime] = event.dateTime
            it[location] = event.location
            it[capacity] = event.capacity?.toInt()
            it[organizedBy] = event.organizedBy
            it[tags] = event.tags
        }
        result.insertedCount > 0
    }

    override suspend fun deleteEvent(id: UUID): Boolean = newSuspendedTransaction(db = database) {
        val deleted = Events.deleteWhere { Events.id eq id }
        deleted > 0
    }

    private suspend fun calculateAttendeeCount(eventId: UUID): Int = newSuspendedTransaction(db = database) {
        EventParticipants.selectAll().where { EventParticipants.eventId eq eventId }.count().toInt()
    }
}