# Prometheus AgeTech - Senior Protection Platform

A full-stack senior care platform providing safety monitoring and emergency features for elderly users.

## Features

- **SOS Emergency Alerts** - One-tap emergency button with GPS location sharing
- **Daily Check-In** - Simple wellness reporting for seniors
- **Emergency Contacts** - Manage and notify emergency contacts
- **JWT Authentication** - Secure user authentication
- **Senior Dashboard** - Overview of alerts, contacts, and check-ins

## Tech Stack

| Component | Technology |
|-----------|------------|
| Backend   | Python 3.11+, FastAPI, SQLite |
| Android   | Kotlin, Jetpack Compose, Material 3 |
| Auth      | JWT (JSON Web Tokens), bcrypt |
| Deploy    | Docker, Docker Compose |

## Project Structure

```
prometheus-age-tech/
├── backend/          # FastAPI backend API
│   ├── src/          # Python source code
│   ├── requirements.txt
│   └── Dockerfile
├── android/          # Android mobile app
│   └── app/          # Kotlin source and resources
├── docs/             # Documentation
├── scripts/          # Setup and deployment scripts
└── docker-compose.yml
```

## Quick Start

### Backend

```bash
cd backend
python -m venv venv
source venv/bin/activate
pip install -r requirements.txt
cd src
uvicorn main:app --reload --host 0.0.0.0 --port 8000
```

### Docker

```bash
docker-compose up --build
```

### Android

Open the `android/` directory in Android Studio, sync Gradle, and run on an emulator or device.

## API Endpoints

| Method | Endpoint                      | Description              |
|--------|-------------------------------|--------------------------|
| POST   | `/auth/register`              | Register a new user      |
| POST   | `/auth/login`                 | Login and get JWT token  |
| GET    | `/seniors/{id}/dashboard`     | Senior dashboard         |
| POST   | `/seniors/sos/alert`          | Send SOS emergency alert |
| POST   | `/seniors/{id}/checkin`       | Daily wellness check-in  |
| POST   | `/seniors/{id}/contacts`      | Add emergency contact    |
| GET    | `/users/me`                   | Get current user profile |

## Documentation

- [API Documentation](docs/API.md)
- [Setup Guide](docs/SETUP.md)
- [Architecture](docs/ARCHITECTURE.md)

## License

This project is for educational and demonstration purposes.
