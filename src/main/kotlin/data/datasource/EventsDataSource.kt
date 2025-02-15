package com.example.data.datasource

import com.example.data.model.Event
import java.util.*

interface EventsDataSource {
    suspend fun getEvents(): List<Event>
    suspend fun getEvent(id: UUID): Event?
    suspend fun createEvent(event: Event): Boolean
    suspend fun deleteEvent(id: UUID): Boolean
}