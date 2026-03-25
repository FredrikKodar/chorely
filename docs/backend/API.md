# API Documentation

**[Back to main project page](../../README.md)**

## Table of Contents

<!-- TOC -->

* [Base URL](#base-url)
* [Authentication Endpoints](#authentication-endpoints)
    * [Login](#login)
    * [Register Parent](#register-parent)
    * [Register Child](#register-child)
* [User Endpoints](#user-endpoints)
    * [Get Current User](#get-current-user)
    * [Get User by ID](#get-user-by-id)
    * [Get Children](#get-children)
    * [Update Parent User](#update-parent-user)
* [Task Endpoints](#task-endpoints)
    * [Create Task (on Parent)](#create-task-on-parent)
    * [Create Task for Child](#create-task-for-child)
    * [Get All Tasks](#get-all-tasks)
    * [Get Task by ID](#get-task-by-id)
    * [Get tasks with status PENDING_APPROVAL](#get-tasks-with-status-pending_approval)
    * [Update Task](#update-task)
    * [Toggle Task Status (Child)](#toggle-task-status-child)
    * [Approve Task](#approve-task)
* [Task Status Flow](#task-status-flow)
* [Error Handling](#error-handling)
* [Authentication](#authentication)

<!-- TOC -->

## Base URL

```
http://localhost:8080/api
```

---

## Authentication Endpoints

### Login

| Method | Endpoint      | Description                             | Authentication Required |
|--------|---------------|-----------------------------------------|-------------------------|
| POST   | `/auth/login` | Authenticate user and receive JWT token | No                      |

**Request Body:**

```json
{
  "email": "parent@example.com",
  "password": "securePassword123"
}
```

**Response (200 OK):**

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresIn": 86400,
  "roles": "[ROLE_PARENT]"
}
```

**Note:** The `roles` field returns authorities as a string (e.g., "[ROLE_PARENT]" or "[ROLE_CHILD]")

---

### Register Parent

| Method | Endpoint         | Description                | Authentication Required |
|--------|------------------|----------------------------|-------------------------|
| POST   | `/auth/register` | Register a new parent user | No                      |

**Request Body:**

```json
{
  "email": "parent@example.com",
  "password": "securePassword123",
  "firstName": "John",
  "lastName": "Doe"
}
```

**Validation:**

- Email must match regex: `^[a-zA-Z0-9_+&*-]+(?:\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\.)+[a-zA-Z]{2,7}$`
- Password must be 8-40 characters
- First name and last name must not be blank

**Response (201 Created):**

```json
"User registered successfully"
```

---

### Register Child

| Method | Endpoint               | Description                                      | Authentication Required |
|--------|------------------------|--------------------------------------------------|-------------------------|
| POST   | `/auth/register-child` | Register a child user under authenticated parent | Yes (Parent)            |

**Request Body:**

```json
{
  "username": "johnny_child",
  "password": "12345678",
  "firstName": "Johnny"
}
```

**Validation:**

- Username: No specific validation constraints
- Password: Must be numeric only (8-20 characters)
- firstName: No specific validation constraints

**Response (201 Created):**

```json
"User registered successfully"
```

---

## User Endpoints

### Get Current User

| Method | Endpoint      | Description            | Authentication Required |
|--------|---------------|------------------------|-------------------------|
| GET    | `/users/me` | Get current authenticated user's profile | Yes                     |

**Response (200 OK):**

```json
{
  "id": 1,
  "email": "parent@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "currentPoints": 100,
  "totalPoints": 500,
  "numTasksOpen": 2,
  "numTasksCompleted": 8,
  "numTasksTotal": 10
}
```

**✅ Resolves**: User ID requirement for frontend login flow
**✅ Status**: IMPLEMENTED in backend
**✅ Usage**: Frontend can now fetch user data without knowing user ID

---

### Get User by ID

| Method | Endpoint      | Description            | Authentication Required |
|--------|---------------|------------------------|-------------------------|
| GET    | `/users/{id}` | Get user details by ID | Yes                     |

**Response (200 OK):**

```json
{
  "id": 2,
  "email": "child@example.com",
  "firstName": "Johnny",
  "lastName": "Doe",
  "currentPoints": 150,
  "totalPoints": 500,
  "numTasksOpen": 3,
  "numTasksCompleted": 10,
  "numTasksTotal": 13
}
```

---

### Get Children

| Method | Endpoint          | Description                                           | Authentication Required |
|--------|-------------------|-------------------------------------------------------|-------------------------|
| GET    | `/users/children` | Get all children associated with authenticated parent | Yes (Parent)            |

**Response (200 OK):**

```json
[
  {
    "id": 2,
    "firstName": "Johnny"
  },
  {
    "id": 3,
    "firstName": "Sarah"
  }
]
```

---

### Update Parent User

| Method | Endpoint  | Description                              | Authentication Required |
|--------|-----------|------------------------------------------|-------------------------|
| PATCH  | `/users`  | Update the authenticated parent's names | Yes (Parent)            |

**Request Body:**

```json
{
  "firstName": "John",
  "lastName": "Doe"
}
```

**Validation:**

- First name and last name must not be blank

**Response (200 OK):**

```json
{
  "id": 1,
  "email": "parent@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "currentPoints": 0,
  "totalPoints": 0,
  "numTasksOpen": 0,
  "numTasksCompleted": 0,
  "numTasksTotal": 0
}
```

---

## Task Endpoints

### Create Task (on Parent)

| Method | Endpoint | Description                                           | Authentication Required |
|--------|----------|-------------------------------------------------------|-------------------------|
| POST   | `/tasks` | Create a task assigned to authenticated user (parent) | Yes (Parent)            |

**Request Body:**

```json
{
  "title": "Clean your room",
  "description": "Vacuum the floor and organize your desk",
  "points": 50
}
```

**Validation:**

- Title: 8-140 characters, not blank
- Description: 8-255 characters, not blank  
- Points: Non-null, must be >= 0

**Response (201 Created):**

```json
{
  "id": 1,
  "title": "Clean your room",
  "description": "Vacuum the floor and organize your desk",
  "points": 50
}
```

---

### Create Task for Child

| Method | Endpoint           | Description                              | Authentication Required |
|--------|--------------------|------------------------------------------|-------------------------|
| POST   | `/tasks/{childId}` | Create a task assigned to specific child | Yes (Parent)            |

**Request Body:**

```json
{
  "title": "Do homework",
  "description": "Complete math assignment pages 10-15",
  "points": 30
}
```

**Response (201 Created):**

```json
{
  "id": 2,
  "title": "Do homework",
  "description": "Complete math assignment pages 10-15",
  "points": 30
}
```

---

### Get All Tasks

| Method | Endpoint | Description                          | Authentication Required |
|--------|----------|--------------------------------------|-------------------------|
| GET    | `/tasks` | Get all tasks for authenticated user | Yes                     |

**Response (200 OK):**

```json
[
  {
    "id": 1,
    "title": "Clean your room",
    "description": "Vacuum the floor and organize your desk",
    "points": 50,
    "created": "2026-01-09T10:00:00.000+00:00",
    "updated": "2026-01-09T10:00:00.000+00:00",
    "status": "ASSIGNED"
  },
  {
    "id": 2,
    "title": "Do homework",
    "description": "Complete math assignment pages 10-15",
    "points": 30,
    "created": "2026-01-09T11:00:00.000+00:00",
    "updated": "2026-01-09T11:00:00.000+00:00",
    "status": "PENDING_APPROVAL"
  }
]
```

---

### Get Task by ID

| Method | Endpoint      | Description                                  | Authentication Required |
|--------|---------------|----------------------------------------------|-------------------------|
| GET    | `/tasks/{id}` | Get specific task by ID (if user has access) | Yes                     |

**Response (200 OK):**

```json
{
  "id": 1,
  "title": "Clean your room",
  "description": "Vacuum the floor and organize your desk",
  "points": 50,
  "created": "2026-01-09T10:00:00.000+00:00",
  "updated": "2026-01-09T10:00:00.000+00:00",
  "status": "ASSIGNED"
}
```

---

### Get tasks with status PENDING_APPROVAL

| Method | Endpoint                  | Description                                               | Authentication Required |
|--------|---------------------------|-----------------------------------------------------------|-------------------------|
| GET    | `/tasks/pending-approval` | Get user's created tasks where status is PENDING_APPROVAL | Yes (Parent)            |

**Response (200 OK):**

```json
[
  {
    "id": 1,
    "title": "Clean your room",
    "description": "Vacuum the floor and organize your desk",
    "points": 50,
    "created": "2026-01-09T10:00:00.000+00:00",
    "updated": "2026-01-09T10:00:00.000+00:00",
    "status": "PENDING_APPROVAL"
  }
]
```

---

### Update Task

| Method | Endpoint      | Description                         | Authentication Required |
|--------|---------------|-------------------------------------|-------------------------|
| PATCH  | `/tasks/{id}` | Update task fields (partial update) | Yes (Parent)            |

**Request Body (all fields optional):**

```json
{
  "title": "Clean your room thoroughly",
  "description": "Vacuum the floor, organize desk, and make the bed",
  "points": 75,
  "status": "ASSIGNED"
}
```

**Response (201 Created):**

```json
{
  "id": 1,
  "title": "Clean your room thoroughly",
  "description": "Vacuum the floor, organize desk, and make the bed",
  "points": 75,
  "created": "2026-01-09T10:00:00.000+00:00",
  "updated": "2026-01-09T12:30:00.000+00:00",
  "status": "ASSIGNED"
}
```

---

### Toggle Task Status (Child)

| Method | Endpoint                          | Description                                       | Authentication Required |
|--------|-----------------------------------|---------------------------------------------------|-------------------------|
| PATCH  | `/tasks/{id}/toggle-status-child` | Toggle task between ASSIGNED and PENDING_APPROVAL | Yes (Child)             |

**Response (201 Created):**

```json
{
  "id": 1,
  "title": "Clean your room",
  "description": "Vacuum the floor and organize your desk",
  "points": 50,
  "created": "2026-01-09T10:00:00.000+00:00",
  "updated": "2026-01-09T13:00:00.000+00:00",
  "status": "PENDING_APPROVAL"
}
```

---

### Approve Task

| Method | Endpoint              | Description                                                     | Authentication Required |
|--------|-----------------------|-----------------------------------------------------------------|-------------------------|
| PATCH  | `/tasks/{id}/approve` | Approve task (changes status from PENDING_APPROVAL to APPROVED) | Yes (Parent)            |

**Response (201 Created):**

```json
{
  "id": 1,
  "title": "Clean your room",
  "description": "Vacuum the floor and organize your desk",
  "points": 50,
  "created": "2026-01-09T10:00:00.000+00:00",
  "updated": "2026-01-09T14:00:00.000+00:00",
  "status": "APPROVED"
}
```

---

## Task Status Flow

```
ASSIGNED → PENDING_APPROVAL → APPROVED
   ↑              ↓
   └──────────────┘
```

### Task Status Enum Values

The `TaskStatus` enum has the following values:

```json
{
  "status": "ASSIGNED" | "PENDING_APPROVAL" | "APPROVED"
}
```

- **ASSIGNED**: Task created by parent, assigned to child
- **PENDING_APPROVAL**: Child marked task as complete, awaiting parent approval  
- **APPROVED**: Parent approved the task, points awarded

### Status Transitions

- **ASSIGNED → PENDING_APPROVAL**: Child toggles task status (child only)
- **PENDING_APPROVAL → APPROVED**: Parent approves task (parent only)
- **PENDING_APPROVAL → ASSIGNED**: Parent can reject by updating status back to ASSIGNED

---

## Error Handling

The API follows standard HTTP status codes:

| Status Code | Description | Common Causes |
|-------------|-------------|---------------|
| 200 OK | Success | Request completed successfully |
| 201 Created | Resource created | POST requests that create resources |
| 400 Bad Request | Invalid request | Validation failures, malformed JSON |
| 401 Unauthorized | Authentication required | Missing or invalid JWT token |
| 403 Forbidden | No permission | User doesn't have required role |
| 404 Not Found | Resource not found | Invalid user ID, task ID, etc. |
| 500 Internal Server Error | Server error | Unexpected exceptions |

### Error Response Format

```json
{
  "timestamp": "2026-01-09T10:00:00.000+00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/tasks"
}
```

For validation errors, additional details may be included:

```json
{
  "timestamp": "2026-01-09T10:00:00.000+00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "errors": [
    "Title must be 8-140 characters.",
    "Description must not be blank."
  ],
  "path": "/api/tasks"
}
```

## Authentication

All authenticated endpoints require a JWT token in the `Authorization` header:

```
Authorization: Bearer <token>
```

Obtain the token via the `/auth/login` endpoint.

### JWT Token Handling

- **Storage**: Store token in memory (not localStorage) for security
- **Expiration**: Token expires after 86400 seconds (24 hours)
- **Refresh**: No automatic refresh - user must login again after expiration
- **Roles**: Token contains user roles/authorities
