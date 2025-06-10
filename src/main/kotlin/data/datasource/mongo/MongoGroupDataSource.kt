package com.example.data.datasource.mongo

import com.example.data.datasource.GroupDataSource
import com.example.data.model.ClubGroup
import com.example.data.model.Response.ClubGroupResponse
import com.mongodb.client.model.Filters.eq
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList

/*
 * Copyright 2025 Abdul Majid
 *
 * This file is part of the backend components developed for the ClubApp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


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