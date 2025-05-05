# Sleep Application

## Overview
This is a Spring Boot application for tracking and managing sleep data. 
The application provides a RESTful API for recording and analyzing sleep patterns.

## Quick Start Guide

### Prerequisites
- Java 21 or higher
- Gradle
- Docker and Docker Compose (for running with containers)

### Running Locally
1. Clone the repository
2. Navigate to the project directory
3. Run the application using Gradle:
   ```
   ./gradlew bootRun
   ```

### Running with Docker
1. Ensure Docker daemon is running on your system
2. Build and start the containers:
   ```
   docker-compose up -d
   ```
3. The application will be available at http://localhost:8080

## Database Migration
The application uses Flyway for database migrations. Migrations are automatically applied when the application starts.

- Migration files are located in `src/main/resources/db/migration`
- The naming convention is `V{version}__{description}.sql`
- Migrations are applied in order based on version number

To manually trigger migrations:
```
./gradlew flywayMigrate
```

## API Documentation
The application provides RESTful endpoints for managing sleep data. 

### Swagger UI
The Swagger UI is available at: http://localhost:8080/swagger-ui.html

The OpenAPI specification is available at: http://localhost:8080/api-docs

### Postman Collection
A Postman collection is available in the `/scripts` directory. Import this collection into Postman to quickly test the API endpoints.

### API Endpoints

#### Create Sleep Log
- **URL**: `POST /api/sleep-logs`
- **Headers**: 
  - `Content-Type: application/json`
  - `X-User-ID: {uuid}` (required)
- **Request Body**:
  ```json
  {
    "sleepDate": "2023-10-15",
    "bedTime": "2023-10-15T22:00:00Z",
    "wakeTime": "2023-10-16T06:00:00Z",
    "feeling": "GOOD"
  }
  ```
- **Response** (201 Created):
  ```json
  {
    "sleepDate": "2023-10-15",
    "bedTime": "2023-10-15T22:00:00Z",
    "wakeTime": "2023-10-16T06:00:00Z",
    "totalTimeInBedMinutes": 480,
    "feeling": "GOOD"
  }
  ```

#### Get Latest Sleep Log
- **URL**: `GET /api/sleep-logs/latest`
- **Headers**: 
  - `X-User-ID: {uuid}` (required)
- **Response** (200 OK):
  ```json
  {
    "sleepDate": "2023-10-15",
    "bedTime": "2023-10-15T22:00:00Z",
    "wakeTime": "2023-10-16T06:00:00Z",
    "totalTimeInBedMinutes": 480,
    "feeling": "GOOD"
  }
  ```

#### Get Sleep Statistics
- **URL**: `GET /api/sleep-logs/statistics`
- **Headers**: 
  - `X-User-ID: {uuid}` (required)
- **Response** (200 OK):
  ```json
  {
    "averageTotalTimeInBedMinutes": 480.0,
    "averageBedTime": "22:30:00",
    "averageWakeTime": "06:30:00",
    "feelingCounts": {
      "GOOD": 3,
      "OK": 2,
      "BAD": 1
    }
  }
  ```

## Development Guidelines
Please refer to the [guidelines document](.junie/guidelines.md) for coding standards and best practices.
