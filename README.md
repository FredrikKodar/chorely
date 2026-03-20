# Rewards App Backend

A Spring Boot REST API for managing a family reward system where parents can assign tasks to children and track their
progress.

## Table of Contents

<!-- TOC -->

* [Features](#features)
* [Technology Stack](#technology-stack)
* [Quick Start](#quick-start)
    * [Prerequisites](#prerequisites)
    * [Running the Application](#running-the-application)
        * [Development Profile (Recommended for local development)](#development-profile-recommended-for-local-development)
        * [Production Profile](#production-profile)
* [Project Structure](#project-structure)
* [Documentation](#documentation)
    * [API Documentation](#api-documentation)
    * [Testing Documentation](#testing-documentation)
* [Troubleshooting](#troubleshooting)
    * [Application connects to wrong database](#application-connects-to-wrong-database)
    * [Missing environment variables error](#missing-environment-variables-error)

<!-- TOC -->

## Features

- JWT-based authentication
- Role-based access control (Parent/Child roles)
- Task creation, assignment, and approval workflow
- Points tracking system
- Parent-child user relationship management

## Technology Stack

- **Spring Boot 3**
- **Spring Security** with JWT authentication
- **Spring Data JPA**
- **MySQL database**
- **Maven**
- **Mockito**
- **JUnit**
- **Mapstruct**

## Quick Start

### Prerequisites

- Java 21+
- Maven 3.6+
- MySQL 8.0+

### Running the Application

#### Development Profile (Recommended for local development)

1. **Set environment variables:**

```bash
export SPRING_PROFILES_ACTIVE=dev
export DB_DEV_URL=jdbc:mysql://localhost:3306/your_dev_database
export DB_USER=your_username
export DB_PASSWORD=your_password
export SECRET_KEY=your-secret-key-at-least-256-bits
export JWT_EXPIRES_IN=86400000
```

2. **Run with dev profile:**

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

#### Production Profile

For production, use the default profile (no profile specified) and ensure environment variables are set in your
deployment environment.

```bash
unset SPRING_PROFILES_ACTIVE
export DB_DEV_URL=jdbc:mysql://localhost:3306/your_dev_database
export DB_USER=your_username
export DB_PASSWORD=your_password
export SECRET_KEY=your-secret-key-at-least-256-bits
export JWT_EXPIRES_IN=86400000
mvn spring-boot:run
```

Application runs on `http://localhost:8080`

## Project Structure

```
src/
├── main/
│   ├── java/com/fredande/rewardsappbackend/
│   │   ├── config/                     # Configuration
│   │   ├── controller/                 # REST controllers
│   │   ├── dto/                        # Data transfer objects
│   │   ├── enums/                      # Enumerations
│   │   ├── exceptionhandler/           # Exception handling
│   │   ├── mapper/                     # Mappers
│   │   ├── model/                      # Entity models
│   │   ├── repository/                 # Data access layer
│   │   ├── security/                   # Security configuration
│   │   └── service/                    # Business logic
│   └── resources/
│       └── application.properties      # Configuration for production environment
│       └── application-test.properties # Configuration for testing environment
└── test/
    └── java/com/fredande/rewardsappbackend/
        ├── controller/                 # Controller integration tests
        ├── service/                    # Service unit tests
        └── testUtils/                  # Helper classes for testing
```

## Documentation

### [API Documentation](docs/API.md)

### [Testing Documentation](docs/TESTS.md)

## Troubleshooting

### Application connects to wrong database

If the application connects to `rewards_app_test` or another unexpected database, check your active Spring profile:

```bash
echo $SPRING_PROFILES_ACTIVE
```

**Solution:**

- For **development**: Set it to `dev`
  ```bash
  export SPRING_PROFILES_ACTIVE=dev
  ```

- For **testing**: Set it to `test`
  ```bash
  export SPRING_PROFILES_ACTIVE=test
  ```

- For **production** (not intended for local development or testing): Unset it
  ```bash
  unset SPRING_PROFILES_ACTIVE
  ```

### Missing environment variables error

If you see errors like `Could not resolve placeholder 'DB_URL'`, ensure all required environment variables are set.
See each run case for specific settings.
