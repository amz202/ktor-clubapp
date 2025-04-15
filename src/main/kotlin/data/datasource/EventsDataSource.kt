package com.example.data.datasource

import com.example.data.model.Event
import com.example.data.model.Response.EventResponse
import java.util.*

interface EventsDataSource {
    suspend fun getEvents(): List<EventResponse>
    suspend fun getEvent(id: UUID): EventResponse?
    suspend fun createEvent(event: Event): Boolean
    suspend fun deleteEvent(id: UUID): Boolean
}