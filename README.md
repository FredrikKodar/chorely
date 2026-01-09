# Rewards App Backend

A Spring Boot REST API for managing a family reward system where parents can assign tasks to children and track their progress.

## Technology Stack

- **Spring Boot 4**
- **Spring Security** with JWT authentication
- **Spring Data JPA**
- **Maven**

## API Documentation

### Base URL
```
http://localhost:8080/api
```

---

## Authentication Endpoints

### Login
| Method | Endpoint | Description | Authentication Required |
|--------|----------|-------------|------------------------|
| POST | `/auth/login` | Authenticate user and receive JWT token | No |

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

---

### Register Parent
| Method | Endpoint | Description | Authentication Required |
|--------|----------|-------------|------------------------|
| POST | `/auth/register` | Register a new parent user | No |

**Request Body:**
```json
{
  "email": "parent@example.com",
  "password": "securePassword123"
}
```

**Validation:**
- Email must be valid format
- Password must be 8-40 characters

**Response (201 Created):**
```json
"User registered successfully"
```

---

### Register Child
| Method | Endpoint | Description | Authentication Required |
|--------|----------|-------------|------------------------|
| POST | `/auth/register-child` | Register a child user under authenticated parent | Yes (Parent) |

**Request Body:**
```json
{
  "username": "johnny_child",
  "password": "12345678",
  "firstName": "Johnny"
}
```

**Validation:**
- Password must be numeric only (8-20 characters)

**Response (201 Created):**
```json
"User registered successfully"
```

---

## User Endpoints

### Get User by ID
| Method | Endpoint | Description | Authentication Required |
|--------|----------|-------------|------------------------|
| GET | `/users/{id}` | Get user details by ID | Yes |

**Response (200 OK):**
```json
{
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
| Method | Endpoint | Description | Authentication Required |
|--------|----------|-------------|------------------------|
| GET | `/users/children` | Get all children associated with authenticated parent | Yes (Parent) |

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

## Task Endpoints

### Create Task (on Parent)
| Method | Endpoint | Description | Authentication Required |
|--------|----------|-------------|------------------------|
| POST | `/tasks` | Create a task assigned to authenticated user (parent) | Yes (Parent) |

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
- Points: Non-null, >= 0

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
| Method | Endpoint | Description | Authentication Required |
|--------|----------|-------------|------------------------|
| POST | `/tasks/{childId}` | Create a task assigned to specific child | Yes (Parent) |

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
| Method | Endpoint | Description | Authentication Required |
|--------|----------|-------------|------------------------|
| GET | `/tasks` | Get all tasks for authenticated user | Yes |

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
| Method | Endpoint | Description | Authentication Required |
|--------|----------|-------------|------------------------|
| GET | `/tasks/{id}` | Get specific task by ID (if user has access) | Yes |

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

### Update Task
| Method | Endpoint | Description | Authentication Required |
|--------|----------|-------------|------------------------|
| PATCH | `/tasks/{id}` | Update task fields (partial update) | Yes (Parent) |

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
| Method | Endpoint | Description | Authentication Required |
|--------|----------|-------------|------------------------|
| PATCH | `/tasks/{id}/toggle-status-child` | Toggle task between ASSIGNED and PENDING_APPROVAL | Yes (Child) |

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
| Method | Endpoint | Description | Authentication Required |
|--------|----------|-------------|------------------------|
| PATCH | `/tasks/{id}/approve` | Approve task (changes status from PENDING_APPROVAL to APPROVED) | Yes (Parent) |

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

- **ASSIGNED**: Task created by parent, assigned to child
- **PENDING_APPROVAL**: Child marked task as complete, awaiting parent approval
- **APPROVED**: Parent approved the task, points awarded

---

## Authentication

All authenticated endpoints require a JWT token in the `Authorization` header:

```
Authorization: Bearer <token>
```

Obtain the token via the `/auth/login` endpoint.

---

## Error Responses

### 400 Bad Request
```json
{
  "timestamp": "2026-01-09T10:00:00.000+00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/tasks"
}
```

### 401 Unauthorized
```json
{
  "timestamp": "2026-01-09T10:00:00.000+00:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Full authentication is required",
  "path": "/api/tasks"
}
```

### 403 Forbidden
```json
{
  "timestamp": "2026-01-09T10:00:00.000+00:00",
  "status": 403,
  "error": "Forbidden",
  "message": "Access denied",
  "path": "/api/tasks/1"
}
```

### 404 Not Found
```json
{
  "timestamp": "2026-01-09T10:00:00.000+00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Task not found",
  "path": "/api/tasks/999"
}
```

---

## Running the Application

```bash
mvn spring-boot:run
```

Application runs on `http://localhost:8080`
