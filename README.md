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

## Authentication mechanism

In this project, we are using a Username and Password authentication mechanism. 

The basic use case for this mechanism consists in a user sending a POST request to an authentication specific endpoint with the username and password in the request body. The application authenticates the user and returns a JWT token.

## Provider manager



### DaoAuthenticationProvider

The `DaoAuthenticationProvider` is an `AuthenticationProvider` implementation that supports Username and Password authentication mechanism. It uses the `UserDetailsService` and `PasswordEncoder` to authenticate a username and password.

TODO: Create a custom diagram to explain the `DaoAuthenticationProvider` usage.

![daoauthenticationprovider.png](src%2Fmain%2Fresources%2Fdaoauthenticationprovider.png)
*[DaoAuthenticationProvider Usage](https://docs.spring.io/spring-security/reference/servlet/authentication/passwords/dao-authentication-provider.html)*

## User entity and SecurityUser class

The `User` entity is used to store the user details in the database using Spring Data JPA.

The `SecurityUser` class is used to retrieve the user details from the database and provide them to the `AuthenticationManager`.

### Why do we need the `SecurityUser` class

Because we are using `DaoAuthenticationProvider` to authenticate the users, we need to implement the `UserDetails` interface to retrieve the user details from the database. 
Thus, we created the `SecurityUser` class that implements the `UserDetails` interface from Spring Security, which provides the user details to the `AuthenticationManager`.

We could have used the `User` entity to implements the `UserDetails` interface, but it is not recommended to expose the entity directly to the `AuthenticationManager`. The `SecurityUser` class provides a layer of abstraction between the entity and the `AuthenticationManager`.

## JpaUserDetailsService

The `JpaUserDetailsService` class implements the `UserDetailsService` interface from Spring Security. It is used to retrieve the user details from the database. If the user is not found, it throws a `UsernameNotFoundException`.

## Configuration

