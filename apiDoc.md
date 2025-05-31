# API Documentation

## 1. User Authentication

### 1.1 Login

**Endpoint:** `POST /auth/login`

#### Request

**Method:** POST

**Request Body:** None (Authentication handled via JWT or basic credentials)

**Headers:**

- `Content-Type: application/json` (Required)
- `Authorization: Bearer <your-jwt-token>` (Optional, if pre-authenticated)

#### Response

**Status Codes:**

- `200 OK`: Successfully logged in
- `401 Unauthorized`: Invalid credentials or missing authentication

**Response Body:**

```json 
{
  "jwt": "eyJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3NDgyODIzMTYsImV4cCI6MTc0OTEyODMxNiwiZW1haWwiOiJyaXlhZEBnbWFpbC5jb20iLCJhdXRob3JpdGllcyI6IiJ9.pngiqPgtFYxD4UjKjzRCk3E573qDn-WaYBJvoR4oAZk",
  "message": "Signin Success"
}
```

### 1.2 Register

**Endpoint:** `POST /auth/register`

#### Request

**Method:** POST

**Request Body:**

```json 
{
  "name": "hossain",
  "email": "hossain@gmail.com",
  "password": "123"
}
```

**Headers:**

- `Content-Type: application/json` (Required)

#### Response

**Status Codes:**

- `201 Created`: Successfully registered
- `400 Bad Request`: Invalid input data
- `409 Conflict`: User already exists

**Response Body:**

```json 
{
  "jwt": "eyJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3NDgyODIzOTUsImV4cCI6MTc0OTEyODM5NSwiZW1haWwiOiJob3NzYWluQGdtYWlsLmNvbSIsImF1dGhvcml0aWVzIjoiIn0.0AuhxlnxUTVdUYI79-FQHKAepivdmtrduFyOezgEBoA",
  "message": "Signin Success"
}
```

## 2. Tasks

### 2.1 Get Tasks

**Endpoint:** `GET /api/tasks`

#### Request

**Method:** GET

**Request Body:** None

**Headers:**

- `Authorization: Bearer <your-jwt-token>` (Required for authentication)

#### Response

**Status Codes:**

- `200 OK`: Successfully retrieved the list of tasks
- `401 Unauthorized`: Invalid or missing authentication token

**Response Body:**

```json 
[
  {
    "id": 2,
    "title": "Design dashboard",
    "description": "Create a user-friendly dashboard interface with charts and analytics",
    "status": "in_progress",
    "deadline": "2025-06-15T23:59:59Z",
    "priority": "high",
    "timeEstimate": "40 hours",
    "aiTimeEstimate": "35 hours",
    "aiPriority": "medium",
    "smartDeadline": "2025-06-10T17:00:00Z",
    "project": {
      "id": 1,
      "title": "Project Alpha",
      "description": "Complete redesign of the company's main web application",
      "createdBy": {
        "id": 1,
        "name": "sadat",
        "email": "sadat@gmail.com"
      },
      "createdAt": "2025-05-26T11:24:00Z"
    },
    "assignedTo": {
      "id": 2,
      "name": "riyad",
      "email": "riyad@gmail.com"
    },
    "assignedBy": {
      "id": 1,
      "name": "sadat",
      "email": "sadat@gmail.com"
    },
    "assignedAt": "2025-05-26T14:30:00Z",
    "parentTask": {
      "id": 1,
      "title": "UI/UX Redesign",
      "status": "in_progress"
    },
    "attachments": [],
    "tentativeStartingDate": "2025-05-28T09:00:00Z"
  }
]
```


## To-Do

- implement /auth/register and /auth/login
- login as sadat@gmail.com , password: 123 , you will get a jwt token
- use the jwt in the header like this : 

```json
Authorization: Bearer <jwt-token>
```
- then fetch the tasks by /api/tasks