# Junie Guidelines for Sleep Application

## Code Style Guidelines

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

## Testing Guidelines
- Write unit tests for all business logic
- Use meaningful test names that describe the scenario being tested
- Follow the Arrange-Act-Assert pattern
- Aim for high test coverage, especially for critical paths

## Database Guidelines
- Use Flyway migrations for all database changes
- Document complex SQL queries
- Use meaningful names for tables and columns
- Follow database normalization principles

## Documentation Guidelines
- Keep README up to date
- Document API endpoints
- Include setup instructions for local development

## Git Guidelines
- Write meaningful commit messages
- Use feature branches for new development
- Squash commits before merging to main branch
- Keep pull requests focused on a single feature or fix

## Security Guidelines
- Never commit sensitive information (passwords, API keys)
- Validate all user inputs
- Use proper authentication and authorization
- Follow the principle of least privilege