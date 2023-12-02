# Auth Service

Auth Service is a modern, secure authentication and authorization server. It provides robust user authentication and authorization functionality, leveraging OAuth2 standards and JWT for secure token management. Built using Spring Boot and Kotlin, it is designed for easy integration with microservices and web applications.

## Authors
- Arshak Nazaryan
- Hayk Khachatryan

## High-Level Description of Used Frameworks and Libraries
- **Spring Boot (v3.1.5)**: For rapid application development, focusing on web services.
- **Kotlin (v1.8.22)**: As the primary programming language with Kotlin-specific Spring and JPA plugins.
- **Spring Boot Starters**: Including Actuator, Data JPA, Web, Validation, Security, and OAuth2 Authorization Server.
- **Java Version**: Compatibility with Java 17.

## How to Run
### Running with Docker
- Navigate to the project root directory.
- Run `docker-compose up` to build and start both the application and the database services.
- The application will be accessible on `http://localhost:8081`.

### Running DB with Docker and Service with IDEA
1. **Starting the Database Service**:
    - Run `docker-compose up auth_db_service` to start only the PostgreSQL database service.
    - The database will be accessible on `localhost:5433`.

2. **Configuring and Running the Application Locally**:
    - Ensure the application properties are configured to connect to `localhost:5433` for the database.
    - Run the application through your IDE or command line (using Gradle or Kotlin commands).
