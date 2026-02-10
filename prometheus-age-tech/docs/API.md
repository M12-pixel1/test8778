# Prometheus AgeTech API Documentation

## Base URL

```
http://localhost:8000
```

## Authentication

All protected endpoints require a Bearer token in the `Authorization` header:

```
Authorization: Bearer <access_token>
```

---

## Endpoints

### Authentication

#### POST /auth/register

Register a new user account.

**Request Body:**

```json
{
  "username": "john_senior",
  "email": "john@example.com",
  "password": "securepass123",
  "full_name": "John Smith",
  "age": 72,
  "is_senior": true,
  "phone": "+1234567890"
}
```

**Validation Rules:**
- `username`: 3-50 characters
- `password`: minimum 8 characters
- `age`: 18-120 (seniors must be 60-120)
- `is_senior`: if true, age must be between 60 and 120

**Response (201):**

```json
{
  "id": 1,
  "username": "john_senior",
  "email": "john@example.com",
  "full_name": "John Smith",
  "age": 72,
  "is_senior": true,
  "phone": "+1234567890",
  "created_at": "2024-01-01 00:00:00"
}
```

#### POST /auth/login

Authenticate and receive a JWT token.

**Request Body:**

```json
{
  "username": "john_senior",
  "password": "securepass123"
}
```

**Response (200):**

```json
{
  "access_token": "eyJhbGciOiJIUzI1NiIs...",
  "token_type": "bearer"
}
```

---

### Seniors

#### GET /seniors/{senior_id}/dashboard

Get the senior's dashboard with contacts, alerts, and check-in data.

**Headers:** `Authorization: Bearer <token>`

**Response (200):**

```json
{
  "senior": { ... },
  "emergency_contacts": [ ... ],
  "recent_alerts": [ ... ],
  "last_check_in": { ... }
}
```

#### POST /seniors/sos/alert

Send an emergency SOS alert.

**Headers:** `Authorization: Bearer <token>`

**Request Body:**

```json
{
  "latitude": 40.7128,
  "longitude": -74.0060,
  "message": "Emergency SOS Alert"
}
```

**Response (201):**

```json
{
  "id": 1,
  "senior_id": 1,
  "latitude": 40.7128,
  "longitude": -74.0060,
  "message": "Emergency SOS Alert",
  "created_at": "2024-01-01 00:00:00",
  "status": "active"
}
```

#### POST /seniors/{senior_id}/checkin

Submit a daily check-in.

**Headers:** `Authorization: Bearer <token>`

**Request Body:**

```json
{
  "mood": "good",
  "notes": "Feeling great today!"
}
```

**Valid mood values:** `good`, `okay`, `bad`

#### POST /seniors/{senior_id}/contacts

Add an emergency contact.

**Headers:** `Authorization: Bearer <token>`

**Request Body:**

```json
{
  "name": "Jane Smith",
  "phone": "+1234567890",
  "relationship": "Daughter"
}
```

---

### Users

#### GET /users/me

Get the current authenticated user's profile.

**Headers:** `Authorization: Bearer <token>`

---

### Health Check

#### GET /health

Returns API health status.

**Response (200):**

```json
{
  "status": "healthy"
}
```
