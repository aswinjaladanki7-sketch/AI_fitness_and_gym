"""
AI GYM & FITNESS - Nutrition & Diet Routes
"""

from flask import Blueprint, request, jsonify
from .auth import get_current_user_id
from ..services.nutrition_service import generate_daily_diet, generate_weekly_diet
from ..models.nutrition import get_active_diet_plan, save_diet_plan
from ..models.user import get_user_profile
from ..models.fitness import get_latest_fitness_goals

nutrition_bp = Blueprint('nutrition', __name__)

@nutrition_bp.route('/diet/today', methods=['GET'])
def get_today_diet():
    user_id = get_current_user_id(request) or 1
    plan = get_active_diet_plan(user_id)

    if not plan:
        # Generate automatically from user profile
        profile = get_user_profile(user_id) or {}
        fitness = get_latest_fitness_goals(user_id) or {}

        cals = float(fitness.get('daily_calorie_target') or 2000.0)
        protein = float(fitness.get('protein_grams') or 130.0)
        diet = profile.get('diet_preference') or 'vegetarian'
        allergies = profile.get('food_allergies') or ''

        plan_dict = generate_daily_diet(cals, protein, diet, allergies)
        save_diet_plan(user_id, plan_dict)
        plan = get_active_diet_plan(user_id)

    return jsonify({"success": True, "plan": plan})

@nutrition_bp.route('/diet/generate', methods=['POST'])
def generate_diet():
    user_id = get_current_user_id(request) or 1
    data = request.get_json() or {}

    cals = float(data.get('target_calories', 2000))
    protein = float(data.get('target_protein', 130))
    diet_pref = data.get('diet_preference', 'vegetarian')
    allergies = data.get('allergies', '')
    preferences = data.get('preferences', '')

    plan = generate_daily_diet(cals, protein, diet_pref, allergies, preferences)
    save_diet_plan(user_id, plan)

    return jsonify({
        "success": True,
        "message": f"Personalized {plan['diet_type']} plan generated.",
        "plan": plan
    })

@nutrition_bp.route('/diet/weekly', methods=['POST'])
def generate_weekly():
    data = request.get_json() or {}

    cals = float(data.get('target_calories', 2000))
    protein = float(data.get('target_protein', 130))
    diet_pref = data.get('diet_preference', 'vegetarian')
    allergies = data.get('allergies', '')
    preferences = data.get('preferences', '')

    weekly = generate_weekly_diet(cals, protein, diet_pref, allergies, preferences)

    return jsonify({
        "success": True,
        "weekly_plan": weekly
    })
