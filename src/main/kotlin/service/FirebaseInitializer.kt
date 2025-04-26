package com.example.service

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import io.ktor.server.application.*
import java.io.File
import java.io.FileInputStream
import java.util.concurrent.atomic.AtomicBoolean

object FirebaseInitializer {
    @Volatile
    private var initialized = false

    fun initialize(application: Application, serviceAccountPath: String) {
        if (initialized) {
            application.log.info("Firebase already initialized")
            return
        }

        synchronized(this) {
            if (!initialized && FirebaseApp.getApps().isEmpty()) {
                try {
                    val serviceAccount = FileInputStream(File(serviceAccountPath))
                    val options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build()

                    FirebaseApp.initializeApp(options)
                    initialized = true
                    application.log.info("Firebase successfully initialized")
                } catch (e: Exception) {
                    application.log.error("Error initializing Firebase: ${e.message}")
                    throw e
                }
            } else if (!initialized) {
                application.log.info("Firebase was already initialized elsewhere")
                initialized = true
            }
        }
    }
}