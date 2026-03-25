# Testing

**[Back to main project page](../../README.md)**

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

Integration tests use **Testcontainers** to spin up a real MySQL database automatically. The only requirement is that **Docker is running** — no manual database setup or environment variables are needed.

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
- ✅ Update user with valid names
- ✅ Update user without authentication (403)
- ✅ Update user with blank first name (400)
- ✅ Update user with blank last name (400)
- ✅ Authorization checks

## Running Tests in CI/CD

Tests are designed to run in a continuous integration environment using Github Actions.
