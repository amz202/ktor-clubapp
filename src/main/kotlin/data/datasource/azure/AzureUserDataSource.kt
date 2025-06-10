package com.example.data.datasource.azure

import com.example.data.database.Users
import com.example.data.datasource.UserDataSource
import com.example.data.model.User
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

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


class AzureUserDataSource(private val database: Database) : UserDataSource {

    override suspend fun getUser(id: String): User? = newSuspendedTransaction(db = database) {
        Users.selectAll().where { Users.id eq id }
            .map { User(it[Users.id], it[Users.email], it[Users.name], it[Users.role], it[Users.photoUrl], it[Users.schoolName]) }
            .singleOrNull()
    }

    override suspend fun createUser(user: User): Boolean = newSuspendedTransaction(db = database) {
        val exists = Users.selectAll().where { Users.id eq user.id }.count() > 0
        if (!exists) {
            Users.insert {
                it[id] = user.id
                it[email] = user.email
                it[name] = user.name
                it[role] = user.role
                it[schoolName] = user.schoolName
                it[photoUrl] = user.photoUrl
            }
        }
        !exists
    }

    override suspend fun changeRole(id: String, role: String): Boolean = newSuspendedTransaction(db = database)  {
        val exists = getUser(id) != null
        if (exists) {
            Users.update({ Users.id eq id }){
                it[Users.role] = role
            }
        }
        exists
    }
}