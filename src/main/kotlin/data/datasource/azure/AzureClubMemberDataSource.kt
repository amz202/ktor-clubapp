package com.example.data.datasource.azure

import com.example.data.database.ClubJoinRequest
import com.example.data.database.ClubMembers
import com.example.data.database.Users
import com.example.data.datasource.ClubMemberDataSource
import com.example.data.model.ClubMember
import com.example.data.model.Response.ClubJoinResponse
import com.example.data.model.Response.ClubMembersResponse
import com.example.data.model.Response.RoleResponse
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.selectAll
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

    /*override suspend fun joinClub(clubId: UUID, userId: String, role: String): Boolean = newSuspendedTransaction(db = database) {
        val result = ClubMembers.insert {
            it[ClubMembers.clubId] = clubId
            it[ClubMembers.userId] = userId
            it[clubRole] = role
            it[joinedOn] = CurrentDateTime
        }
        result.insertedCount > 0
    }*/

    override suspend fun joinClub(clubId: UUID, userId: String): Boolean = newSuspendedTransaction(db = database) {
        val result = ClubJoinRequest.insert {
            it[ClubMembers.clubId] = clubId
            it[ClubMembers.userId] = userId
            it[requestedOn] = CurrentDateTime
            it[status] = "pending"
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

    override suspend fun getPendingMembers(clubId: UUID): List<ClubJoinResponse>? = newSuspendedTransaction(db = database){
        val pendingMembers = ClubJoinRequest.selectAll().where { (ClubJoinRequest.clubId eq clubId) and (ClubJoinRequest.status eq "pending") }
            .map { row ->
                ClubJoinResponse(
                    clubId = row[ClubJoinRequest.clubId].toString(),
                    userId = row[ClubJoinRequest.userId],
                    status = row[ClubJoinRequest.status],
                    requestedOn = row[ClubJoinRequest.requestedOn].toString()
                )
            }
        pendingMembers.ifEmpty { null }
    }

    override suspend fun approveMember(clubId: UUID, userId: String, role: String): Boolean = newSuspendedTransaction(db = database) {
        val updated = ClubMembers.insert {
            it[ClubMembers.clubId] = clubId
            it[ClubMembers.userId] = userId
            it[clubRole] = role
            it[joinedOn] = CurrentDateTime
        }
        if (updated.insertedCount > 0) {
            ClubJoinRequest.deleteWhere { (ClubJoinRequest.clubId eq clubId) and (ClubJoinRequest.userId eq userId) }
            true
        } else {
            false
        }
    }

    override suspend fun rejectMember(clubId: UUID, userId: String): Boolean = newSuspendedTransaction(db=database){
        val rejected = ClubJoinRequest.update({ (ClubJoinRequest.clubId eq clubId) and (ClubJoinRequest.userId eq userId) }) {
            it[status] = "rejected"
        }
        rejected > 0
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