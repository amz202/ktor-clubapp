package com.example.plugins

import com.example.data.datasource.*
import com.example.routes.*
import io.ktor.server.application.*
import io.ktor.server.routing.*

/*
 * Copyright 2025 Abdul Majid
 *
 * This file is part of the backend components developed for the ClubApp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


fun Application.configureRouting(
    clubDataSource: ClubDataSource,
    eventsDataSource: EventsDataSource,
    userDataSource: UserDataSource,
    clubMemberDataSource: ClubMemberDataSource,
    eventParticipantDataSource: EventParticipantDataSource,
    eventNewsDataSource: EventNewsDataSource,
    chatDataSource: ChatDataSource,
    groupDataSource: GroupDataSource
) {
    routing {
        // Club routes
        getClub(clubDataSource)
        getClubs(clubDataSource)
        createClub(clubDataSource, clubMemberDataSource, groupDataSource)
        deleteClub(clubDataSource, groupDataSource)
        getClubEvents(clubDataSource)
        getMyClubs(clubDataSource)
        getClubGroup(groupDataSource)
        openClub(clubDataSource)
        closeClub(clubDataSource)

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

        //chat
        recentChat(chatDataSource)
        deleteMessage(chatDataSource)
        editMessage(chatDataSource)
    }
}
