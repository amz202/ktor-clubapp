package com.example.data.datasource

import com.example.data.database.EventNews
import com.example.data.model.Requests.EventNewsRequest
import com.example.data.model.Response.EventNewsResponse

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


interface EventNewsDataSource {
    suspend fun createNews(eventNews: EventNewsRequest): Boolean
    suspend fun getEventNews(eventId: String): List<EventNewsResponse>
    suspend fun getEventNewsById(eventNewsId: String): EventNewsResponse?
    suspend fun deleteEventNews(eventNews: EventNewsRequest): Boolean
    suspend fun sendEventNewsNotification(eventId: String, newsTitle: String, newsContent: String): Boolean
}