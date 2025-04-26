
import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification
import io.ktor.server.application.*
import java.io.FileInputStream
import java.nio.file.Paths
import java.util.*

class FCMService(private val application: Application) {
    private var initialized = false

    init {
        try {
            // Get the path to service account credentials from application config
            val serviceAccountPath = "C:\\Users\\amz20\\firebase_key\\club\\clubapp-f255a-firebase-adminsdk-fbsvc-fc41eebff2.json"
            val serviceAccount = FileInputStream(serviceAccountPath)

            // Configure with credentials and initialize Firebase
            val options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build()

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options)
            }

            initialized = true
            application.log.info("Firebase Cloud Messaging initialized successfully")
        } catch (e: Exception) {
            application.log.error("Failed to initialize Firebase: ${e.localizedMessage}")
        }
    }

    /**
     * Send a notification to a specific topic (e.g., event_123)
     */
    suspend fun sendNotificationToEventTopic(
        eventId: String,
        title: String,
        body: String,
        data: Map<String, String> = emptyMap()
    ): Boolean {
        if (!initialized) {
            application.log.error("Cannot send notification: Firebase not initialized")
            return false
        }

        try {
            // Build notification payload
            val notificationBuilder = Message.builder()
                .setTopic("event_$eventId")
                .setNotification(
                    Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build()
                )

            if (data.isNotEmpty()) {
                notificationBuilder.putAllData(data)
            }

            // Send the message
            val message = notificationBuilder.build()
            val response = FirebaseMessaging.getInstance().send(message)

            application.log.info("Successfully sent message to topic event_$eventId: $response")
            return true
        } catch (e: Exception) {
            application.log.error("Error sending FCM notification: ${e.message}")
            return false
        }
    }
}