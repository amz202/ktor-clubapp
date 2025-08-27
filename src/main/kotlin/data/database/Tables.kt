package com.example.data.database

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.transactions.transaction
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


object Clubs : Table() {
    val id = uuid("id").autoGenerate()
    val name = varchar("name", 50)
    val description = text("description")
    val createdOn = datetime("createdOn")
    val createdBy = varchar("createdBy", 50)
    val tags = varchar("tags", 100)
    val status = varchar("status", 100).default("open")

    override val primaryKey = PrimaryKey(id)
}

object Users : Table(){
    val id = varchar("id", 50)
    val email = varchar("email", 255).uniqueIndex()
    val name = varchar("name", 50)
    val role = varchar("role",10).default("student")
    val schoolName = varchar("schoolName", 255).nullable()
    val photoUrl = varchar("photoUrl", 255).nullable()

    override val primaryKey = PrimaryKey(id)
}

object Events : Table() {
    val id = uuid("id")
    val name = varchar("name", 50)
    val description = text("description")
    val clubId = uuid("clubId").references(Clubs.id, onDelete = ReferenceOption.SET_NULL).nullable()
    val dateTime = varchar("dateTime", 50)
    val location = varchar("location", 100)
    val capacity = integer("capacity").nullable()
    val organizedBy = varchar("organizedBy", 50)
    val tags = varchar("tags", 100)

    override val primaryKey = PrimaryKey(id)
}

object ClubMembers : Table() {
    val userId = varchar("userId", 50).references(Users.id)
    val clubId = uuid("clubId").references(Clubs.id, onDelete = ReferenceOption.CASCADE)
    val clubRole = varchar("clubRole", 50).default("member")
    val joinedOn = datetime("joinedOn")

    override val primaryKey = PrimaryKey(userId, clubId)
}

object EventParticipants : Table() {
    val userId = varchar("userId", 50).references(Users.id)
    val eventId = uuid("eventId").references(Events.id, onDelete = ReferenceOption.CASCADE)
    val eventRole = varchar("eventRole", 30)
    val joinedOn = datetime("joinedOn")

    override val primaryKey = PrimaryKey(userId, eventId)
}

object EventDetails : Table() {
    val eventId = uuid("eventId").references(Events.id, onDelete = ReferenceOption.CASCADE).uniqueIndex()
    val reviewSummary = varchar("reviewSummary", 150).nullable()
    val mediaUrls = varchar("mediaUrls", 150).nullable()
    val sponsors = varchar("sponsors", 50).nullable()

    override val primaryKey = PrimaryKey(eventId)
}

object EventNews : Table() {
    val eventId = uuid("eventId").references(Events.id, onDelete = ReferenceOption.CASCADE)
    val news = varchar("news", 255)
    val createdOn = datetime("createdOn")
    val id = uuid("id").autoGenerate()

    override val primaryKey = PrimaryKey(eventId)
}

object ClubJoinRequest: Table() {
    val userId = varchar("userId", 50).references(Users.id)
    val clubId = uuid("clubId").references(Clubs.id, onDelete = ReferenceOption.CASCADE)
    val status = varchar("status", 20).default("pending")
    val requestedOn = datetime("requestedOn")

    override val primaryKey = PrimaryKey(userId, clubId)
}

fun createClubsTable() {
    transaction {
        SchemaUtils.create(Clubs, Events, Users, ClubMembers, EventDetails, EventParticipants, EventNews,
            ClubJoinRequest)
    }
}