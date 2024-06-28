# Spring Boot Security Authentication and Authorization project
This is a Spring Boot application that implements Spring Security for authentication and authorization.

## Contents

- [Features](#features)
- [Dependencies](#dependencies)
- [Implementation](#implementation)
  - [Security filter chain](#security-filter-chain)
  - [Authentication mechanism](#authentication-mechanism)
    - [UsernamePasswordAuthenticationToken](#usernamepasswordauthenticationtoken)
    - [AuthenticationProvider](#authenticationprovider)
    - [UserDetailsService and PasswordEncoder](#userdetailsservice-and-passwordencoder)
    - [User entity and SecurityUser class](#user-entity-and-securityuser-class)
  - [Authorization](#authorization)

# Features

- Exposes endpoints to register and authenticate users (with username and password)
- Exposes an endpoint to retrieve the current user information
- Uses Spring Security for authentication and authorization
- Generates a JWT token for authenticated users
- Uses JWT token for authorization
- Stores the user details in a PostgreSQL database

**Additional features**:

- Uses Spring Data JPA for database operations
- Uses Flyway for database migrations
- Uses Spring Boot Docker Compose to start and stop a Docker container running the PostgreSQL database
- Includes a datasource configuration for testing purposes that uses the H2 in-memory database

In this readme file, we will focus on the main features' implementation. For more details about the additional features, please refer to the [Spring Boot Template project](https://github.com/andrecaiado/spring-boot-template).

# Dependencies

To use Spring Security, we added the Spring Boot Starter Security dependency to the `pom.xml` file.

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

To use JWT tokens, we added the following dependencies to the `pom.xml` file.

```xml
<dependency>
  <groupId>io.jsonwebtoken</groupId>
  <artifactId>jjwt-api</artifactId>
  <version>0.11.5</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.11.5</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.11.5</version>
</dependency>
```

# Implementation

In order to access a protected resource, a request goes through a filter chain, and an authentication and authorization mechanism.

## Security filter chain

The security filter chain is a list of filters that are executed in a specific order. Each filter is responsible for a specific task, such as authorizing the access to specific endpoints or processing the JWT token from the request header.

In this project, we are using the following filters:

- `CsrfFilter`: Prevents CSRF attacks (currently disabled but should be enabled on production)
- `Authorize Requests`: Authorizes the requests based on the request matchers
- `Session Management`: Set the session management to be stateless because we don't want to store the session in the server
- `Authentication Provider`: To set TODO 
- `JwtAuthenticationFilter`: Processes the JWT token from the request header. This filter is added in a `addFilterBefore` filter so a JWT authentication is processed before a username and password authentication.

The `SecurityFilterChain` is configured in the [SecurityConfig.java](src%2Fmain%2Fjava%2Fcom%2Fexample%2Fspringbootsecurityauth%2Fconfig%2FSecurityConfig.java) class.

```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests((authorize) -> authorize
            .requestMatchers("/auth/login").permitAll()
            .requestMatchers("/auth/signup").permitAll()
            .anyRequest().permitAll()
        )
        .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authenticationProvider(authenticationProvider())
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    
    return http.build();
}
```

## Authentication mechanism

In this project, we are using a Username and Password authentication mechanism. 
The basic use case for this mechanism consists in a user sending a POST request to an authentication specific endpoint with the username and password in the request body. The application authenticates the user and returns a JWT token.

Because we are storing the user details in a database, we use the `DaoAuthenticationProvider` to authenticate the users.

The implementation of the authentication mechanism is based on the following components and workflow:

![daoauthenticationprovider.png](src%2Fmain%2Fresources%2Fdaoauthenticationprovider.png#center)

*[DaoAuthenticationProvider Usage (image from Spring documentation)](https://docs.spring.io/spring-security/reference/servlet/authentication/passwords/dao-authentication-provider.html)*

Components and workflow description ([from Spring documentation](https://docs.spring.io/spring-security/reference/servlet/authentication/passwords/dao-authentication-provider.html)):

1 - A UsernamePasswordAuthenticationToken is passed to the AuthenticationManager, which is implemented by ProviderManager.

2 - The ProviderManager is configured to use an AuthenticationProvider of type DaoAuthenticationProvider.

3 - DaoAuthenticationProvider looks up the UserDetails from the UserDetailsService.

4 - DaoAuthenticationProvider uses the PasswordEncoder to validate the password on the UserDetails returned in the previous step.

5 - When authentication is successful, the Authentication that is returned is of type UsernamePasswordAuthenticationToken and has a principal that is the UserDetails returned by the configured UserDetailsService. Ultimately, the returned UsernamePasswordAuthenticationToken is set on the SecurityContextHolder by the authentication Filter.

### UsernamePasswordAuthenticationToken

The username and password are passed to the `AuthenticationManager` in a `UsernamePasswordAuthenticationToken` object.

Example from the [AuthenticationService.java](src%2Fmain%2Fjava%2Fcom%2Fexample%2Fspringbootsecurityauth%2Fservice%2FAuthenticationService.java) class:

```java
public Authentication signIn(String username, String password) {
    Authentication authentication = new UsernamePasswordAuthenticationToken(username, password);
    return authenticationManager.authenticate(authentication);
}
```

### AuthenticationProvider

In the [SecurityConfig.java](src%2Fmain%2Fjava%2Fcom%2Fexample%2Fspringbootsecurityauth%2Fconfig%2FSecurityConfig.java) class, we configure the `AuthenticationManager` and the `PasswordEncoder`.

The `AuthenticationManager` is configured to use a `DaoAuthenticationProvider` that uses a `UserDetailsService` and a `PasswordEncoder`.

Example from the [SecurityConfig.java](src%2Fmain%2Fjava%2Fcom%2Fexample%2Fspringbootsecurityauth%2Fconfig%2FSecurityConfig.java) class:

```java
@Bean
public AuthenticationManager authenticationManager(UserDetailsService jpaUserDetailsService, PasswordEncoder passwordEncoder) {
    DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
    authenticationProvider.setUserDetailsService(jpaUserDetailsService);
    authenticationProvider.setPasswordEncoder(passwordEncoder);
  
    return new ProviderManager(authenticationProvider);
}

@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

### UserDetailsService and PasswordEncoder

The `UserDetailsService` is an interface that retrieves the user details from the database.

When the `authenticate` method from the `AuthenticationManager` is called, the `loadUserByUsername` method from the `UserDetailsService` is called to retrieve the user details from the database.

Example from the [JpaUserDetailsService.java](src%2Fmain%2Fjava%2Fcom%2Fexample%2Fspringbootsecurityauth%2Fservice%2FJpaUserDetailsService.java) class:

```java
@Override
public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return userRepository.findByUsername(username)
        .map(SecurityUser::new)
        .orElseThrow(() -> new UsernameNotFoundException("Username not found: " + username));
}
```
The user returned by the `UserDetailsService` is wrapped in a `SecurityUser` object that implements the `UserDetails` interface from Spring Security.

### User entity and SecurityUser class

The `User` entity ([User.java](src%2Fmain%2Fjava%2Fcom%2Fexample%2Fspringbootsecurityauth%2Fentity%2FUser.java)) is used to store the user details in the database using Spring Data JPA.

The `SecurityUser` ([SecurityUser.java](src%2Fmain%2Fjava%2Fcom%2Fexample%2Fspringbootsecurityauth%2Fentity%2FSecurityUser.java)) class is used to retrieve the user details from the database and provide them to the `AuthenticationManager`.

#### Why do we need the `SecurityUser` class

Because we are using `DaoAuthenticationProvider` to authenticate the users, we need to implement the `UserDetails` interface to retrieve the user details from the database. 
Thus, we created the `SecurityUser` class that implements the `UserDetails` interface from Spring Security, which provides the user details to the `AuthenticationManager`.

We could have used the `User` entity to implements the `UserDetails` interface, but it is not recommended to expose the entity directly to the `AuthenticationManager`. The `SecurityUser` class provides a layer of abstraction between the entity and the `AuthenticationManager`.

# Authorization

The authorization mechanism is implemented in the [JwtAuthenticationFilter.java](src%2Fmain%2Fjava%2Fcom%2Fexample%2Fspringbootsecurityauth%2Fsecurity%2FJwtAuthenticationFilter.java) class.

Method security is enabled in the [SecurityConfig.java](src%2Fmain%2Fjava%2Fcom%2Fexample%2Fspringbootsecurityauth%2Fconfig%2FSecurityConfig.java) class.

Annotations in controller etc
