package com.example.plugins


import com.example.data.datasource.*
import com.example.routes.*
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting(
    clubDataSource: ClubDataSource,
    eventsDataSource: EventsDataSource,
    userDataSource: UserDataSource,
    clubMemberDataSource: ClubMemberDataSource,
    eventParticipantDataSource: EventParticipantDataSource
) {
    routing {
        // Club routes
        getClub(clubDataSource)
        getClubs(clubDataSource)
        createClub(clubDataSource, clubMemberDataSource)
        deleteClub(clubDataSource)
        getClubEvents(clubDataSource)

        // Club member routes
        getClubsMembers(clubMemberDataSource)
        joinClub(clubMemberDataSource)
        leaveClub(clubMemberDataSource)
        getUsersClubs(clubMemberDataSource)
        changeClubMemberRole(clubMemberDataSource)

        // Event routes
        getEvent(eventsDataSource)
        getEvents(eventsDataSource)
        createEvent(eventsDataSource)
        deleteEvent(eventsDataSource)

        // Event participant routes
        joinEvent(eventParticipantDataSource)
        leaveEvent(eventParticipantDataSource, eventsDataSource)
        getEventParticipants(eventParticipantDataSource)
        getUserEvents(eventParticipantDataSource)
        changeEventRole(eventParticipantDataSource)

        // User routes
        changeRole(userDataSource)
        login(userDataSource)
    }
}
