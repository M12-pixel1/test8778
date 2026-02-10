"""SQLite database layer."""

import sqlite3
from contextlib import contextmanager
from datetime import datetime
from typing import Optional

DATABASE_PATH = "prometheus_agetech.db"


def get_connection():
    conn = sqlite3.connect(DATABASE_PATH)
    conn.row_factory = sqlite3.Row
    return conn


@contextmanager
def get_db():
    conn = get_connection()
    try:
        yield conn
        conn.commit()
    except Exception:
        conn.rollback()
        raise
    finally:
        conn.close()


def init_db():
    with get_db() as conn:
        cursor = conn.cursor()
        cursor.executescript("""
            CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT UNIQUE NOT NULL,
                email TEXT UNIQUE NOT NULL,
                password_hash TEXT NOT NULL,
                full_name TEXT NOT NULL,
                age INTEGER NOT NULL,
                is_senior BOOLEAN DEFAULT 0,
                phone TEXT,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            );

            CREATE TABLE IF NOT EXISTS emergency_contacts (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                senior_id INTEGER NOT NULL,
                name TEXT NOT NULL,
                phone TEXT NOT NULL,
                relationship TEXT NOT NULL,
                FOREIGN KEY (senior_id) REFERENCES users(id)
            );

            CREATE TABLE IF NOT EXISTS sos_alerts (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                senior_id INTEGER NOT NULL,
                latitude REAL,
                longitude REAL,
                message TEXT,
                status TEXT DEFAULT 'active',
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (senior_id) REFERENCES users(id)
            );

            CREATE TABLE IF NOT EXISTS daily_checkins (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                senior_id INTEGER NOT NULL,
                mood TEXT NOT NULL,
                notes TEXT,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (senior_id) REFERENCES users(id)
            );
        """)


def create_user(username: str, email: str, password_hash: str,
                full_name: str, age: int, is_senior: bool,
                phone: Optional[str] = None) -> dict:
    with get_db() as conn:
        cursor = conn.cursor()
        cursor.execute(
            """INSERT INTO users (username, email, password_hash, full_name,
               age, is_senior, phone) VALUES (?, ?, ?, ?, ?, ?, ?)""",
            (username, email, password_hash, full_name, age, is_senior, phone),
        )
        user_id = cursor.lastrowid
    return get_user_by_id(user_id)


def get_user_by_username(username: str) -> Optional[dict]:
    with get_db() as conn:
        cursor = conn.cursor()
        cursor.execute("SELECT * FROM users WHERE username = ?", (username,))
        row = cursor.fetchone()
        return dict(row) if row else None


def get_user_by_id(user_id: int) -> Optional[dict]:
    with get_db() as conn:
        cursor = conn.cursor()
        cursor.execute("SELECT * FROM users WHERE id = ?", (user_id,))
        row = cursor.fetchone()
        return dict(row) if row else None


def add_emergency_contact(senior_id: int, name: str, phone: str,
                          relationship: str) -> dict:
    with get_db() as conn:
        cursor = conn.cursor()
        cursor.execute(
            """INSERT INTO emergency_contacts (senior_id, name, phone,
               relationship) VALUES (?, ?, ?, ?)""",
            (senior_id, name, phone, relationship),
        )
        contact_id = cursor.lastrowid
        cursor.execute(
            "SELECT * FROM emergency_contacts WHERE id = ?", (contact_id,)
        )
        return dict(cursor.fetchone())


def get_emergency_contacts(senior_id: int) -> list:
    with get_db() as conn:
        cursor = conn.cursor()
        cursor.execute(
            "SELECT * FROM emergency_contacts WHERE senior_id = ?",
            (senior_id,),
        )
        return [dict(row) for row in cursor.fetchall()]


def create_sos_alert(senior_id: int, latitude: Optional[float],
                     longitude: Optional[float],
                     message: str) -> dict:
    with get_db() as conn:
        cursor = conn.cursor()
        cursor.execute(
            """INSERT INTO sos_alerts (senior_id, latitude, longitude, message)
               VALUES (?, ?, ?, ?)""",
            (senior_id, latitude, longitude, message),
        )
        alert_id = cursor.lastrowid
        cursor.execute(
            "SELECT * FROM sos_alerts WHERE id = ?", (alert_id,)
        )
        return dict(cursor.fetchone())


def get_sos_alerts(senior_id: int, limit: int = 10) -> list:
    with get_db() as conn:
        cursor = conn.cursor()
        cursor.execute(
            """SELECT * FROM sos_alerts WHERE senior_id = ?
               ORDER BY created_at DESC LIMIT ?""",
            (senior_id, limit),
        )
        return [dict(row) for row in cursor.fetchall()]


def create_checkin(senior_id: int, mood: str,
                   notes: Optional[str] = None) -> dict:
    with get_db() as conn:
        cursor = conn.cursor()
        cursor.execute(
            """INSERT INTO daily_checkins (senior_id, mood, notes)
               VALUES (?, ?, ?)""",
            (senior_id, mood, notes),
        )
        checkin_id = cursor.lastrowid
        cursor.execute(
            "SELECT * FROM daily_checkins WHERE id = ?", (checkin_id,)
        )
        return dict(cursor.fetchone())


def get_last_checkin(senior_id: int) -> Optional[dict]:
    with get_db() as conn:
        cursor = conn.cursor()
        cursor.execute(
            """SELECT * FROM daily_checkins WHERE senior_id = ?
               ORDER BY created_at DESC LIMIT 1""",
            (senior_id,),
        )
        row = cursor.fetchone()
        return dict(row) if row else None
