package com.example.data.datasource

import com.example.data.model.Club
import com.example.data.model.Event
import java.util.*

interface ClubDataSource {
    suspend fun getClub(id: UUID): Club?
    suspend fun getClubs(): List<Club>
    suspend fun createClub(club: Club, creator:String): Boolean
    suspend fun deleteClub(id: UUID): Boolean
    suspend fun getClubEvents(clubId: UUID): List<Event>?
}