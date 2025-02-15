package com.example.data.database

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

import java.util.UUID

object Clubs : Table() {
    val id = uuid("id").autoGenerate()
    val name = varchar("name", 50)
    val description = text("description")

    override val primaryKey = PrimaryKey(id)
}

object Users : Table(){
    val id = varchar("id", 50)
    val email = varchar("email", 255).uniqueIndex()
    val name = varchar("name", 50)

    override val primaryKey = PrimaryKey(id)
}

object Events:Table(){
    val id = uuid("id").autoGenerate()
    val name = varchar("name", 50)
    val description = text("description")
    val clubId = uuid("clubId")

    override val primaryKey = PrimaryKey(id)
}

fun createClubsTable() {
    transaction {
        SchemaUtils.create(Clubs, Events, Users)
    }
}