"""Pydantic models for request/response validation."""

from datetime import datetime
from typing import Optional

from pydantic import BaseModel, Field, validator


class UserRegister(BaseModel):
    username: str = Field(..., min_length=3, max_length=50)
    email: str = Field(..., min_length=5)
    password: str = Field(..., min_length=8)
    full_name: str = Field(..., min_length=1, max_length=100)
    age: int = Field(..., ge=18, le=120)
    is_senior: bool = False
    phone: Optional[str] = None

    @validator("age")
    def validate_senior_age(cls, v, values):
        if values.get("is_senior") and (v < 60 or v > 120):
            raise ValueError("Seniors must be between 60 and 120 years old")
        return v


class UserLogin(BaseModel):
    username: str
    password: str


class UserResponse(BaseModel):
    id: int
    username: str
    email: str
    full_name: str
    age: int
    is_senior: bool
    phone: Optional[str] = None
    created_at: str


class TokenResponse(BaseModel):
    access_token: str
    token_type: str = "bearer"


class EmergencyContact(BaseModel):
    name: str = Field(..., min_length=1, max_length=100)
    phone: str = Field(..., min_length=7, max_length=20)
    relationship: str = Field(..., min_length=1, max_length=50)


class EmergencyContactResponse(BaseModel):
    id: int
    senior_id: int
    name: str
    phone: str
    relationship: str


class SOSAlert(BaseModel):
    latitude: Optional[float] = None
    longitude: Optional[float] = None
    message: Optional[str] = "Emergency SOS Alert"


class SOSAlertResponse(BaseModel):
    id: int
    senior_id: int
    latitude: Optional[float]
    longitude: Optional[float]
    message: str
    created_at: str
    status: str


class DailyCheckIn(BaseModel):
    mood: str = Field(..., pattern="^(good|okay|bad)$")
    notes: Optional[str] = None


class DashboardResponse(BaseModel):
    senior: UserResponse
    emergency_contacts: list
    recent_alerts: list
    last_check_in: Optional[dict] = None
