package com.example.data.datasource

import com.example.data.database.EventNews
import com.example.data.model.Requests.EventNewsRequest
import com.example.data.model.Response.EventNewsResponse

interface EventNewsDataSource {
    suspend fun createNews(eventNews: EventNewsRequest): Boolean
    suspend fun getEventNews(eventId: String): List<EventNewsResponse>
    suspend fun getEventNewsById(eventNewsId: String): EventNewsResponse?
    suspend fun deleteEventNews(eventNews: EventNewsRequest): Boolean
    suspend fun sendEventNewsNotification(eventId: String, newsTitle: String, newsContent: String): Boolean
}