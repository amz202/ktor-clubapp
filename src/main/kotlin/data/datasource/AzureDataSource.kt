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

    override suspend fun createClub(club: Club): Boolean = newSuspendedTransaction(db = database) {
        val result = Clubs.insert {
            it[id] = club.id   //'it' refers to the columns in the table
            it[name] = club.name
            it[description] = club.description
        }
        result.insertedCount > 0
    }

    override suspend fun deleteClub(id: UUID): Boolean = newSuspendedTransaction(db = database) {
        val deleted = Clubs.deleteWhere { Clubs.id eq id }
        deleted > 0
    }

    override suspend fun getClubEvents(clubId: UUID): List<Event> = newSuspendedTransaction(db = database) {
        Events.selectAll().where { Events.clubId eq clubId }
            .map { rowToEvent(it) }
    }

    private fun rowToEvent(row: ResultRow) = Event(
        id = row[Events.id],
        clubId = row[Events.clubId].toString(),
        name = row[Events.name],
        description = row[Events.description],
    )

    private fun rowToClub(row: ResultRow) = Club(
        id = row[Clubs.id],
        name = row[Clubs.name],
        description = row[Clubs.description]
    )
}
