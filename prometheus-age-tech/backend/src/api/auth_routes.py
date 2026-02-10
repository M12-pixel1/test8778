"""Authentication API routes."""

from fastapi import APIRouter, HTTPException, status

from auth import create_access_token
from models import UserRegister, UserLogin, TokenResponse, UserResponse
from services import register_user, authenticate_user

router = APIRouter()


@router.post("/register", response_model=UserResponse,
             status_code=status.HTTP_201_CREATED)
async def register(user_data: UserRegister):
    try:
        user = register_user(user_data)
        return UserResponse(
            id=user["id"],
            username=user["username"],
            email=user["email"],
            full_name=user["full_name"],
            age=user["age"],
            is_senior=bool(user["is_senior"]),
            phone=user["phone"],
            created_at=str(user["created_at"]),
        )
    except ValueError as e:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST, detail=str(e)
        )


@router.post("/login", response_model=TokenResponse)
async def login(login_data: UserLogin):
    user = authenticate_user(login_data)
    if not user:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Invalid credentials",
        )
    token = create_access_token(data={"sub": str(user["id"])})
    return TokenResponse(access_token=token)
