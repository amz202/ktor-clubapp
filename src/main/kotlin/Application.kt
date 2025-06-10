package com.example

import FCMService
import com.example.data.database.DatabaseFactory
import com.example.data.database.createClubsTable
import com.example.data.datasource.azure.AzureClubMemberDataSource
import com.example.data.datasource.azure.AzureClubDataSource
import com.example.data.datasource.azure.AzureEventDataSource
import com.example.data.datasource.azure.AzureEventNewsDataSource
import com.example.data.datasource.azure.AzureEventParticipantDataSource
import com.example.data.datasource.azure.AzureUserDataSource
import com.example.data.datasource.mongo.MongoChatDataSource
import com.example.data.datasource.mongo.MongoGroupDataSource
import com.example.plugins.configureMonitoring
import com.example.plugins.configureRouting
import com.example.plugins.configureSecurity
import com.example.plugins.configureSerialization
import com.example.plugins.configureSockets
import com.mongodb.kotlin.client.coroutine.MongoClient
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database

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


fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    //Azure
    val serviceAccountPath = "C:\\Users\\amz20\\firebase_key\\club\\clubapp-f255a-firebase-adminsdk-fbsvc-fc41eebff2.json"
    val dataSource = DatabaseFactory.init()
    val database = Database.connect(dataSource)
    val userDataSource = AzureUserDataSource(database)

    //Mongo
    val mongoPw = System.getenv("MONGO_PW")
    val dbName = "ChatDatabase"
    val db = MongoClient.create(
        connectionString = "mongodb+srv://abdulmajidzeeshan4:${mongoPw}@cluster0.dgrppcp.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0"
    ).getDatabase(dbName)

    // First set up security which will initialize Firebase through the auth provider
    configureSecurity(userDataSource, serviceAccountPath)

    // Then create FCMService which will use the existing instance
    val fcmService = FCMService(this)

    val clubDataSource = AzureClubDataSource(database)
    val eventsDataSource = AzureEventDataSource(database)
    val clubMemberDataSource = AzureClubMemberDataSource(database)
    val eventParticipantDataSource = AzureEventParticipantDataSource(database)
    val eventNewsDataSource = AzureEventNewsDataSource(database, fcmService)
    val chatDataSource = MongoChatDataSource(db)
    val groupDataSource = MongoGroupDataSource(db)

    configureMonitoring()
    configureSerialization()
    createClubsTable()
    configureRouting(
        clubDataSource = clubDataSource,
        eventsDataSource = eventsDataSource,
        userDataSource = userDataSource,
        clubMemberDataSource = clubMemberDataSource,
        eventParticipantDataSource = eventParticipantDataSource,
        eventNewsDataSource = eventNewsDataSource,
        chatDataSource = chatDataSource,
        groupDataSource = groupDataSource
    )
    configureSockets(chatDataSource, clubMemberDataSource)
}

// Exposed is a library that helps interact with databases in a simpler way using Kotlin code instead of writing raw SQL.
// It provides an easy way to define tables and run queries without dealing with complex database handling.

// HikariCP is a tool that manages database connections efficiently, so the app doesn’t open and close connections repeatedly.
// It improves performance by keeping a pool of ready-to-use connections instead of creating a new one every time.