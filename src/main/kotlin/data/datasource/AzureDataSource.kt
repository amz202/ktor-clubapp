package com.example.data.datasource

import com.example.data.database.ClubMembers
import com.example.data.database.Clubs
import com.example.data.database.Events
import com.example.data.model.Club
import com.example.data.model.Event
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.util.UUID
import com.example.data.datasource.helpers.rowToEvent
import com.example.data.datasource.helpers.rowToClub
import com.example.data.model.Response.ClubResponse

class AzureDataSource(private val database: Database) : ClubDataSource {

    override suspend fun getClub(id: UUID): ClubResponse? = newSuspendedTransaction(db = database) {
        val club = Clubs.selectAll().where { Clubs.id eq id }
            .map { rowToClub(it) }
            .singleOrNull()

        club?.let {
            val memberCount = calculateMemberCount(id)
            ClubResponse(
                name = it.name,
                description = it.description,
                tags = it.tags,
                createdOn = it.createdOn,
                id = it.id.toString(),
                memberCount = memberCount,
                createdBy = it.createdBy
            )
        }
    }

    override suspend fun getClubs(): List<ClubResponse> = newSuspendedTransaction(db = database) {
        Clubs.selectAll()
            .map { rowToClub(it) }
            .map { club ->
                val memberCount = calculateMemberCount(club.id)
                ClubResponse(
                    name = club.name,
                    description = club.description,
                    tags = club.tags,
                    createdOn = club.createdOn,
                    id = club.id.toString(),
                    memberCount = memberCount,
                    createdBy = club.createdBy
                )
            }
    }

    override suspend fun createClub(club: Club): Boolean = newSuspendedTransaction(db = database) {
        val result = Clubs.insert {
            it[id] = club.id   //'it' refers to the columns in the table
            it[name] = club.name
            it[description] = club.description
            it[createdBy] = club.createdBy
            it[createdOn] = org.jetbrains.exposed.sql.javatime.CurrentDateTime
            it[tags] = club.tags
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

    private suspend fun calculateMemberCount(clubId: UUID): Int = newSuspendedTransaction(db = database) {
        ClubMembers.selectAll().where { ClubMembers.clubId eq clubId }.count().toInt()
    }
}
