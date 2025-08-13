package com.example.data.datasource

import com.example.data.model.Club
import com.example.data.model.Response.ClubResponse
import com.example.data.model.Event
import com.example.data.model.Response.EventResponse
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


interface ClubDataSource {
    suspend fun getClub(id: UUID): ClubResponse?
    suspend fun getClubs(): List<ClubResponse>
    suspend fun createClub(club: Club): Boolean
    suspend fun deleteClub(id: UUID): Boolean
    suspend fun getClubEvents(clubId: UUID): List<EventResponse>?
    suspend fun getMyClubs(userId: String): List<ClubResponse>? // Renamed function
    suspend fun openClub(id: UUID): Boolean
    suspend fun closeClub(id: UUID): Boolean

}