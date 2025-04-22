package com.example.data.datasource

import com.example.data.database.ClubMembers
import com.example.data.database.Users
import com.example.data.model.ClubMember
import com.example.data.model.Response.ClubMembersResponse
import com.example.data.model.Response.RoleResponse
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.util.*

class AzureClubMemberDataSource(private val database: Database) : ClubMemberDataSource {
    override suspend fun getClubsMembers(clubId: UUID): List<ClubMembersResponse> = newSuspendedTransaction(db = database) {
        (ClubMembers innerJoin Users). selectAll().where{ClubMembers.clubId eq clubId}
            .map{row->
                ClubMembersResponse(
                    id = row[Users.id],
                    name = row[Users.name],
                    email = row[Users.email],
                    clubRole = row[ClubMembers.clubRole],
                )
            }
    }

    override suspend fun getUsersClubs(userId: String): List<ClubMember> = newSuspendedTransaction(db = database) {
        ClubMembers.selectAll().where { ClubMembers.userId eq userId }
            .map { rowToClubMember(it) }
    }

    override suspend fun joinClub(clubId: UUID, userId: String, role: String): Boolean = newSuspendedTransaction(db = database) {
        val result = ClubMembers.insert {
            it[ClubMembers.clubId] = clubId
            it[ClubMembers.userId] = userId
            it[clubRole] = role
            it[joinedOn] = org.jetbrains.exposed.sql.javatime.CurrentDateTime
        }
        result.insertedCount > 0
    }

    override suspend fun leaveClub(clubId: UUID, userId: String): Boolean = newSuspendedTransaction(db = database) {
        val deleted = ClubMembers.deleteWhere { (ClubMembers.clubId eq clubId) and (ClubMembers.userId eq userId) }
        deleted > 0
    }

    override suspend fun changeRole(clubId: UUID, userId: String, role: String): Boolean = newSuspendedTransaction(db = database) {
        val updated = ClubMembers.update({ (ClubMembers.clubId eq clubId) and (ClubMembers.userId eq userId) }) {
            it[clubRole] = role
        }
        updated > 0
    }

    override suspend fun getClubRole(clubId: UUID, userId: String): RoleResponse? = newSuspendedTransaction(db = database) {
        val role = ClubMembers.selectAll().where { (ClubMembers.clubId eq clubId) and (ClubMembers.userId eq userId) }
            .map { it[ClubMembers.clubRole] }
            .singleOrNull()
        role?.let { RoleResponse(it) }
    }

    private fun rowToClubMember(row: ResultRow): ClubMember {
        return ClubMember(
            userId = row[ClubMembers.userId],
            clubId = row[ClubMembers.clubId],
            clubRole = row[ClubMembers.clubRole],
            joinedOn = row[ClubMembers.joinedOn].toString()
        )
    }
}