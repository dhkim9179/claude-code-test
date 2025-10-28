# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Spring Boot 3.5.7 batch processing application built with Java 21. The project uses Spring Batch for batch job execution, MyBatis for database access, and Oracle database for data persistence.

## Technology Stack

- **Java**: 21 (managed via Gradle Toolchain)
- **Spring Boot**: 3.5.7
- **Spring Batch**: For batch job processing
- **MyBatis**: 3.0.5 (ORM layer)
- **Database**: Oracle (ojdbc11)
- **Build Tool**: Gradle with wrapper
- **Testing**: JUnit 5 Platform
- **Code Generation**: Lombok

## Common Development Commands

### Building and Running
```bash
# Build the project
./gradlew build

# Run the application
./gradlew bootRun

# Clean build artifacts
./gradlew clean
```

### Testing
```bash
# Run all tests
./gradlew test

# Run a specific test class
./gradlew test --tests com.example.demo.YourTestClass

# Run a specific test method
./gradlew test --tests com.example.demo.YourTestClass.testMethodName

# Run tests with additional logging
./gradlew test --info
```

### Dependencies
```bash
# Show dependency tree
./gradlew dependencies

# Check for dependency updates
./gradlew dependencyUpdates
```

## Architecture

### Package Structure
- **Base Package**: `com.example.demo`
- **Source**: `src/main/java` - Application code
- **Resources**: `src/main/resources` - Configuration files, SQL mappers
- **Tests**: `src/test/java` - Test code

### Spring Batch Components
When implementing batch jobs, follow Spring Batch architecture:
- **Job**: Define batch jobs using `@Configuration` classes
- **Step**: Create steps with readers, processors, and writers
- **ItemReader**: Read data from sources (database, files)
- **ItemProcessor**: Transform/process data
- **ItemWriter**: Write processed data to targets

### MyBatis Integration
- MyBatis XML mappers should be placed in `src/main/resources/mybatis/mapper/`
- Mapper interfaces in Java code under appropriate packages
- Use `@Mapper` annotation for mapper interfaces

### Database Configuration
- Oracle database configuration in `application.properties`
- Connection pooling is handled by Spring Boot defaults
- Consider using separate profiles for different environments (dev, test, prod)

## Development Notes

### Lombok Usage
- Project uses Lombok to reduce boilerplate code
- Enable annotation processing in your IDE
- Common annotations: `@Data`, `@Getter`, `@Setter`, `@Builder`, `@Slf4j`

### Configuration
- Main configuration: `src/main/resources/application.properties`
- For profile-specific configs, use `application-{profile}.properties`

### Code Organization
- Keep batch job configurations separate from business logic
- Use service layer for business logic
- Repository/Mapper layer for database access
- DTOs/Entities in dedicated packages
