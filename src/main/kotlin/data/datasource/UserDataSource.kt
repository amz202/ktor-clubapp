package com.example.data.datasource

import com.example.data.model.User
import java.util.*

interface UserDataSource {
    suspend fun getUser(id: String): User?
    suspend fun createUser(user: User): Boolean
    suspend fun changeRole(id: String, role: String): Boolean
}