package com.example.data.datasource

import com.example.data.model.ClubGroup

interface GroupDataSource {
    suspend fun createGroup(group: ClubGroup): Boolean
    suspend fun getGroups(): List<ClubGroup>
    suspend fun getGroupById(id: String): ClubGroup?
    suspend fun deleteGroup(clubId: String): Boolean
}