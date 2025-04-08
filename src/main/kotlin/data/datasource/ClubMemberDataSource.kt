package com.example.data.datasource

import com.example.data.model.ClubMember
import java.util.*

interface ClubMemberDataSource {
    suspend fun getClubsMembers(clubId: UUID): List<ClubMember>
    suspend fun getUsersClubs(userId: String): List<ClubMember>
    suspend fun joinClub(clubId: UUID, userId: String, role:String): Boolean
    suspend fun leaveClub(clubId: UUID, userId: String): Boolean
    suspend fun changeRole(clubId: UUID, userId: String, role: String): Boolean
}