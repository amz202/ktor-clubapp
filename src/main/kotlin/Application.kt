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
import com.example.plugins.configureMonitoring
import com.example.plugins.configureRouting
import com.example.plugins.configureSecurity
import com.example.plugins.configureSerialization
// import com.example.service.FirebaseInitializer - We won't use this anymore
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    val serviceAccountPath = "C:\\Users\\amz20\\firebase_key\\club\\clubapp-f255a-firebase-adminsdk-fbsvc-fc41eebff2.json"

    // Don't initialize Firebase here anymore - let the auth provider do it first
    // We'll configure security first, which will allow the auth provider to initialize Firebase

    val dataSource = DatabaseFactory.init()
    val database = Database.connect(dataSource)

    val userDataSource = AzureUserDataSource(database)

    // First set up security which will initialize Firebase through the auth provider
    configureSecurity(userDataSource, serviceAccountPath)

    // Then create FCMService which will use the existing instance
    val fcmService = FCMService(this)

    val clubDataSource = AzureClubDataSource(database)
    val eventsDataSource = AzureEventDataSource(database)
    val clubMemberDataSource = AzureClubMemberDataSource(database)
    val eventParticipantDataSource = AzureEventParticipantDataSource(database)
    val eventNewsDataSource = AzureEventNewsDataSource(database, fcmService)

    configureMonitoring()
    configureSerialization()
    createClubsTable()
    configureRouting(
        clubDataSource = clubDataSource,
        eventsDataSource = eventsDataSource,
        userDataSource = userDataSource,
        clubMemberDataSource = clubMemberDataSource,
        eventParticipantDataSource = eventParticipantDataSource,
        eventNewsDataSource = eventNewsDataSource
    )
}

// Exposed is a library that helps interact with databases in a simpler way using Kotlin code instead of writing raw SQL.
// It provides an easy way to define tables and run queries without dealing with complex database handling.

// HikariCP is a tool that manages database connections efficiently, so the app doesn’t open and close connections repeatedly.
// It improves performance by keeping a pool of ready-to-use connections instead of creating a new one every time.