package com.example.plugins

import com.example.data.datasource.ChatDataSource
import com.example.data.datasource.ClubMemberDataSource
import com.example.websocket.chatWSRoute
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import kotlin.time.Duration.Companion.seconds

fun Application.configureSockets(chatDataSource: ChatDataSource, clubMemberDataSource: ClubMemberDataSource) {
    install(WebSockets) {
        pingPeriod = 15.seconds
        timeout = 15.seconds
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }
    routing {
        chatWSRoute(chatDataSource, clubMemberDataSource)
    }
}
