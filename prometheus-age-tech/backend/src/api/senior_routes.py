"""Senior-specific API routes."""

from fastapi import APIRouter, Depends, HTTPException, status

from auth import get_current_user
from models import (
    SOSAlert,
    SOSAlertResponse,
    DailyCheckIn,
    DashboardResponse,
    EmergencyContact,
    EmergencyContactResponse,
    UserResponse,
)
from services import (
    get_senior_dashboard,
    trigger_sos_alert,
    daily_check_in,
    add_emergency_contact,
)

router = APIRouter()


@router.get("/{senior_id}/dashboard")
async def dashboard(senior_id: int,
                    current_user: dict = Depends(get_current_user)):
    try:
        data = get_senior_dashboard(senior_id)
        return data
    except ValueError as e:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND, detail=str(e)
        )


@router.post("/sos/alert", status_code=status.HTTP_201_CREATED)
async def sos_alert(alert_data: SOSAlert,
                    current_user: dict = Depends(get_current_user)):
    try:
        alert = trigger_sos_alert(current_user["id"], alert_data)
        return alert
    except ValueError as e:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST, detail=str(e)
        )


@router.post("/{senior_id}/checkin")
async def check_in(senior_id: int, checkin_data: DailyCheckIn,
                   current_user: dict = Depends(get_current_user)):
    try:
        checkin = daily_check_in(senior_id, checkin_data)
        return checkin
    except ValueError as e:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST, detail=str(e)
        )


@router.post("/{senior_id}/contacts",
             status_code=status.HTTP_201_CREATED)
async def create_contact(senior_id: int, contact_data: EmergencyContact,
                         current_user: dict = Depends(get_current_user)):
    try:
        contact = add_emergency_contact(senior_id, contact_data)
        return contact
    except ValueError as e:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST, detail=str(e)
        )
