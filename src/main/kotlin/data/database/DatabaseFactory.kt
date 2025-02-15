package com.example.data.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database

object DatabaseFactory {
    fun init(): HikariDataSource {
        val config = HikariConfig().apply {
            jdbcUrl = System.getenv("JDBC_DATABASE_URL") ?: "jdbc:postgresql://amz-1.postgres.database.azure.com:5432/clubs_db?sslmode=require"
            driverClassName = "org.postgresql.Driver"
            username = System.getenv("DB_USER") ?: "amz18"
            password = System.getenv("DB_PASSWORD") ?: "Hammertime202"
            maximumPoolSize = 10
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        }
        return HikariDataSource(config)

    }
}
