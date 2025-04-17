package com.example.data.datasource

import com.example.data.model.EventParticipant
import com.example.data.model.Response.EventParticipantsResponse
import java.util.*

interface EventParticipantDataSource {
    suspend fun getEventParticipants(eventId: UUID): List<EventParticipantsResponse>
    suspend fun getUserEvents(userId: String): List<EventParticipant>
    suspend fun joinEvent(eventId: UUID, userId: String, role: String): Boolean
    suspend fun leaveEvent(eventId: UUID, userId: String): Boolean
    suspend fun changeEventRole(eventId: UUID, userId: String, role: String): Boolean
    suspend fun getEventRole(eventId: UUID, userId: String): String?
}