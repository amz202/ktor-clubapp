# ktor-clubapp

Here are some useful links to get you started:

- [Ktor Documentation](https://ktor.io/docs/home.html)
- [Ktor GitHub page](https://github.com/ktorio/ktor)
- The [Ktor Slack chat](https://app.slack.com/client/T09229ZC6/C0A974TJ9). You'll need
  to [request an invite](https://surveys.jetbrains.com/s3/kotlin-slack-sign-up) to join.

# ClubApp Backend

A modern backend server built with Ktor to power the ClubApp mobile application. This service provides a robust and scalable API for managing clubs, events, user sessions, and memberships.

---

## Overview

The ClubApp Backend serves as the core API layer of the ClubApp ecosystem, handling:

- Club creation, discovery, and membership management  
- Event coordination and participation tracking  
- Firebase Authentication token verification  
- Push notifications via Firebase Cloud Messaging  
- Role-based access control and permission enforcement  

Designed with clean architecture principles, the backend ensures clear separation of concerns, scalability, and ease of maintenance. It connects to a PostgreSQL database and uses Exposed for type-safe database access.

---

## Technology Stack

- Kotlin with Ktor  
- PostgreSQL using Exposed ORM  
- Firebase Authentication for session validation  
- Firebase Cloud Messaging for notifications  
- HikariCP for connection pooling  
- kotlinx.serialization for JSON parsing  

---

Let me know if you’d like to add usage instructions, API route documentation, or a database schema overview.


| Name                                                                      | Description                                                                        |
|---------------------------------------------------------------------------|------------------------------------------------------------------------------------|
| [Routing](https://start.ktor.io/p/routing)                                | Provides a structured routing DSL                                                  |
| [Call Logging](https://start.ktor.io/p/call-logging)                      | Logs client requests                                                               |
| [Content Negotiation](https://start.ktor.io/p/content-negotiation)        | Provides automatic content conversion according to Content-Type and Accept headers |
| [kotlinx.serialization](https://start.ktor.io/p/kotlinx-serialization)    | Handles JSON serialization using kotlinx.serialization library                     |
| [Exposed](https://start.ktor.io/p/exposed)                                | Adds Exposed database to your application                                          |
| [Postgres](https://start.ktor.io/p/postgres)                              | Adds Postgres database to your application                                         |
| [Authentication](https://start.ktor.io/p/auth)                            | Provides extension point for handling the Authorization header                     |
| [Firebase authentication](https://start.ktor.io/p/firebase-auth-provider) | Handles Firebase bearer authentication                                             |
| [Static Content](https://start.ktor.io/p/static-content)                  | Serves static files from defined locations                                         |

## Building & Running

To build or run the project, use one of the following tasks:

| Task                          | Description                                                          |
|-------------------------------|----------------------------------------------------------------------|
| `./gradlew test`              | Run the tests                                                        |
| `./gradlew build`             | Build everything                                                     |
| `buildFatJar`                 | Build an executable JAR of the server with all dependencies included |
| `buildImage`                  | Build the docker image to use with the fat JAR                       |
| `publishImageToLocalRegistry` | Publish the docker image locally                                     |
| `run`                         | Run the server                                                       |
| `runDocker`                   | Run using the local docker image                                     |

If the server starts successfully, you'll see the following output:

```
2024-12-04 14:32:45.584 [main] INFO  Application - Application started in 0.303 seconds.
2024-12-04 14:32:45.682 [main] INFO  Application - Responding at http://0.0.0.0:8080
```

