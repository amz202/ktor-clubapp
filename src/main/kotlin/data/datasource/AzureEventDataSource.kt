package com.example.data.datasource

import com.example.data.database.EventParticipants
import com.example.data.database.Events
import com.example.data.model.Event
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.util.UUID
import com.example.data.datasource.helpers.rowToEvent
import com.example.data.model.Response.EventResponse

class AzureEventDataSource(private val database: Database) : EventsDataSource {

    override suspend fun getEvent(id: UUID): EventResponse? = newSuspendedTransaction(db = database) {
        val event = Events.selectAll().where { Events.id eq id }
            .map { rowToEvent(it) }
            .singleOrNull()

        event?.let {
            val attendeeCount = calculateAttendeeCount(id)
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
                tags = it.tags
            )
        }
    }

    override suspend fun getEvents(): List<EventResponse> = newSuspendedTransaction(db = database) {
        Events.selectAll()
            .map { rowToEvent(it) }
            .map { event ->
                val attendeeCount = calculateAttendeeCount(event.id)
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
                    tags = event.tags
                )
            }
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