package com.example.data.datasource

import com.example.data.database.Events
import com.example.data.model.Event
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.util.UUID
import com.example.data.datasource.helpers.rowToEvent

class AzureEventDataSource(private val database: Database) : EventsDataSource {

    override suspend fun getEvent(id: UUID): Event? = newSuspendedTransaction(db = database) {
        Events.selectAll().where { Events.id eq id }
            .map { rowToEvent(it) }
            .singleOrNull()
    }

    override suspend fun getEvents(): List<Event> = newSuspendedTransaction(db = database) {
        Events.selectAll()
            .map { rowToEvent(it) }
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
        }
        result.insertedCount > 0
    }

    override suspend fun deleteEvent(id: UUID): Boolean = newSuspendedTransaction(db = database) {
        val deleted = Events.deleteWhere { Events.id eq id }
        deleted > 0
    }

}