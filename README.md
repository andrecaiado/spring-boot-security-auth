# Spring Boot Security Authentication and Authorization project
This is a Spring Boot application that implements Spring Security for authentication and authorization.

## Features

- Exposes endpoints to register and authenticate users
- Exposes an endpoint to retrieve the current user information
- Uses Spring Security for authentication and authorization
- Generates a JWT token for authenticated users
- Uses JWT token for authorization
- Stores the user details in a PostgreSQL database

### Additional features

- Uses Spring Data JPA for database operations
- Uses Flyway for database migrations
- Uses Spring Boot Docker Compose to start and stop a Docker container running the PostgreSQL database
- Includes a datasource configuration for testing purposes that uses the H2 in-memory database

In this readme file, we will focus on the main features' implementation. For more details about the additional features, please refer to the [Spring Boot Template project](https://github.com/andrecaiado/spring-boot-template).

## Dependencies

To use Spring Security, we added the Spring Boot Starter Security dependency to the `pom.xml` file.

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```
