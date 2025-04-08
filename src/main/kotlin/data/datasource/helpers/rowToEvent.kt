package com.example.data.datasource.helpers

import com.example.data.database.Events
import com.example.data.model.Event
import org.jetbrains.exposed.sql.ResultRow

fun rowToEvent(row: ResultRow) = Event( //converts the row response from database to Event object
    id = row[Events.id],
    clubId = row[Events.clubId].toString(),
    name = row[Events.name],
    description = row[Events.description],
    dateTime = row[Events.dateTime],
    location = row[Events.location],
    capacity = row[Events.capacity].toString(),
    organizedBy = row[Events.organizedBy],
)