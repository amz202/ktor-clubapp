package com.example.service

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import io.ktor.server.application.*
import java.io.FileInputStream
import java.util.concurrent.atomic.AtomicBoolean

object FirebaseManager {
    private val initialized = AtomicBoolean(false)

    @Synchronized
    fun initialize(application: Application, serviceAccountPath: String) {
        if (initialized.get()) {
            application.log.info("Firebase already initialized, skipping initialization")
            return
        }

        try {
            // First check if any Firebase app exists
            if (FirebaseApp.getApps().isEmpty()) {
                val serviceAccount = FileInputStream(serviceAccountPath)
                val options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build()

                FirebaseApp.initializeApp(options)
                application.log.info("Firebase initialized successfully")
            } else {
                application.log.info("Using existing Firebase app instance")
            }
            initialized.set(true)
        } catch (e: Exception) {
            application.log.error("Failed to initialize Firebase: ${e.localizedMessage}")
            throw e
        }
    }

    fun getApp(): FirebaseApp {
        if (!initialized.get()) {
            throw IllegalStateException("Firebase has not been initialized")
        }
        return FirebaseApp.getInstance()
    }
}