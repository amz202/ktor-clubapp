package com.example.data.datasource

import com.example.data.model.Club
import com.example.data.model.Response.ClubResponse
import com.example.data.model.Event
import com.example.data.model.Response.EventResponse
import java.util.*

interface ClubDataSource {
    suspend fun getClub(id: UUID): ClubResponse?
    suspend fun getClubs(): List<ClubResponse>
    suspend fun createClub(club: Club): Boolean
    suspend fun deleteClub(id: UUID): Boolean
    suspend fun getClubEvents(clubId: UUID): List<EventResponse>?
    suspend fun getMyClubs(userId: String): List<ClubResponse>? // Renamed function

}