package com.example.data.datasource

import com.example.data.database.Users
import com.example.data.model.User
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class AzureUserDataSource(private val database: Database) : UserDataSource {

    override suspend fun getUser(id: String): User? = newSuspendedTransaction(db = database) {
        Users.selectAll().where { Users.id eq id }
            .map { User(it[Users.id], it[Users.email], it[Users.name]) }
            .singleOrNull()
    }

    override suspend fun createUser(user: User): Boolean = newSuspendedTransaction(db = database) {
        val exists = Users.selectAll().where { Users.id eq user.id }.count() > 0
        if (!exists) {
            Users.insert {
                it[id] = user.id
                it[email] = user.email
                it[name] = user.name
            }
        }
        !exists
    }
}