# Prometheus AgeTech - System Architecture

## Overview

Prometheus AgeTech is a full-stack senior care platform designed to provide safety and monitoring features for elderly users. The system consists of a Python backend API and an Android mobile application.

---

## Architecture Diagram

```
┌─────────────────┐     HTTPS/REST      ┌─────────────────────┐
│   Android App   │ ◄──────────────────► │   FastAPI Backend   │
│   (Kotlin)      │                      │   (Python)          │
│                 │                      │                     │
│  - Login        │                      │  - JWT Auth         │
│  - SOS Button   │                      │  - User Management  │
│  - Check-In     │                      │  - SOS Alerts       │
│  - GPS Location │                      │  - Emergency Contacts│
└─────────────────┘                      └──────────┬──────────┘
                                                    │
                                                    │
                                              ┌─────┴─────┐
                                              │  SQLite    │
                                              │  Database  │
                                              └───────────┘
```

---

## Backend Architecture

### Technology Stack

- **Framework:** FastAPI (Python 3.11+)
- **Database:** SQLite
- **Authentication:** JWT (JSON Web Tokens)
- **Password Hashing:** bcrypt via passlib
- **Deployment:** Docker

### Module Structure

| Module         | Responsibility                        |
| -------------- | ------------------------------------- |
| `main.py`      | FastAPI application entry point       |
| `models.py`    | Pydantic request/response models      |
| `database.py`  | SQLite database operations            |
| `services.py`  | Business logic layer                  |
| `auth.py`      | JWT authentication and authorization  |
| `api/`         | Route handlers organized by domain    |

### Database Schema

**users** - Stores registered user accounts
- `id`, `username`, `email`, `password_hash`, `full_name`, `age`, `is_senior`, `phone`, `created_at`

**emergency_contacts** - Emergency contacts linked to seniors
- `id`, `senior_id`, `name`, `phone`, `relationship`

**sos_alerts** - SOS alert history
- `id`, `senior_id`, `latitude`, `longitude`, `message`, `status`, `created_at`

**daily_checkins** - Daily wellness check-ins
- `id`, `senior_id`, `mood`, `notes`, `created_at`

---

## Android Architecture

### Technology Stack

- **Language:** Kotlin
- **UI Framework:** Jetpack Compose with Material 3
- **Networking:** HttpURLConnection (lightweight)
- **Local Storage:** SharedPreferences
- **Location:** Google Play Services Location API

### Key Components

| Component          | Responsibility                         |
| ------------------ | -------------------------------------- |
| `LoginActivity`    | User authentication screen             |
| `MainActivity`     | Main dashboard with SOS and check-in   |
| `SOSActivity`      | Emergency SOS alert screen             |
| `ApiClient`        | HTTP client for backend communication  |
| `SeniorDataStore`  | Local data persistence                 |

### Key Features

1. **SOS Emergency Button** - Large, prominent button for sending emergency alerts with GPS location
2. **Daily Check-In** - Simple mood-based wellness reporting
3. **GPS Location Sharing** - Automatic location inclusion in SOS alerts
4. **Simple UI** - Large text and buttons designed for senior accessibility

---

## Security

- JWT tokens for API authentication
- bcrypt password hashing
- HTTPS enforcement in production
- Location permissions requested at runtime on Android
