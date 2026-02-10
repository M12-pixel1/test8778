# Prometheus AgeTech - Setup Guide

## Prerequisites

- Python 3.11+
- Docker & Docker Compose (optional)
- Android Studio (for mobile app)

---

## Backend Setup

### Local Development

1. **Navigate to backend directory:**

   ```bash
   cd prometheus-age-tech/backend
   ```

2. **Create a virtual environment:**

   ```bash
   python -m venv venv
   source venv/bin/activate  # Linux/macOS
   # or
   venv\Scripts\activate     # Windows
   ```

3. **Install dependencies:**

   ```bash
   pip install -r requirements.txt
   ```

4. **Configure environment:**

   ```bash
   cp .env.example .env
   # Edit .env with your settings
   ```

5. **Run the server:**

   ```bash
   cd src
   uvicorn main:app --reload --host 0.0.0.0 --port 8000
   ```

6. **Access API documentation:**

   Open `http://localhost:8000/docs` for the interactive Swagger UI.

### Docker Setup

1. **Build and run with Docker Compose:**

   ```bash
   cd prometheus-age-tech
   docker-compose up --build
   ```

2. The API will be available at `http://localhost:8000`.

---

## Android Setup

1. **Open in Android Studio:**

   Open the `android/` directory in Android Studio.

2. **Sync Gradle files:**

   Android Studio will prompt you to sync Gradle files. Click "Sync Now".

3. **Configure API URL:**

   In `ApiClient.kt`, update `BASE_URL` to point to your backend:
   - Emulator: `http://10.0.2.2:8000`
   - Physical device: Use your computer's local IP address

4. **Run the app:**

   Select an emulator or connected device and click "Run".

---

## Testing the API

### Using curl

**Register a user:**

```bash
curl -X POST http://localhost:8000/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "test_senior",
    "email": "senior@test.com",
    "password": "password123",
    "full_name": "Test Senior",
    "age": 72,
    "is_senior": true
  }'
```

**Login:**

```bash
curl -X POST http://localhost:8000/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "test_senior", "password": "password123"}'
```

**Send SOS Alert:**

```bash
curl -X POST http://localhost:8000/seniors/sos/alert \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <your_token>" \
  -d '{"message": "Emergency!", "latitude": 40.7128, "longitude": -74.006}'
```
