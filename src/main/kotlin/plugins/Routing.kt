package com.example.plugins


import com.example.data.datasource.ClubDataSource
import com.example.data.datasource.EventsDataSource
import com.example.data.datasource.UserDataSource
import com.example.routes.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting(clubDataSource: ClubDataSource, eventsDataSource: EventsDataSource, userDataSource: UserDataSource) {
    routing {
        getClub(clubDataSource)
        getClubs(clubDataSource)
        createClub(clubDataSource)
        deleteClub(clubDataSource)
        getClubEvents(clubDataSource)
        getEvent(eventsDataSource)
        getEvents(eventsDataSource)
        createEvent(eventsDataSource)
        deleteEvent(eventsDataSource)
        login(userDataSource)
    }
}
