# `io.github.vaporsea.vsindustry.controllers`

This package contains the controllers for the Vapor Sea Industry application. Controllers in this package handle HTTP requests, process input, and return responses to the client. They act as the entry point for the application's REST API and web endpoints.

## Controllers

Below is a list of controllers in this package and their primary responsibilities:

### 1. `ExampleController`
- **Description**: Handles example-related operations.
- **Endpoints**:
    - `GET /api/example`: Fetches example data.
    - `POST /api/example`: Creates a new example entry.

### 2. `IndustryController`
- **Description**: Manages industry-related data and operations.
- **Endpoints**:
    - `GET /api/industry/jobs`: Retrieves industry jobs.
    - `POST /api/industry/jobs`: Submits a new industry job.

### 3. `MarketController`
- **Description**: Handles market transactions and related data.
- **Endpoints**:
    - `GET /api/market/transactions`: Fetches market transactions.
    - `GET /api/market/summary`: Provides a summary of market activity.

### 4. `CorporationController`
- **Description**: Manages corporation-related data and operations.
- **Endpoints**:
    - `GET /api/corporation/wallet`: Retrieves corporation wallet details.
    - `GET /api/corporation/contracts`: Fetches corporation contracts.

## How to Extend

To add a new controller:
1. Create a new class in this package and annotate it with `@RestController`.
2. Define request mappings using `@RequestMapping` or `@GetMapping`, `@PostMapping`, etc.
3. Inject necessary service-layer components.
4. Implement the required business logic and return appropriate responses.

## Dependencies

- **Spring Boot Starter Web**: For building RESTful web services.
- **Spring Security OAuth2**: For securing endpoints with OAuth2.
- **Validation Framework**: For request validation.

## Notes

- Ensure that the `application.yml` and `config/application.yml` files are properly configured for OAuth2 and other application settings.
- Refer to the `config/application-template.yml` for endpoint-specific configurations.
- OAuth2 is actually disabled as the new frontend app handles that along with token refreshes
  - I could never get spring-boot to do the refresh for me and I didn't feel like implementing that RestClient logic myself...

## If you're just here to run it

I recommend using docker to run this, but if you must know how to build from scratch:

Prerequisites:
- Java 17 or higher
- Maven 3.6 or higher

1. Clone the repository.
2. Package the application using Maven: `mvn clean package`.
3. Copy config/application-template.yml to config/application.yml and edit it to your liking. 
4. Run the application: `java -jar target/vapor-sea-industry-0.0.1-SNAPSHOT.jar`. 
5. This will create a data and vsindustry directories
6. Grab Squirrel SQL and setup the H2 driver inside there.
7. Configure the connection to point to the H2 database.
8. Update the users table and add your character.
9. Edit the user_roles and add a record with your character id and 1 (for the admin role)
10. Run the application again and grab the frontend.
