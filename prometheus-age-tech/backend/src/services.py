"""Business logic for Prometheus AgeTech."""

from typing import Optional

import database as db
from auth import get_password_hash, verify_password, create_access_token
from models import (
    UserRegister,
    UserLogin,
    SOSAlert,
    DailyCheckIn,
    EmergencyContact,
)


def register_user(user_data: UserRegister) -> dict:
    existing = db.get_user_by_username(user_data.username)
    if existing:
        raise ValueError("Username already exists")

    password_hash = get_password_hash(user_data.password)
    user = db.create_user(
        username=user_data.username,
        email=user_data.email,
        password_hash=password_hash,
        full_name=user_data.full_name,
        age=user_data.age,
        is_senior=user_data.is_senior,
        phone=user_data.phone,
    )
    return user


def authenticate_user(login_data: UserLogin) -> Optional[dict]:
    user = db.get_user_by_username(login_data.username)
    if not user or not verify_password(login_data.password, user["password_hash"]):
        return None
    return user


def get_senior_dashboard(senior_id: int) -> dict:
    senior = db.get_user_by_id(senior_id)
    if not senior:
        raise ValueError("Senior not found")
    if not senior["is_senior"]:
        raise ValueError("User is not registered as a senior")

    contacts = db.get_emergency_contacts(senior_id)
    alerts = db.get_sos_alerts(senior_id)
    last_checkin = db.get_last_checkin(senior_id)

    return {
        "senior": senior,
        "emergency_contacts": contacts,
        "recent_alerts": alerts,
        "last_check_in": last_checkin,
    }


def trigger_sos_alert(senior_id: int, alert_data: SOSAlert) -> dict:
    senior = db.get_user_by_id(senior_id)
    if not senior:
        raise ValueError("Senior not found")

    alert = db.create_sos_alert(
        senior_id=senior_id,
        latitude=alert_data.latitude,
        longitude=alert_data.longitude,
        message=alert_data.message,
    )
    return alert


def daily_check_in(senior_id: int, checkin_data: DailyCheckIn) -> dict:
    senior = db.get_user_by_id(senior_id)
    if not senior:
        raise ValueError("Senior not found")

    checkin = db.create_checkin(
        senior_id=senior_id,
        mood=checkin_data.mood,
        notes=checkin_data.notes,
    )
    return checkin


def add_emergency_contact(senior_id: int,
                          contact_data: EmergencyContact) -> dict:
    senior = db.get_user_by_id(senior_id)
    if not senior:
        raise ValueError("Senior not found")

    contact = db.add_emergency_contact(
        senior_id=senior_id,
        name=contact_data.name,
        phone=contact_data.phone,
        relationship=contact_data.relationship,
    )
    return contact
