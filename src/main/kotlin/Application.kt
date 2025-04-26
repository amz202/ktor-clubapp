package com.example

import FCMService
import com.example.data.database.DatabaseFactory
import com.example.data.database.createClubsTable
import com.example.data.datasource.*
import com.example.plugins.configureMonitoring
import com.example.plugins.configureRouting
import com.example.plugins.configureSecurity
//import com.example.plugins.configureSecurity
import com.example.plugins.configureSerialization
import com.example.service.FirebaseInitializer
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    // Initialize Firebase first thing
    val serviceAccountPath = "C:\\Users\\amz20\\firebase_key\\club\\clubapp-f255a-firebase-adminsdk-fbsvc-fc41eebff2.json"
    FirebaseInitializer.initialize(this, serviceAccountPath)

    val dataSource = DatabaseFactory.init()
    val database = Database.connect(dataSource)

    // FCMService will now use the existing Firebase instance
    val fcmService = FCMService(this)

    val clubDataSource = AzureDataSource(database)
    val eventsDataSource = AzureEventDataSource(database)
    val userDataSource = AzureUserDataSource(database)
    val clubMemberDataSource = AzureClubMemberDataSource(database)
    val eventParticipantDataSource = AzureEventParticipantDataSource(database)
    val eventNewsDataSource = AzureEventNewsDataSource(database, fcmService)

    configureMonitoring()
    configureSerialization()
    createClubsTable()
    configureSecurity(userDataSource, serviceAccountPath)
    configureRouting(
        clubDataSource = clubDataSource,
        eventsDataSource = eventsDataSource,
        userDataSource = userDataSource,
        clubMemberDataSource = clubMemberDataSource,
        eventParticipantDataSource = eventParticipantDataSource,
    )
}

// Exposed is a library that helps interact with databases in a simpler way using Kotlin code instead of writing raw SQL.
// It provides an easy way to define tables and run queries without dealing with complex database handling.

// HikariCP is a tool that manages database connections efficiently, so the app doesn’t open and close connections repeatedly.
// It improves performance by keeping a pool of ready-to-use connections instead of creating a new one every time.