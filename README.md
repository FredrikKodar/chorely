# Chorely

A family task tracking system where parents assign tasks to children and track their progress. Children earn points by
completing tasks, which parents approve.

## Table of Contents

<!-- TOC -->

* [Features](#features)
* [Technology Stack](#technology-stack)
* [Quick Start](#quick-start)
    * [Backend](#backend)
    * [Frontend](#frontend)
* [CI/CD](#cicd)
* [Documentation](#documentation)
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

### Backend

- **Spring Boot 4**
- **Spring Security** with JWT authentication
- **Spring Data JPA**
- **MySQL**
- **Maven**
- **Mockito / JUnit**
- **MapStruct**
- **Testcontainers**

### Frontend

- **React 18** with **TypeScript**
- **Vite**
- **Tailwind CSS**
- **Axios**
- **React Router v6**

## Quick Start

### Backend

1. **Set environment variables:**

```bash
export SPRING_PROFILES_ACTIVE=dev
export DB_DEV_URL=jdbc:mysql://localhost:3306/your_dev_database
export DB_USER=your_username
export DB_PASSWORD=your_password
export SECRET_KEY=your-secret-key-at-least-256-bits
export JWT_EXPIRES_IN=86400000
```

2. **Run:**

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

Backend runs on `http://localhost:8080`.

### Frontend

```bash
cd frontend
npm install
npm run dev
```

Frontend dev server runs on `http://localhost:3000` and proxies API requests to the backend.

## CI/CD

### Release flow

This project uses [Release Please](https://github.com/googleapis/release-please) for automated versioning and releases.

1. Every merge to `main` triggers Release Please, which creates or updates a release PR that bumps the version and
   updates the changelog.
2. Merging the release PR publishes a GitHub release.
3. Publishing a release triggers the **Deploy to Elastic Beanstalk** workflow, which:
    - Builds the frontend (`npm run build` in `frontend/`)
    - Builds the backend JAR with the frontend embedded (`./mvnw clean package`)
    - Deploys the JAR to AWS Elastic Beanstalk (`eu-north-1`, application `chorely`)

### Other workflows

- **Unit tests** — run on every push
- **Integration tests** — run on every push using Testcontainers (Docker-based)

## Documentation

- [Backend API Reference](docs/backend/API.md)
- [Frontend Overview](docs/frontend/OVERVIEW.md)
- [Testing Guide](docs/backend/TESTS.md)

## Troubleshooting

### Application connects to wrong database

If the application connects to `chorely_test` or another unexpected database, check your active Spring profile:

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
See the [Backend Quick Start](#backend) section for the full list.
