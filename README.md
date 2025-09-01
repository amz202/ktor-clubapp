# ClubApp Backend

A modern backend server built with Ktor to power the [ClubApp](https://github.com/amz202/ClubApp) mobile app. This service provides a robust and scalable API for managing clubs, events, user sessions, live group chat, and memberships.

---

## Overview

The ClubApp Backend serves as the core API layer of the ClubApp ecosystem, handling:

- Club creation, discovery, and membership management  
- Event coordination and participation tracking  
- Firebase Authentication token verification  
- Real-time group chat with WebSocket support  
- Role-based access control and permission enforcement  
- Push notifications via Firebase Cloud Messaging  

Designed with clean architecture principles, the backend ensures clear separation of concerns, scalability, and ease of maintenance. It connects to both the **Azure Database for PostgreSQL** flexible server (for structured data) and **MongoDB Atlas** (for unstructured chat data).

---

## Technology Stack

- **Kotlin** with **Ktor**  
- **Azure PostgreSQL** using **Exposed ORM** (temporarily shifted to **Supabase**) 
- **MongoDB Atlas** for chat groups and messages  
- **Firebase Authentication** for session validation  
- **Firebase Cloud Messaging** for push notifications  
- **WebSockets** for real-time communication  
- **HikariCP** for connection pooling  
- **kotlinx.serialization** for JSON parsing  

---

## Modules

| Module                | Description                                                                 |
|-----------------------|-----------------------------------------------------------------------------|
| Clubs & Memberships   | Club creation, open/close, join/leave functionality                         |
| Events                | Club-specific event creation and participation tracking                     |
| Authentication        | Firebase ID token verification, user session linking                        |
| Notifications         | Firebase Cloud Messaging integration for real-time push alerts              |
| WebSocket Chat        | Real-time group chat system scoped to each club                             |
| MongoDB Integration   | Stores `groups` and `messages` collections for chat                         |

---

## Version 1.2

> **ClubApp Backend v1.2** refines membership management, improves role lookups, and introduces a temporary migration to Supabase for structured data.

### Highlights

- **Membership control** – Join requests now flow through a structured approval system, with creators and moderators managing access  
- **Role service** – Added a dedicated service to fetch a user’s role in a club or event, simplifying admin-specific checks  
- **Database migration** – Switched PostgreSQL from Azure to Supabase (temporary), improving backend stability and reducing connection issues  
- **Event–club link** – Improved handling of parent club references for events, keeping data consistent with the app  

---

## Ktor Plugins Used

| Plugin                                                                     | Purpose                                                                                 |
|----------------------------------------------------------------------------|-----------------------------------------------------------------------------------------|
| [Routing](https://start.ktor.io/p/routing)                                | Structured routing DSL                                                                 |
| [Call Logging](https://start.ktor.io/p/call-logging)                      | Logs client requests                                                                   |
| [Content Negotiation](https://start.ktor.io/p/content-negotiation)        | JSON serialization via `kotlinx.serialization`                                         |
| [Authentication](https://start.ktor.io/p/auth)                            | JWT and Firebase-based authentication                                                  |
| [Exposed](https://start.ktor.io/p/exposed)                                | Type-safe SQL DSL for Azure PostgreSQL                                                 |
| [Postgres](https://start.ktor.io/p/postgres)                              | PostgreSQL driver integration                                                          |
| [Firebase Auth Provider](https://start.ktor.io/p/firebase-auth-provider)  | Handles Firebase bearer token auth                                                     |
| [WebSockets](https://ktor.io/docs/websockets.html)                        | Enables persistent bi-directional communication for real-time chat                     |
| [Static Content](https://start.ktor.io/p/static-content)                  | Serves assets like `.well-known`, documentation, etc.                                  |

---

## License

This project is licensed under the [Apache License 2.0](./LICENSE).

---




