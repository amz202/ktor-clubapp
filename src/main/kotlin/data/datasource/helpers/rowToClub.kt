package com.example.data.datasource.helpers

import com.example.data.database.Clubs
import com.example.data.model.Club
import org.jetbrains.exposed.sql.ResultRow

fun rowToClub(row: ResultRow) = Club(
    id = row[Clubs.id],
    name = row[Clubs.name],
    description = row[Clubs.description]
)