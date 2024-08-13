# Spring Boot Security Authentication and Authorization project

This is a Spring Boot application that implements Spring Security for authentication and authorization.

## Contents

- [Features](#features)
- [Dependencies](#dependencies)
- [Implementation](#implementation)
  - [Spring Security Authentication Flow for DaoAuthenticationProvider](#spring-security-authentication-flow-for-daoauthenticationprovider)
  - [Spring Security Implementation](#spring-security-implementation)
    - [Security Filter Chain](#security-filter-chain)
    - [Authentication Manager](#authentication-manager)
    - [UserDetailsService](#userdetailservice)
    - [JwtAuthorizationFilter](#jwtauthorizationfilter)
  - [Security Exception Handling](#security-exception-handling)
    - [JwtAuthenticationEntryPoint](#jwtauthenticationentrypoint)
    - [AccessDeniedHandlerJwt](#accessdeniedhandlerjwt)

# Features

- Exposes endpoints to register and authenticate users (with username and password)
- Exposes endpoints to get user details
- Uses Spring Security for authentication and authorization
- Generates a JWT token for authenticated users to be used in subsequent requests
- Generates a refresh token to refresh the JWT token
- Stores the user details in a PostgreSQL database

**Additional features**:

- Uses Spring Data JPA for database operations
- Uses Flyway for database migrations
- Uses Spring Boot Docker Compose to start and stop a Docker container running the PostgreSQL database
- Includes a datasource configuration for testing purposes that uses the H2 in-memory database

In this readme file, we will focus on the implementation of the security features. For more details about the additional features, please refer to the [Spring Boot Template project](https://github.com/andrecaiado/spring-boot-template).

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

## Spring Security Authentication Flow for DaoAuthenticationProvider

In this project, we are using a Username and Password authentication mechanism.
The basic use case for this mechanism consists in a POST request being sent to an authentication specific endpoint with the username and password in the request body. The application authenticates the user and returns a JWT token.

Because we are storing the user details in a database, we use the `DaoAuthenticationProvider` to authenticate the users.

The implementation of the authentication mechanism is based on the following components and workflow:

![dao-auth-prov.png](src%2Fmain%2Fresources%2Fdao-auth-prov.png)

*[DaoAuthenticationProvider Usage (image from Spring documentation)](https://docs.spring.io/spring-security/reference/servlet/authentication/passwords/dao-authentication-provider.html)*

Components and workflow description ([from Spring documentation](https://docs.spring.io/spring-security/reference/servlet/authentication/passwords/dao-authentication-provider.html)):

1 - A UsernamePasswordAuthenticationToken is passed to the AuthenticationManager, which is implemented by ProviderManager.

2 - The ProviderManager is configured to use an AuthenticationProvider of type DaoAuthenticationProvider.

3 - DaoAuthenticationProvider looks up the UserDetails from the UserDetailsService.

4 - DaoAuthenticationProvider uses the PasswordEncoder to validate the password on the UserDetails returned in the previous step.

5 - When authentication is successful, the Authentication that is returned is of type UsernamePasswordAuthenticationToken and has a principal that is the UserDetails returned by the configured UserDetailsService. Ultimately, the returned UsernamePasswordAuthenticationToken is set on the SecurityContextHolder by the authentication Filter.

## Spring Security Implementation

In order to use Spring Security, we need to create a configuration class and annotate it with `@EnableWebSecurity`.
In this project, the security configuration is implemented in the [SecurityConfiguration.java](src%2Fmain%2Fjava%2Fcom%2Fexample%2Fspringbootsecurityauth%2Fconfig%2FSecurityConfiguration.java) class.

In this configuration class, we define the security filter chain, the authentication manager and the password encoder.
```java
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {
      ...
    }
    
    @Bean
    public AuthenticationManager authenticationManager() {
      ...
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
      return new BCryptPasswordEncoder();
    }
  
}
```

### Security Filter Chain

The security filter chain is a list of filters that are executed in a specific order. Each filter is responsible for a specific task, such as authorizing the access to specific endpoints or processing the JWT token from the request header.

In this project, we are using the following filters:

- `CsrfFilter`: Prevents CSRF attacks (currently disabled but should be enabled on production)
- `ExceptionHandling`: Handles exceptions thrown during the authentication process
- `SessionManagement`: Set the session management to be stateless because we don't want to store the session in the server
- `AuthorizeRequests`: Authorizes the requests based on the request matchers
- `AuthenticationManager`: Authenticates the user based on the username and password
- `JwtAuthenticationFilter`: Processes the JWT token from the request header. This filter is added in a `addFilterBefore` filter so a JWT authentication is processed before a username and password authentication.

The `SecurityFilterChain` is configured in the [SecurityConfiguration.java](src%2Fmain%2Fjava%2Fcom%2Fexample%2Fspringbootsecurityauth%2Fconfig%2FSecurityConfiguration.java) class.

```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {
  return http
          .csrf(cr->cr.disable())
          .exceptionHandling(
                  ex -> ex.accessDeniedHandler(accessDeniedHandlerJwt)
                          .authenticationEntryPoint(jwtAuthenticationEntryPoint)
          )
          .sessionManagement(session->session
                  .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
          .authorizeHttpRequests(req ->
                  req.requestMatchers("/auth/**").permitAll()
                          .requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN")
                          .anyRequest().authenticated()
          )
          .authenticationManager(authenticationManager)
          .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
          .build();
}
```

### Authentication Manager

The authentication manager is responsible for authenticating the user based on the username and password. It delegates the authentication process to one or more authentication providers.

In this project, we are using the `DaoAuthenticationProvider` as the authentication provider. The `DaoAuthenticationProvider` uses the `UserDetailsService` to load the user details from the database and the `PasswordEncoder` to verify the password.

The `AuthenticationManager` is configured in the [SecurityConfiguration.java](src%2Fmain%2Fjava%2Fcom%2Fexample%2Fspringbootsecurityauth%2Fconfig%2FSecurityConfiguration.java) class.

```java
@Bean
public AuthenticationManager authenticationManager() {
    DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
    authenticationProvider.setUserDetailsService(userDetailsService);
    authenticationProvider.setPasswordEncoder(passwordEncoder());

    return new ProviderManager(authenticationProvider);
}
```

### UserDetailsService

The `UserDetailsService` interface is used to retrieve user-related data. It has one method, `loadUserByUsername`, which is used to load the user based on the username. The `UserDetailsService` interface is implemented by the [UserDetailsServiceImpl.java](src%2Fmain%2Fjava%2Fcom%2Fexample%2Fspringbootsecurityauth%2Fservice%2FUserDetailsServiceImpl.java) class.

```java
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

  private final UserRepository userRepository;

  public UserDetailsServiceImpl(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    AppUser appUser = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("Username not found"));
    return new User(appUser.getUsername(), appUser.getPassword(), mapRolesToAuthorities(appUser.getRoles()));
  }

  ...
}
```

### JwtAuthorizationFilter

The JwtAuthorizationFilter is responsible for processing the JWT token from the request header. It extracts the token, validates it, and sets the authentication in the Security Context.

The JwtAuthorizationFilter is implemented in the [JwtAuthorizationFilter.java](src%2Fmain%2Fjava%2Fcom%2Fexample%2Fspringbootsecurityauth%2Fsecurity%2FJwtAuthorizationFilter.java) class.

## Security Exception Handling

In this project, we have two classes to handle exceptions thrown during the authentication process. They are specified in the `SecurityFilterChain` configuration.

### JwtAuthenticationEntryPoint

This class is responsible for handling exceptions thrown during the authentication process. It is used when the user is not authenticated.

### AccessDeniedHandlerJwt

This class is responsible for handling exceptions thrown when the user is authenticated but does not have the required authorities to access the endpoint.
