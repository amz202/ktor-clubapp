package com.example.websocket

import com.example.data.model.ChatMessage
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.concurrent.ConcurrentHashMap

object ChatSessionManager {
    private val groupConnections = ConcurrentHashMap<String, ConcurrentHashMap.KeySetView<DefaultWebSocketServerSession, Boolean>>()

    fun register(groupId: String, session: DefaultWebSocketServerSession) {
        groupConnections.computeIfAbsent(groupId) { ConcurrentHashMap.newKeySet() }.add(session)
    }

    fun unregister(groupId: String, session: DefaultWebSocketServerSession) {
        groupConnections[groupId]?.let { sessions ->
            sessions.remove(session)
            if (sessions.isEmpty()) {
                groupConnections.remove(groupId)
            }
        }
    }

    suspend fun broadcast(groupId: String, message: ChatMessage) {
        val json = Json.encodeToString(message)
        groupConnections[groupId]?.forEach { session ->
            try {
                session.send(Frame.Text(json))
            } catch (e: Exception) {
                e.printStackTrace()
                unregister(groupId, session)
            }
        }
    }
}
