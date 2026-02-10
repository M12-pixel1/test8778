"""User management API routes."""

from fastapi import APIRouter, Depends, HTTPException, status

from auth import get_current_user
from models import UserResponse

router = APIRouter()


@router.get("/me", response_model=UserResponse)
async def get_current_user_profile(
    current_user: dict = Depends(get_current_user),
):
    return UserResponse(
        id=current_user["id"],
        username=current_user["username"],
        email=current_user["email"],
        full_name=current_user["full_name"],
        age=current_user["age"],
        is_senior=bool(current_user["is_senior"]),
        phone=current_user["phone"],
        created_at=str(current_user["created_at"]),
    )
