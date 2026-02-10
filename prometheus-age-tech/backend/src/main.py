"""Prometheus AgeTech - FastAPI Entry Point"""

from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

from api.auth_routes import router as auth_router
from api.senior_routes import router as senior_router
from api.user_routes import router as user_router
from database import init_db

app = FastAPI(
    title="Prometheus AgeTech Senior Protection Platform",
    description="Backend API for senior care and protection services",
    version="1.0.0",
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

app.include_router(auth_router, prefix="/auth", tags=["Authentication"])
app.include_router(senior_router, prefix="/seniors", tags=["Seniors"])
app.include_router(user_router, prefix="/users", tags=["Users"])


@app.on_event("startup")
async def startup():
    init_db()


@app.get("/")
async def root():
    return {"message": "Prometheus AgeTech Senior Protection Platform API"}


@app.get("/health")
async def health_check():
    return {"status": "healthy"}
