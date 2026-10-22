"""
AI GYM & FITNESS - Security Utilities
Password hashing and token management.
"""

import hashlib
import os
import time
import json
import base64
import hmac

SECRET_KEY = os.getenv("SECRET_KEY", "ai-gym-fitness-secret-key-srmist-21csc203p")
JWT_SECRET = os.getenv("JWT_SECRET", "ai-gym-jwt-super-secret-key-2026")

def hash_password(password: str) -> str:
    """Hashes a password with a random salt using SHA-256."""
    salt = os.urandom(16).hex()
    salted = f"{password}{salt}".encode('utf-8')
    digest = hashlib.sha256(salted).hexdigest()
    return f"{digest}${salt}"

def verify_password(password: str, hashed_str: str) -> bool:
    """Verifies a plain password against the stored digest$salt."""
    try:
        parts = hashed_str.split("$")
        if len(parts) != 2:
            return False
        stored_digest, salt = parts
        computed = hashlib.sha256(f"{password}{salt}".encode('utf-8')).hexdigest()
        return hmac.compare_digest(stored_digest, computed)
    except Exception:
        return False

def generate_token(user_id: int, email: str, expiry_hours: int = 48) -> str:
    """Generates a lightweight HMAC-SHA256 signed bearer token."""
    header = {"alg": "HS256", "typ": "JWT"}
    payload = {
        "user_id": user_id,
        "email": email,
        "exp": int(time.time()) + (expiry_hours * 3600),
        "iat": int(time.time())
    }

    def b64url(data: bytes) -> str:
        return base64.urlsafe_b64encode(data).rstrip(b'=').decode('utf-8')

    header_b64 = b64url(json.dumps(header).encode('utf-8'))
    payload_b64 = b64url(json.dumps(payload).encode('utf-8'))

    signature_input = f"{header_b64}.{payload_b64}".encode('utf-8')
    signature = hmac.new(JWT_SECRET.encode('utf-8'), signature_input, hashlib.sha256).digest()
    sig_b64 = b64url(signature)

    return f"{header_b64}.{payload_b64}.{sig_b64}"

def verify_token(token: str):
    """Verifies HMAC-SHA256 token and returns payload if valid, otherwise None."""
    try:
        parts = token.split(".")
        if len(parts) != 3:
            return None
        header_b64, payload_b64, sig_b64 = parts

        signature_input = f"{header_b64}.{payload_b64}".encode('utf-8')
        expected_sig = hmac.new(JWT_SECRET.encode('utf-8'), signature_input, hashlib.sha256).digest()

        # Add base64 padding back
        rem = len(sig_b64) % 4
        padded_sig = sig_b64 + ('=' * (4 - rem) if rem else '')
        actual_sig = base64.urlsafe_b64decode(padded_sig.encode('utf-8'))

        if not hmac.compare_digest(expected_sig, actual_sig):
            return None

        rem = len(payload_b64) % 4
        padded_payload = payload_b64 + ('=' * (4 - rem) if rem else '')
        payload_data = json.loads(base64.urlsafe_b64decode(padded_payload.encode('utf-8')).decode('utf-8'))

        if payload_data.get("exp", 0) < time.time():
            return None

        return payload_data
    except Exception:
        return None
