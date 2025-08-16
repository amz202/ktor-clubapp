package com.example.data.datasource

import com.example.data.model.ClubMember
import com.example.data.model.Response.ClubJoinResponse
import com.example.data.model.Response.ClubMembersResponse
import com.example.data.model.Response.RoleResponse
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


interface ClubMemberDataSource {
    suspend fun getClubsMembers(clubId: UUID): List<ClubMembersResponse>
    suspend fun getUsersClubs(userId: String): List<ClubMember>
    suspend fun joinClub(clubId: UUID, userId: String): Boolean //request
    suspend fun leaveClub(clubId: UUID, userId: String): Boolean
    suspend fun changeRole(clubId: UUID, userId: String, role: String): Boolean
    suspend fun getClubRole(clubId: UUID, userId: String): RoleResponse?
    suspend fun getPendingMembers(clubId: UUID): List<ClubJoinResponse>?
    suspend fun approveMember(clubId: UUID, userId: String, role: String): Boolean
    suspend fun rejectMember(clubId: UUID, userId: String): Boolean
}