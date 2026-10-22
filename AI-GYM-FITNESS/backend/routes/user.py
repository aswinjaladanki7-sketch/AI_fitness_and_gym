"""
AI GYM & FITNESS - User Profile Routes
"""

from flask import Blueprint, request, jsonify
from .auth import get_current_user_id
from ..models.user import get_user_profile, upsert_user_profile, find_user_by_id
from ..models.fitness import get_latest_fitness_goals, save_fitness_goals
from ..services.bmi_service import calculate_bmi, calculate_bmr, calculate_tdee, calculate_calorie_and_macros, calculate_progress_projection

user_bp = Blueprint('user', __name__)

@user_bp.route('/profile', methods=['GET'])
def get_profile():
    user_id = get_current_user_id(request)
    if not user_id:
        # Default demo profile if unauthorized for easy previewing
        user_id = 1

    user = find_user_by_id(user_id)
    profile = get_user_profile(user_id)
    fitness = get_latest_fitness_goals(user_id)

    return jsonify({
        "success": True,
        "user": user,
        "profile": profile,
        "fitness_goals": fitness
    })

@user_bp.route('/profile', methods=['PUT'])
def update_profile():
    user_id = get_current_user_id(request) or 1
    data = request.get_json() or {}

    upsert_user_profile(user_id, data)

    # Recalculate fitness goals if body stats changed
    try:
        weight = float(data.get('weight_kg', 75))
        height = float(data.get('height_cm', 175))
        age = int(data.get('age', 25))
        gender = data.get('gender', 'male')
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
    except Exception as e:
        fitness_record = None

    return jsonify({
        "success": True,
        "message": "Profile updated successfully.",
        "profile": get_user_profile(user_id),
        "fitness_goals": fitness_record
    })
