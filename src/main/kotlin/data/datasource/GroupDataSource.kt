package com.example.data.datasource

import com.example.data.model.ClubGroup
import com.example.data.model.Response.ClubGroupResponse

interface GroupDataSource {
    suspend fun createGroup(group: ClubGroup): Boolean
    suspend fun getGroups(): List<ClubGroup>
    suspend fun getGroupById(id: String): ClubGroupResponse?
    suspend fun deleteGroup(clubId: String): Boolean
}