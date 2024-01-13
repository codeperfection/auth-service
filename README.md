# Auth Service

Auth Service is a modern, secure authentication and authorization server. It provides robust user authentication and authorization functionality, leveraging OAuth2 standards and JWT for secure token management. Built using Spring Boot and Kotlin, it is designed for easy integration with microservices and web applications.

## Authors
- [Arshak Nazaryan](https://github.com/nazaryan)
- [Hayk Khachatryan](https://github.com/haykart)

## High-Level Description of Used Frameworks and Libraries
- **Spring Boot (v3.2.0)**: For rapid application development, focusing on web services.
- **Kotlin (v1.9.20)**: As the primary programming language with Kotlin-specific Spring and JPA plugins.
- **Spring Boot Starters**: Including Actuator, Data JPA, Web, Validation, Security, and OAuth2 Authorization Server.
- **Java Version**: Compatibility with Java 21.

## How RSA keys are generated and encoded
We use pre-generated RSA keys to create, sign, and validate JWT tokens, ensuring the private key is password-protected.
This approach, using a consistent key across all instances of the application, avoids on-the-fly key generation.
These keys are located in the keys directory within the [src/main/resources](src/main/resources) directory

Generation of keys was done with the following commands
```shell
openssl genpkey -algorithm RSA -out jwt_private_key.pem -pkeyopt rsa_keygen_bits:2048
openssl rsa -pubout -in jwt_private_key.pem -out jwt_public_key.pem
openssl pkcs8 -topk8 -inform PEM -outform PEM -in jwt_private_key.pem -out jwt_private_key_encoded.pem -passout pass:<PASSWORD>
```
For details on how RSA keys are read and decoded check `JwkSourceConfig` configuration class.

## Supported authorization grant types
Currently only 3 authorization grant types are supported
- authorization code
- client credentials
- refresh token (cannot be used standalone)

Use the Postman collection and environment in [postman](postman) directory to try out the endpoints.

## How to Run
### Running with Docker
Required Docker version is 24.0.6 or above.
- Navigate to the project root directory.
- Run `docker compose up --build auth-app-service` to build and start both the application and the database services.
- The application will be accessible on `http://localhost:8081`.

### Running only database with Docker
1. **Starting the Database Service**:
    - Run `docker compose up auth-db-service` in a Terminal to start only the PostgreSQL database service.
    - The database will be accessible on `localhost:5433`.

2. **Configuring and Running the Application Locally**:
    - Default application properties are configured to connect to `localhost:5433` for the database.
    - Run the application through your IDE or command line (`./gradlew bootRun`).

## Implementation nuances 
### Issuer configuration
as you can see from application properties there is a custom property which holds issuer name:
```yaml
auth-server:
  issuer-name: https://codeperfection.auth.com
```
This configuration is needed to correctly set 'iss' claim in generated access tokens, 
so that container orchestration can be done outside 'production' environment.
This mean that counterpart should also get corresponding changes to do issuer validation correctly
For details please see [example resource server configuration](https://github.com/codeperfection/ship-it/blob/main/src/main/kotlin/com/codeperfection/shipit/security/JwtIssuerValidatorReplacerConfig.kt)