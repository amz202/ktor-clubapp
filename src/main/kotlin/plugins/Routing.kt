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
    eventParticipantDataSource: EventParticipantDataSource,
    eventNewsDataSource: EventNewsDataSource,
) {
    routing {
        // Club routes
        getClub(clubDataSource)
        getClubs(clubDataSource)
        createClub(clubDataSource, clubMemberDataSource)
        deleteClub(clubDataSource)
        getClubEvents(clubDataSource)
        getMyClubs(clubDataSource)

        // Club member routes
        getClubsMembers(clubMemberDataSource)
        joinClub(clubMemberDataSource)
        leaveClub(clubMemberDataSource)
        getUsersClubs(clubMemberDataSource)
        changeClubMemberRole(clubMemberDataSource)
        getClubRole(clubMemberDataSource)

        // Event routes
        getEvent(eventsDataSource)
        getEvents(eventsDataSource)
        createEvent(eventsDataSource, eventParticipantDataSource)
        deleteEvent(eventsDataSource)
        getMyEvents(eventsDataSource)

        // Event participant routes
        joinEvent(eventParticipantDataSource, eventsDataSource)
        leaveEvent(eventParticipantDataSource, eventsDataSource)
        getEventParticipants(eventParticipantDataSource)
        getUserEvents(eventParticipantDataSource)
        changeEventRole(eventParticipantDataSource)
        getEventRole(eventParticipantDataSource)

        // User routes
        changeRole(userDataSource)
        login(userDataSource)

        //event news
        getEventNews(eventNewsDataSource, eventsDataSource)
        createEventNews(eventNewsDataSource, eventsDataSource)
        deleteEventNews(eventNewsDataSource, eventsDataSource)
    }
}
