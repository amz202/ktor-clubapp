package com.example.data.datasource.mongo

import com.example.data.datasource.GroupDataSource
import com.example.data.model.ClubGroup
import com.example.data.model.Response.ClubGroupResponse
import com.mongodb.client.model.Filters.eq
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList

class MongoGroupDataSource(db: MongoDatabase) : GroupDataSource {
    private val groups = db.getCollection<ClubGroup>("groups")

    override suspend fun createGroup(group: ClubGroup): Boolean {
        return groups.insertOne(group).wasAcknowledged()
    }

    override suspend fun getGroups(): List<ClubGroup> {
        return groups.find().toList()
    }

    override suspend fun getGroupById(id: String): ClubGroupResponse? {
        val group = groups.find(eq("clubId", id)).firstOrNull()
        return group?.let {
            ClubGroupResponse(
                id = it.id.toString(),
                name = it.name,
                clubId = it.clubId
            )
        }
    }

    override suspend fun deleteGroup(id: String): Boolean {
        val deleteResult = groups.deleteOne(eq("clubId",id))
        return deleteResult.deletedCount > 0
    }
}