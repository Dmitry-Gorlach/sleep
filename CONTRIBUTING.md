# Contributing to Sleep Logger API

This document outlines the conventions and guidelines for contributing to the Sleep Logger API project.

## Branch Naming Conventions

- Feature branches: `feature/SSL-X-short-descriptor`
  - Example: `feature/SSL-4-sleep-log-domain-model`

## Commit Message Conventions

- Format: `SSL-X: Short description`
  - Example: `SSL-4: Add SleepLog entity and repository`

## Code Style Guidelines

Please follow the Junie code style guidelines as defined in the `.junie/guidelines.md` file. These include:

### Java/Kotlin
- Use 4 spaces for indentation
- Maximum line length: 120 characters
- Follow standard Java/Kotlin naming conventions
  - Classes: PascalCase
  - Methods/Functions: camelCase
  - Variables: camelCase
  - Constants: UPPER_SNAKE_CASE
- Add appropriate Javadoc/KDoc comments for public APIs

### Spring Boot Specific
- Use constructor injection over field injection
- Prefer immutable configurations when possible
- Use appropriate Spring annotations (@Service, @Repository, @Controller, etc.)
- Follow RESTful API design principles

## Git Workflow

1. Create a new branch from `develop` using the branch naming convention
2. Make your changes, following the code style guidelines
3. Write meaningful commit messages following the commit message convention
4. Push your branch to the remote repository
5. Create a pull request to merge your changes into `develop`
6. Squash commits before merging to `develop` branch
7. Keep pull requests focused on a single feature or fix

## Testing

- Write unit tests for all business logic
- Use meaningful test names that describe the scenario being tested
- Follow the Arrange-Act-Assert pattern
- Aim for high test coverage, especially for critical paths

## Development Environment Setup

Before starting development, ensure you have set up your environment correctly:

1. Make sure you have Java 21 or higher installed
2. Set the JAVA_HOME environment variable
3. Install Gradle if you're not using the wrapper

## Building the Project

Before submitting a pull request, ensure that the project builds successfully:

```bash
./gradlew build
```

This will compile the code, run the tests, and apply the Junie code-styles.
