"""
AI GYM & FITNESS - Authentication Routes
Handles Registration, Login, Logout, and Token Verification.
"""

from flask import Blueprint, request, jsonify
from ..utils.security import hash_password, verify_password, generate_token, verify_token
from ..utils.validators import validate_registration_data
from ..models.user import find_user_by_email, create_user, upsert_user_profile, find_user_by_id, get_user_profile
from ..services.bmi_service import calculate_bmi, calculate_bmr, calculate_tdee, calculate_calorie_and_macros, calculate_progress_projection
from ..models.fitness import save_fitness_goals
from ..models.progress import log_weight
import datetime

auth_bp = Blueprint('auth', __name__)

def get_current_user_id(req):
    auth_header = req.headers.get('Authorization', '')
    if auth_header.startswith('Bearer '):
        token = auth_header.split(' ')[1]
        payload = verify_token(token)
        if payload:
            return payload.get('user_id')
    return None

@auth_bp.route('/register', methods=['POST'])
def register():
    data = request.get_json() or {}
    errors = validate_registration_data(data)
    if errors:
        return jsonify({"success": False, "errors": errors, "message": errors[0]}), 400

    email = data['email'].strip().lower()
    existing = find_user_by_email(email)
    if existing:
        return jsonify({"success": False, "message": "An account with this email already exists."}), 409

    # Hash password & create user
    pwd_hash = hash_password(data['password'])
    user_id = create_user(data['name'], email, pwd_hash)

    # Save profile
    upsert_user_profile(user_id, data)

    # Automatically calculate initial metrics
    weight = float(data['weight_kg'])
    height = float(data['height_cm'])
    age = int(data['age'])
    gender = data['gender']
    activity = data.get('activity_level', 'moderately_active')
    goal = data.get('fitness_goal', 'weight_loss')
    target_weight = float(data.get('target_weight_kg', weight))

    bmi, category, _ = calculate_bmi(weight, height)
    bmr = calculate_bmr(weight, height, age, gender)
    tdee = calculate_tdee(bmr, activity)
    macros = calculate_calorie_and_macros(tdee, weight, goal, gender)
    weeks, _ = calculate_progress_projection(weight, target_weight)

    fitness_record = {
        "bmi": bmi,
        "bmi_category": category,
        "bmr": bmr,
        "tdee": tdee,
        "daily_calories": macros['daily_calories'],
        "protein_g": macros['protein_g'],
        "carbs_g": macros['carbs_g'],
        "fat_g": macros['fat_g'],
        "estimated_weeks": weeks
    }
    save_fitness_goals(user_id, fitness_record)

    # Log initial weight history entry
    log_weight(user_id, weight, bmi, datetime.date.today().isoformat(), "Initial registration baseline")

    token = generate_token(user_id, email)
    return jsonify({
        "success": True,
        "message": "Registration successful! Welcome to AI Gym & Fitness.",
        "token": token,
        "user": {
            "id": user_id,
            "name": data['name'],
            "email": email
        },
        "fitness_goals": fitness_record
    }), 201

@auth_bp.route('/login', methods=['POST'])
def login():
    data = request.get_json() or {}
    email = (data.get('email') or '').strip().lower()
    password = data.get('password') or ''

    if not email or not password:
        return jsonify({"success": False, "message": "Email and password are required."}), 400

    user = find_user_by_email(email)
    if not user or not verify_password(password, user['password_hash']):
        return jsonify({"success": False, "message": "Invalid email or password."}), 401

    token = generate_token(user['id'], user['email'])
    profile = get_user_profile(user['id'])

    return jsonify({
        "success": True,
        "message": "Login successful.",
        "token": token,
        "user": {
            "id": user['id'],
            "name": user['name'],
            "email": user['email']
        },
        "profile": profile
    })

@auth_bp.route('/logout', methods=['POST'])
def logout():
    return jsonify({"success": True, "message": "Logged out successfully."})

@auth_bp.route('/me', methods=['GET'])
def get_me():
    user_id = get_current_user_id(request)
    if not user_id:
        return jsonify({"success": False, "message": "Unauthorized"}), 401

    user = find_user_by_id(user_id)
    profile = get_user_profile(user_id)
    return jsonify({
        "success": True,
        "user": user,
        "profile": profile
    })
