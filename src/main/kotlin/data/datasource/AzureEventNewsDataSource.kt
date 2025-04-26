package com.example.data.datasource

import com.example.data.database.EventNews
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

class AzureEventNewsDataSource(private val database: Database): EventNewsDataSource {
    override suspend fun createNews(eventNews: EventNewsRequest): Boolean = newSuspendedTransaction(db = database) {
        val result = EventNews.insert {
            it[news] = eventNews.news
            it[createdOn] = CurrentDateTime
            it[eventId] = UUID.fromString(eventNews.eventId)
            it[id] = UUID.randomUUID()
        }
        result.insertedCount > 0
    }

    override suspend fun getEventNews(eventId: String): List<EventNewsResponse> = newSuspendedTransaction(db = database) {
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

    override suspend fun getEventNewsById(eventNewsId: String): EventNewsResponse? = newSuspendedTransaction(db = database) {
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

    override suspend fun deleteEventNews(eventNews: EventNewsRequest): Boolean = newSuspendedTransaction(db = database) {
        val deletedCount = EventNews.deleteWhere {
            (EventNews.eventId eq UUID.fromString(eventNews.eventId)) and
                    (EventNews.news eq eventNews.news)
        }
        deletedCount > 0
    }
}

