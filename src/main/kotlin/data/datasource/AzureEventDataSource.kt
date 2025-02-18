package com.example.data.datasource

import com.example.data.database.Events
import com.example.data.model.Event
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.util.UUID

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
            it[clubId] = UUID.fromString(event.clubId)
        }
        result.insertedCount > 0
    }

    override suspend fun deleteEvent(id: UUID): Boolean = newSuspendedTransaction(db = database) {
        val deleted = Events.deleteWhere { Events.id eq id }
        deleted > 0
    }

    private fun rowToEvent(row: ResultRow) = Event( //converts the row response from database to Event object
        id = row[Events.id],
        name = row[Events.name],
        description = row[Events.description],
        clubId = row[Events.clubId].toString()
    )
}