package com.example.data.database

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.transactions.transaction

import java.util.UUID

object Clubs : Table() {
    val id = uuid("id").autoGenerate()
    val name = varchar("name", 50)
    val description = text("description")
    val createdOn = datetime("createdOn")
    val createdBy = varchar("createdBy", 50)
    val tags = varchar("tags", 100)

    override val primaryKey = PrimaryKey(id)
}

object Users : Table(){
    val id = varchar("id", 50)
    val email = varchar("email", 255).uniqueIndex()
    val name = varchar("name", 50)
    val role = varchar("role",10).default("student")
    val schoolName = varchar("schoolName", 255).nullable()

    override val primaryKey = PrimaryKey(id)
}

object Events:Table(){
    val id = uuid("id").autoGenerate()
    val name = varchar("name", 50)
    val description = text("description")
    val clubId = uuid("clubId").references(Clubs.id).nullable()
    val dateTime = varchar("dateTime", 50)
    val location = varchar("location", 100)
    val capacity = integer("capacity").nullable()
    val organizedBy = varchar("organizedBy",50)
    val tags = varchar("tags", 100)


    override val primaryKey = PrimaryKey(id)
}

object ClubMembers : Table() {
    val userId = varchar("userId", 50).references(Users.id)
    val clubId = uuid("clubId").references(Clubs.id)
    val clubRole = varchar("clubRole", 50).default("member")
    val joinedOn = datetime("joinedOn")

    override val primaryKey = PrimaryKey(userId, clubId)
}

object EventParticipants  : Table() {
    val userId = varchar("userId", 50).references(Users.id)
    val eventId = uuid("eventId").references(Events.id)
    val eventRole = varchar("eventRole", 30)
    val joinedOn = datetime("joinedOn")

    override val primaryKey = PrimaryKey(userId, eventId)
}

object EventDetails : Table() {
    val eventId = uuid("eventId").references(Events.id).uniqueIndex()
    val reviewSummary = varchar("reviewSummary", 150).nullable()
    val mediaUrls = varchar("mediaUrls", 150).nullable()
    val sponsors = varchar("sponsors", 50).nullable()

    override val primaryKey = PrimaryKey(eventId)
}

object EventNews : Table() {
    val eventId = uuid("eventId").references(Events.id)
    val news = varchar("news", 255)
    val createdOn = datetime("createdOn")
    val id = uuid("id").autoGenerate()

    override val primaryKey = PrimaryKey(eventId)
}

fun createClubsTable() {
    transaction {
        SchemaUtils.create(Clubs, Events, Users, ClubMembers, EventDetails, EventParticipants, EventNews)
    }
}