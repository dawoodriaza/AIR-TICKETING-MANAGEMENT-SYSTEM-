# Airtick Springboot REST API

An MVP REST API for managing air ticket bookings, built with Spring Boot. This system provides functionality for flight management, user authentication, booking operations, airport management, and payment processing. The API supports role-based access control with JWT authentication and includes features such as email verification, password recovery, and image uploads.

## Related Repositories

- **Front-end Application**: [airtick-ui](https://github.com/read2see/airtick-ui) - Next.js front-end for consuming this REST API

## Tools and Technologies

### Core Technologies
- **Spring Boot 4.0.1** - Java application framework
- **Spring Data JPA** - Data persistence layer
- **Spring Security** - Authentication and authorization
- **PostgreSQL** - Relational database
- **JWT (JSON Web Tokens)** - Token-based authentication
- **MapStruct** - Object mapping library
- **Lombok** - Java annotation processor
- **Spring Mail** - Email service integration
- **Thymeleaf** - Template engine for email templates
- **SpringDoc OpenAPI** - API documentation (Swagger)
- **Java 17** - Programming language
- ~~**Twilio SDK** - SMS and WhatsApp messaging integration~~

### Development Tools
- **[Intellij Idea](https://www.jetbrains.com/idea/)** - Java IDE
- **[Postman](https://www.postman.com/)** - API testing and documentation
- **[Docker](https://www.docker.com/)** - Running Mailpit container
- **[Mailpit](https://mailpit.axllent.org/)** - Email testing tool
- **[MJML](https://mjml.io/)** - Generating email compatible Template
- **[Trello](https://trello.com/)** - Project management and task tracking
- **[Draw.io](https://www.drawio.com/)** - System architecture and diagramming

## Prerequisites

Before you begin, ensure you have the following installed:

- **Java 17** or higher
- **Maven 3.6+** (or use the included Maven wrapper)
- **PostgreSQL** (latest stable version)
- **Git** for version control
- **Mailpit** for testing email sending

## Installation

### 1. Clone the Repository

```bash
git clone https://github.com/dawoodriaza/AIR-TICKETING-MANAGEMENT-SYSTEM
cd AIR-TICKETING-MANAGEMENT-SYSTEM
```

### 2. Database Setup

1. Create a PostgreSQL database:
```sql
CREATE DATABASE airtick;
```

2. Update database credentials in `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/airtick
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### 3. Configure Application Properties

Update the following in `src/main/resources/application.properties` or create `application-dev.properties`:

- JWT secret key
- Email server configuration (for email verification and password reset)
- File upload directory path
- ~~Twilio credentials (for SMS/WhatsApp notifications)~~

### 4. Build and Run

- Use IDE to build and run.

The API will be available at `http://localhost:8080` unless port was changed.

### 5. Access API Documentation

Once the application is running, access the Swagger UI at:
```
http://localhost:8080/swagger-ui/index.html
```

### 6. Start Mailpit

> [!NOTE]
> 
> - This project will use mailpit through a docker container.
> - For installing mailpit and running it directly on your machine please refer to [Mailpit Docs](https://mailpit.axllent.org/docs/install/).

Run Mailpit Docker Container
```bash
docker run -d \
--restart unless-stopped \
--name=mailpit \
-p 8025:8025 \
-p 1025:1025 \
axllent/mailpit
```
Set `application-dev.properties` for Mailpit
```
spring.mail.host=localhost
spring.mail.port=1025
spring.mail.username=
spring.mail.password=
spring.mail.properties.mail.smtp.auth=false
spring.mail.properties.maiil.smtp.starttls.enable=false
```
Go to `localhost:8025` in your browser to view sent emails.

## API Documentation

### Auth

| Request Type | Endpoint                           | Functionality                     | Access |
|--------------|------------------------------------|-----------------------------------|--------|
| POST         | `/auth/users/register`             | Register new user                 | PUBLIC |
| POST         | `/auth/users/login`                | Login user                        | PUBLIC |
| POST         | `/auth/users/resend-verification`  | Request resend verification email | PUBLIC |
| GET          | `/auth/users/verify`               | Verify User Email                 | PUBLIC |
| POST         | `/auth/users/forgot-password`      | Request password reset email      | PUBLIC |
| POST         | `/auth/users/reset-password/token` | Reset password with token         | PUBLIC |
| PATCH        | `/auth/users/change-password`      | Change password while logged-in   | PRIVATE|
| GET          | `/auth/users/me`                   | Get current authenticated user    | PRIVATE|
| POST         | `/auth/users/logout`               | Logout user                       | PRIVATE|
| DELETE       | `/auth/users/{userId}`             | Soft delete user                  | PRIVATE (ADMIN)|
| PATCH        | `/auth/users/{userId}/reactivate`  | Reactivate user                   | PRIVATE (ADMIN)|
| GET          | `/auth/users/search`               | Search users                      | PRIVATE (ADMIN)|

### Airport

| Request Type | Endpoint                    | Functionality             | Access |
|--------------|-----------------------------|---------------------------|--------|
| GET          | `/api/airports/{id}`        | Retrieve airport          | PUBLIC |
| GET          | `/api/airports`             | Retrieve List of airports | PUBLIC |
| POST         | `/api/airports`             | Create airport            | PRIVATE (ADMIN)|
| PUT          | `/api/airports/{id}`        | Update airport            | PRIVATE (ADMIN)|
| DELETE       | `/api/airports/{id}`        | Delete airport            | PRIVATE (ADMIN)|

### Flight

| Request Type | Endpoint                                             | Functionality                      | Access |
|--------------|------------------------------------------------------|------------------------------------|--------|
| GET          | `/api/flights`                                       | Retrieve list of flights           | PUBLIC |
| GET          | `/api/flights/{flightId}`                            | Retrieve flight                    | PUBLIC |
| GET          | `/api/flights/browse`                                | Browse flights with filters        | PUBLIC |
| POST         | `/api/flights`                                       | Create flight                      | PRIVATE (ADMIN)|
| PUT          | `/api/flights/{flightId}`                            | Update flight                      | PRIVATE (ADMIN)|
| DELETE       | `/api/flights/{flightId}`                            | Delete flight                      | PRIVATE (ADMIN)|

### Airport-Flight

| Request Type | Endpoint                                             | Functionality                      | Access |
|--------------|------------------------------------------------------|------------------------------------|--------|
| GET          | `/api/airports/{airportId}/flights/{flightId}`       | Retrieve flight                    | PUBLIC |
| GET          | `/api/airports/{airportId}/flights`                  | Retrieve List of flights           | PUBLIC |
| GET          | `/api/airports/{airportId}/departures`               | Retrieve List of departing flights | PUBLIC |
| GET          | `/api/airports/{airportId}/arrivals`                 | Retrieve List of arriving flights| PUBLIC |
| POST         | `/api/airports/{originAirportId}/flights`             | Create flight                      | PRIVATE (ADMIN)|
| PUT          | `/api/airports/{airportId}/flights/{flightId}`       | Update flight                      | PRIVATE (ADMIN)|

### Booking

| Request Type | Endpoint                           | Functionality                     | Access |
|--------------|------------------------------------|-----------------------------------|--------|
| GET          | `/api/bookings`                    | Retrieve list of bookings         | PRIVATE|
| GET          | `/api/bookings/{id}`               | Retrieve booking by ID            | PRIVATE|
| GET          | `/api/bookings/users/{userId}`     | Retrieve bookings by user ID      | PRIVATE|
| POST         | `/api/bookings`                    | Create booking                    | PRIVATE|
| PUT          | `/api/bookings/{id}`               | Update booking                    | PRIVATE|
| DELETE       | `/api/bookings/{id}`               | Delete booking                    | PRIVATE|

### User Profile

| Request Type | Endpoint                           | Functionality                     | Access |
|--------------|------------------------------------|-----------------------------------|--------|
| GET          | `/api/user-profiles/{id}`          | Retrieve user profile             | PUBLIC |
| GET          | `/api/user-profiles`               | Retrieve list of user profiles    | PUBLIC |
| POST         | `/api/user-profiles`               | Create user profile               | PRIVATE (ADMIN)|
| PUT          | `/api/user-profiles/{id}`          | Update user profile               | PRIVATE (ADMIN)|
| DELETE       | `/api/user-profiles/{id}`          | Delete user profile               | PRIVATE (ADMIN)|
| PUT          | `/api/users/me/profile`            | Update my user profile (JSON)     | PRIVATE|
| PUT          | `/api/users/me/profile/upload`     | Update my user profile with file  | PRIVATE|

### Image

| Request Type | Endpoint                           | Functionality                     | Access |
|--------------|------------------------------------|-----------------------------------|--------|
| POST         | `/api/images/upload`               | Upload image                      | PRIVATE|
| GET          | `/api/images/{fileName}`           | Retrieve image                    | PUBLIC |
| GET          | `/api/images/all`                  | Retrieve all images               | PUBLIC |
| GET          | `/api/images/download/{fileName}`  | Download image                    | PUBLIC |
| DELETE       | `/api/images/{id}`                 | Delete image                      | PRIVATE|

## Entity Relationship Diagram

### Initial ERD
![old erd](/diagrams/erd/flight_booking_erd_old.drawio.png)

### Final ERD
![final erd](/diagrams/erd/flight_booking_erd_final.drawio.png)

### Design Assumptions & Constraints

The final ERD is based on the following domain and data-integrity assumptions:

- A user may upload multiple assets. Currently, assets are used for profile images but the model is designed to support future features requiring user uploads. Deleting a user will cascade to their assets.
- A user will always have an associated user profile created at the time of user registration. Deleting a user will cascade to their user profile.
- A user may create multiple bookings. Deleting a user will **not** cascade to their bookings in order to preserve booking history.
- A flight may have multiple bookings. Deleting a flight is restricted if bookings exist.
- An airport may be the origin of multiple flights. Deleting an airport is restricted if it is referenced as a flight origin.
- An airport may be the destination of multiple flights. Deleting an airport is restricted if it is referenced as a flight destination.

## Postman Export

[Postman exported collection](/postman/Flight%20Booking%20System%20(final).postman_collection.json)

## Future Work & Improvements

- Dockerize Project.
- JUnit testing.
- Implement jobs queue with retries for email sending jobs using RabbitMQ and Redis.
- Payment Processing Mock.
- Analytics.

## Resources

The following resources were referenced during the design and implementation of this Spring Boot MVP REST API project:

### DTOs & Mapping
- Understanding DTOs in Spring Boot – Medium  
  https://medium.com/@roshanfarakate/understanding-dtos-in-spring-boot-a-comprehensive-guide-20e2b8101ee6
- MapStruct DTO Mapper – Baeldung  
  https://www.baeldung.com/mapstruct

### Spring Data JPA & Pagination
- Spring Data JPA Query Methods (Paging, Sorting, Limiting)  
  https://docs.spring.io/spring-data/jpa/reference/repositories/query-methods-details.html
- Pageable API Documentation  
  https://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/domain/Pageable.html
- REST API Pagination – Baeldung  
  https://www.baeldung.com/rest-api-pagination-in-spring
- Spring Data Pagination – Petrikainulainen  
  https://www.petrikainulainen.net/programming/spring-framework/spring-data-jpa-tutorial-part-seven-pagination/
- REST Query Language with Spring Data JPA Specifications - Baeldung    
  https://www.baeldung.com/rest-api-search-language-spring-data-specifications

### Validation
- Jakarta Bean Validation Guide – Medium  
  https://agussyahrilmubarok.medium.com/guide-to-field-validation-with-jakarta-validation-in-spring-8c9eca68022e
- Jakarta Bean Validation API Docs  
  https://jakarta.ee/specifications/bean-validation/3.1/apidocs/

### Email Sending
- Spring Boot Email Support  
  https://docs.spring.io/spring-boot/reference/io/email.html

### Async Processing & Events
- Spring @Async – Baeldung  
  https://www.baeldung.com/spring-async
- Spring Events – Baeldung  
  https://www.baeldung.com/spring-events
- Async Method Guide – Spring  
  https://spring.io/guides/gs/async-method
- Async Pitfalls & Gotchas – Medium  
  https://medium.com/@dvikash1001/springboot-async-the-magic-and-the-gotchas-17f9471c6fe4

### Authentication & Security
- OpenAPI + JWT Configuration – Baeldung  
  https://www.baeldung.com/openapi-jwt-authentication
- OWASP Forgot Password Cheat Sheet  
  https://cheatsheetseries.owasp.org/cheatsheets/Forgot_Password_Cheat_Sheet.html
- Forgot Password Best Practices – Stack Overflow  
  https://stackoverflow.com/questions/31949159/best-practices-for-forgot-password-function-via-rest-api

### Database Migrations
- Flyway Migrations in Spring Boot – JetBrains  
  https://blog.jetbrains.com/idea/2024/11/how-to-use-flyway-for-database-migrations-in-spring-boot-applications/

### API Documentation 
- Configure JWT Authentication for OpenAPI - Baeldung   
  https://www.baeldung.com/openapi-jwt-authentication

## License

This project is licensed under the MIT License.