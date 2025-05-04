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
1. Build and start the containers:
   ```
   docker-compose up -d
   ```
2. The application will be available at http://localhost:8080

## API Documentation
The application provides RESTful endpoints for managing sleep data.

## Development Guidelines
Please refer to the [guidelines document](.junie/guidelines.md) for coding standards and best practices.
