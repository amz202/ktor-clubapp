package com.example.data.datasource.azure

import com.example.data.database.ClubMembers
import com.example.data.database.Clubs
import com.example.data.database.EventParticipants
import com.example.data.database.Events
import com.example.data.datasource.ClubDataSource
import com.example.data.model.Club
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.util.UUID
import com.example.data.datasource.helpers.rowToClub
import com.example.data.model.Response.ClubResponse
import com.example.data.model.Response.EventResponse
import org.jetbrains.exposed.sql.javatime.CurrentDateTime

class AzureClubDataSource(private val database: Database) : ClubDataSource {

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

    override suspend fun getMyClubs(userId: String): List<ClubResponse>? = newSuspendedTransaction(db = database) {
        val clubs = ClubMembers.selectAll().where { ClubMembers.userId eq userId }
            .mapNotNull { row ->
                val clubId = row[ClubMembers.clubId]
                val memberCount = calculateMemberCount(clubId)
                Clubs.selectAll().where { Clubs.id eq clubId }
                    .map { clubRow ->
                        ClubResponse(
                            id = clubRow[Clubs.id].toString(),
                            name = clubRow[Clubs.name],
                            description = clubRow[Clubs.description],
                            tags = clubRow[Clubs.tags],
                            createdOn = clubRow[Clubs.createdOn].toString(),
                            createdBy = clubRow[Clubs.createdBy],
                            memberCount = memberCount
                        )
                    }.singleOrNull()
            }
        clubs.ifEmpty { null }
    }

    override suspend fun createClub(club: Club): Boolean = newSuspendedTransaction(db = database) {
        val result = Clubs.insert {
            it[id] = club.id   //'it' refers to the columns in the table
            it[name] = club.name
            it[description] = club.description
            it[createdBy] = club.createdBy
            it[createdOn] = CurrentDateTime
            it[tags] = club.tags
        }
        result.insertedCount > 0
    }

    override suspend fun deleteClub(id: UUID): Boolean = newSuspendedTransaction(db = database) {
        val deleted = Clubs.deleteWhere { Clubs.id eq id }
        deleted > 0
    }
    override suspend fun getClubEvents(clubId: UUID): List<EventResponse>? = newSuspendedTransaction(db = database) {
        val events = Events.selectAll().where { Events.clubId eq clubId }
            .map { event->
                EventResponse(
                    name = event[Events.name],
                    description = event[Events.description],
                    clubId = event[Events.clubId]?.toString(),
                    dateTime = event[Events.dateTime],
                    location = event[Events.location],
                    capacity = event[Events.capacity]?.toString(),
                    organizedBy = event[Events.organizedBy],
                    id = event[Events.id].toString(),
                    attendeeCount = calculateAttendeeCount(event[Events.id]),
                    tags = event[Events.tags]
                )
            }
        events.ifEmpty { null }
    }

    private suspend fun calculateMemberCount(clubId: UUID): Int = newSuspendedTransaction(db = database) {
        ClubMembers.selectAll().where { ClubMembers.clubId eq clubId }.count().toInt()
    }
    private suspend fun calculateAttendeeCount(eventId: UUID): Int = newSuspendedTransaction(db = database) {
        EventParticipants.selectAll().where { EventParticipants.eventId eq eventId }.count().toInt()
    }
}
