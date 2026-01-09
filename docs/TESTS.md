# Testing

**[Back to main project page](../README.md)**

## Table of Contents

<!-- TOC -->

* [Overview](#overview)
* [Test Structure](#test-structure)
* [Running Tests](#running-tests)
    * [Prerequisites](#prerequisites)
    * [Run Unit Tests](#run-unit-tests)
    * [Run Integration Tests](#run-integration-tests)
* [Test Categories](#test-categories)
    * [Unit Tests](#unit-tests)
        * [Service Layer Tests](#service-layer-tests)
            * [Test classes](#test-classes)
    * [Integration Tests](#integration-tests)
        * [Controller Layer Tests](#controller-layer-tests)
            * [Test classes](#test-classes-1)
* [Test Scenarios](#test-scenarios)
    * [Authentication Tests](#authentication-tests)
    * [Task Tests](#task-tests)
    * [User Tests](#user-tests)
* [Running Tests in CI/CD](#running-tests-in-cicd)

<!-- TOC -->

## Overview

The project uses **JUnit 5** and **Mockito** for testing. Tests are organized by layer (service, controller,
repository) to ensure comprehensive coverage of business logic and API behavior.

## Test Structure

```
src/test/java/com/fredande/rewardsappbackend/
├── controller/
│   ├── AuthenticationControllerIT.java
│   ├── TaskControllerIT.java
│   └── UserControllerIT.java
└── service/
    ├── AuthenticationServiceTest.java
    ├── TaskServiceTest.java
    └── UserServiceTest.java

```

## Running Tests

### Prerequisites

Before running integration tests, create a test database. Note that create-drop is used, so the data will be dropped
when test run is completed.

Tests require environment variables for database connection. Set them before running tests:

```bash
export SPRING_PROFILES_ACTIVE=test
export DB_TEST_URL=jdbc:mysql://localhost:3306/your_test_database
export DB_USER=your_username
export DB_PASSWORD=your_password
export SECRET_KEY=your-test-secret-key
export JWT_EXPIRES_IN=86400000
```

### Run Unit Tests

```bash
mvn clean test
```

### Run Integration Tests

```bash
mvn clean verify
```

## Test Categories

### Unit Tests

#### Service Layer Tests

Focus on testing business logic in isolation using mocked dependencies.

##### Test classes

- **AuthenticationServiceTest** - Authentication, registration
- **TaskServiceTest** - Task CRUD operations, status transitions, authorization
- **UserServiceTest** - User management, parent-child relationships

### Integration Tests

#### Controller Layer Tests

Controller integration tests using `@SpringBootTest`, `TestRestTemplate`, `@WithMockUser` and `MockMvc` to test the full
request/response cycle, including generation and validation of JWT tokens.

- **TestRestTemplate** - Testing with valid JWT tokens.
- **@WithMockUser** - Mocking authenticated user.

##### Test classes

- **AuthenticationControllerIT** - Authentication, registration
- **TaskControllerIT** - Task CRUD operations, status transitions, authorization
- **UserControllerIT** - User management, parent-child relationships

## Test Scenarios

### Authentication Tests

- ✅ User login with valid credentials
- ✅ User login with invalid credentials
- ✅ Parent registration
- ✅ Child registration
- ✅ JWT token generation and validation

### Task Tests

- ✅ Create task on parent
- ✅ Create task for child
- ✅ Get all tasks by user
- ✅ Get task by ID
- ✅ Get tasks with status PENDING_APPROVAL
- ✅ Update task
- ✅ Toggle task status (child)
- ✅ Approve task (parent)
- ✅ Authorization checks

### User Tests

- ✅ Get user by ID
- ✅ Get children for parent
- ✅ Authorization checks

## Running Tests in CI/CD

Tests are designed to run in a continuous integration environment using Github Actions.
