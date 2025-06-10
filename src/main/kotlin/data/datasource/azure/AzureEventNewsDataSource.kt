package com.example.data.datasource.azure

import FCMService
import com.example.data.database.EventNews
import com.example.data.database.Events
import com.example.data.datasource.EventNewsDataSource
import com.example.data.model.Requests.EventNewsRequest
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import com.example.data.model.Response.EventNewsResponse
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import java.util.UUID

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


class AzureEventNewsDataSource(
    private val database: Database, private val fcmService: FCMService
) : EventNewsDataSource {
    override suspend fun createNews(eventNews: EventNewsRequest): Boolean = newSuspendedTransaction(db = database) {
        val result = EventNews.insert {
            it[news] = eventNews.news
            it[createdOn] = CurrentDateTime
            it[eventId] = UUID.fromString(eventNews.eventId)
            it[id] = UUID.randomUUID()
        }
        if (result.insertedCount > 0) {
            val eventName = Events
                .selectAll()
                .where { Events.id eq UUID.fromString(eventNews.eventId) }
                .map { it[Events.name] }
                .singleOrNull() ?: "Event"

            val newsTitle = "$eventName"
            sendEventNewsNotification(eventNews.eventId, newsTitle, eventNews.news)
            true
        } else {
            false
        }    }

    override suspend fun getEventNews(eventId: String): List<EventNewsResponse> =
        newSuspendedTransaction(db = database) {
            EventNews.selectAll()
                .where { EventNews.eventId eq UUID.fromString(eventId) }
                .map { row ->
                    EventNewsResponse(
                        id = row[EventNews.id].toString(),
                        news = row[EventNews.news],
                        createdOn = row[EventNews.createdOn].toString(),
                        eventId = row[EventNews.eventId].toString()
                    )
                }
        }

    override suspend fun getEventNewsById(eventNewsId: String): EventNewsResponse? =
        newSuspendedTransaction(db = database) {
            EventNews.selectAll()
                .where { EventNews.id eq UUID.fromString(eventNewsId) }
                .map { row ->
                    EventNewsResponse(
                        id = row[EventNews.id].toString(),
                        news = row[EventNews.news],
                        createdOn = row[EventNews.createdOn].toString(),
                        eventId = row[EventNews.eventId].toString()
                    )
                }
                .singleOrNull()
        }

    override suspend fun deleteEventNews(eventNews: EventNewsRequest): Boolean =
        newSuspendedTransaction(db = database) {
            val deletedCount = EventNews.deleteWhere {
                (EventNews.eventId eq UUID.fromString(eventNews.eventId)) and
                        (EventNews.news eq eventNews.news)
            }
            deletedCount > 0
        }

    override suspend fun sendEventNewsNotification(
        eventId: String,
        newsTitle: String,
        newsContent: String
    ): Boolean {
        // Include some additional data in the notification
        val data = mapOf(
            "eventId" to eventId,
            "timestamp" to System.currentTimeMillis().toString(),
            "type" to "event_announcement"
        )

        // Send to the topic for this event
        return fcmService.sendNotificationToEventTopic(
            eventId = eventId,
            title = newsTitle,
            body = newsContent,
            data = data
        )
    }
}

