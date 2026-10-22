"""
AI GYM & FITNESS - Workout Routes
"""

from flask import Blueprint, request, jsonify
from .auth import get_current_user_id
from ..services.workout_service import generate_workout_routine
from ..models.workout import save_workout_session, get_user_workout_sessions, get_all_exercises
from ..models.user import get_user_profile

workout_bp = Blueprint('workout', __name__)

@workout_bp.route('/workout/today', methods=['GET'])
def get_today_workout():
    user_id = get_current_user_id(request) or 1
    profile = get_user_profile(user_id) or {}

    goal = profile.get('fitness_goal', 'weight_loss')
    level = profile.get('fitness_level', 'beginner')
    location = profile.get('workout_type', 'gym')
    frequency = profile.get('workout_frequency', 4)

    routine = generate_workout_routine(goal, level, location, frequency)
    return jsonify({"success": True, "routine": routine})

@workout_bp.route('/workout/generate', methods=['POST'])
def generate_workout():
    data = request.get_json() or {}

    goal = data.get('goal', 'weight_loss')
    level = data.get('fitness_level', 'beginner')
    location = data.get('location', 'gym')
    frequency = int(data.get('frequency', 4))

    routine = generate_workout_routine(goal, level, location, frequency)
    return jsonify({"success": True, "routine": routine})

@workout_bp.route('/workout/session', methods=['POST'])
def record_session():
    user_id = get_current_user_id(request) or 1
    data = request.get_json() or {}

    session_id = save_workout_session(user_id, data)
    return jsonify({
        "success": True,
        "message": "Workout session recorded successfully!",
        "session_id": session_id
    }), 201

@workout_bp.route('/workout/history', methods=['GET'])
def get_history():
    user_id = get_current_user_id(request) or 1
    sessions = get_user_workout_sessions(user_id)
    return jsonify({"success": True, "sessions": sessions})
