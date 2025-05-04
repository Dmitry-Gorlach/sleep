# Sleep Application

## Overview
This is a Spring Boot application for tracking and managing sleep data. 
The application provides a RESTful API for recording and analyzing sleep patterns.

## Setup and Installation

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

Note: The application uses environment variables for database configuration. These are set in the `.env` file, .env example:
SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/postgres
DB_USER=postgres
DB_PASSWORD=postgres

The Dockerfile uses the `openjdk:21-slim` base image, which is a Debian-based image that includes the apt package manager needed for installing additional dependencies.

## API Documentation
The application provides RESTful endpoints for managing sleep data.

## Development Guidelines
Please refer to the [guidelines document](.junie/guidelines.md) for coding standards and best practices.
