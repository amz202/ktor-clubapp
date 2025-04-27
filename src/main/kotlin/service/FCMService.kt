import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification
import io.ktor.server.application.*
import java.util.*

class FCMService(private val application: Application) {
    // Remove the initialized flag, we'll check each time

    init {
        // Just log that the service is created
        application.log.info("FCM Service created, will use Firebase when available")
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
        // Check if Firebase is initialized right now, not relying on an instance variable
        if (FirebaseApp.getApps().isEmpty()) {
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