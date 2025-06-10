package com.example.data.datasource

import com.example.data.model.EventParticipant
import com.example.data.model.Response.EventParticipantsResponse
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


interface EventParticipantDataSource {
    suspend fun getEventParticipants(eventId: UUID): List<EventParticipantsResponse>
    suspend fun getUserEvents(userId: String): List<EventParticipant>
    suspend fun joinEvent(eventId: UUID, userId: String, role: String): Boolean
    suspend fun leaveEvent(eventId: UUID, userId: String): Boolean
    suspend fun changeEventRole(eventId: UUID, userId: String, role: String): Boolean
    suspend fun getEventRole(eventId: UUID, userId: String): RoleResponse?
}