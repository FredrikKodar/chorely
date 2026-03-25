# Frontend Overview

**[Back to main project page](../../README.md)**

## Table of Contents

<!-- TOC -->

* [Note on Origin](#note-on-origin)
* [Tech Stack](#tech-stack)
* [Project Structure](#project-structure)
* [Key Workflows](#key-workflows)
    * [Authentication Flow](#authentication-flow)
    * [Role-Based Routing](#role-based-routing)
    * [API Integration](#api-integration)
    * [Build Integration](#build-integration)

<!-- TOC -->

## Note on Origin

The entire frontend was generated using **Mistral Vibe** (AI-assisted coding). No frontend code was written by hand.

---

## Tech Stack

| Tool | Version | Purpose |
|------|---------|---------|
| React | 18 | UI library |
| TypeScript | 5 | Type safety |
| Vite | 5 | Build tool and dev server |
| Tailwind CSS | 3 | Utility-first styling |
| React Router | v6 | Client-side routing |
| Axios | 1 | HTTP client |

---

## Project Structure

```
frontend/src/
├── components/         # Reusable UI components
│   ├── children/       # Child management (registration, editing)
│   ├── dashboard/      # Stats cards and charts
│   ├── tasks/          # Task cards, forms, status badges
│   ├── ui/             # Generic UI primitives (Button, etc.)
│   └── ProtectedRoute.tsx
├── context/            # Global state
│   ├── AuthContext.tsx  # Auth state, login/logout
│   └── ThemeContext.tsx # Light/dark mode
├── layouts/            # Page shell components
│   ├── ParentLayout.tsx
│   ├── ChildLayout.tsx
│   └── SettingsLayout.tsx
├── pages/              # Route-level page components
│   ├── auth/           # Login, Register
│   ├── parent/         # Dashboard, Tasks, Children, History
│   ├── child/          # Dashboard, Tasks, TaskDetail
│   └── settings/       # Theme settings
├── services/           # API calls and utilities
│   ├── api.ts          # Axios instance
│   ├── authService.ts  # Login, register
│   ├── taskService.ts  # Task CRUD
│   ├── themeService.ts # Dark mode persistence
│   └── userService.ts  # User and children endpoints
└── types/              # TypeScript interfaces
    ├── auth.ts
    ├── tasks.ts
    ├── theme.ts
    └── user.ts
```

---

## Key Workflows

### Authentication Flow

1. User submits login form → `authService.login()` → `POST /api/auth/login`
2. JWT token received and stored in memory
3. `userService.getCurrentUser()` → `GET /api/users/me` to fetch full user profile including ID and role
4. Role extracted from response → user redirected to Parent or Child dashboard
5. Token attached to all subsequent requests via Axios interceptor in `services/api.ts`

### Role-Based Routing

Routes are wrapped in `ProtectedRoute`, which checks the authenticated user's role and redirects accordingly:

- **Parent** users → `ParentLayout` with access to dashboard, tasks, children management, and history
- **Child** users → `ChildLayout` with access to their own dashboard and task list

Unauthenticated users are redirected to `/login`.

### API Integration

All HTTP calls go through a single Axios instance in `services/api.ts`. In development, Vite proxies requests from `/api` to `http://localhost:8080`, so no CORS configuration is needed locally.

Each domain area has its own service file:

| Service | Endpoints used |
|---------|---------------|
| `authService.ts` | `POST /auth/login`, `POST /auth/register`, `POST /auth/register-child` |
| `userService.ts` | `GET /users/me`, `GET /users/{id}`, `GET /users/children`, `PATCH /users` |
| `taskService.ts` | `GET /tasks`, `POST /tasks`, `PATCH /tasks/{id}`, etc. |

### Build Integration

Running `npm run build` in the `frontend/` directory outputs compiled assets to `../src/main/resources/static`. Spring Boot serves these files as static content, so the frontend and backend are deployed as a single JAR.

For local development, run the Vite dev server (`npm run dev` in `frontend/`) alongside the Spring Boot backend. The dev server starts on port 3000.
