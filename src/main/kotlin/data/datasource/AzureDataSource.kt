package com.example.data.datasource

import com.example.data.database.Clubs
import com.example.data.database.Events
import com.example.data.model.Club
import com.example.data.model.Event
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID
import com.example.data.datasource.helpers.rowToEvent
import com.example.data.datasource.helpers.rowToClub

class AzureDataSource(private val database: Database) : ClubDataSource {

    override suspend fun getClub(id: UUID): Club? = newSuspendedTransaction(db = database) {
        Clubs.selectAll().where { Clubs.id eq id }
            .map { rowToClub(it) }
            .singleOrNull()
    }

    override suspend fun getClubs(): List<Club> = newSuspendedTransaction(db = database) {
        Clubs.selectAll()
            .map { rowToClub(it) }
    }

    override suspend fun createClub(club: Club, creator:String): Boolean = newSuspendedTransaction(db = database) {
        val result = Clubs.insert {
            it[id] = club.id   //'it' refers to the columns in the table
            it[name] = club.name
            it[description] = club.description
            it[createdBy] = creator
            it[createdOn] = org.jetbrains.exposed.sql.javatime.CurrentDateTime
        }
        result.insertedCount > 0
    }

    override suspend fun deleteClub(id: UUID): Boolean = newSuspendedTransaction(db = database) {
        val deleted = Clubs.deleteWhere { Clubs.id eq id }
        deleted > 0
    }
    override suspend fun getClubEvents(clubId: UUID): List<Event>? = newSuspendedTransaction(db = database) {
        val events = Events.selectAll().where { Events.clubId eq clubId }
            .map { rowToEvent(it) }
        events.ifEmpty { null }
    }
}
