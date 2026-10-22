"""
AI GYM & FITNESS - Fitness Calculation Routes
"""

from flask import Blueprint, request, jsonify
from ..services.bmi_service import (
    calculate_bmi, calculate_bmr, calculate_tdee,
    calculate_calorie_and_macros, calculate_progress_projection,
    HEALTH_DISCLAIMER
)

fitness_bp = Blueprint('fitness', __name__)

@fitness_bp.route('/calculate', methods=['POST'])
def calculate_fitness():
    data = request.get_json() or {}

    try:
        weight = float(data.get('weight_kg', 70))
        height = float(data.get('height_cm', 170))
        age = int(data.get('age', 25))
        gender = (data.get('gender') or 'male').lower()
        activity = (data.get('activity_level') or 'moderately_active').lower()
        goal = (data.get('fitness_goal') or 'weight_loss').lower()
        target_weight = float(data.get('target_weight_kg', weight))

        bmi, category, color = calculate_bmi(weight, height)
        bmr = calculate_bmr(weight, height, age, gender)
        tdee = calculate_tdee(bmr, activity)
        macros = calculate_calorie_and_macros(tdee, weight, goal, gender)
        weeks, projection_text = calculate_progress_projection(weight, target_weight)

        return jsonify({
            "success": True,
            "data": {
                "bmi": bmi,
                "bmi_category": category,
                "bmi_color": color,
                "bmr": bmr,
                "tdee": tdee,
                "daily_calories": macros['daily_calories'],
                "protein_g": macros['protein_g'],
                "carbs_g": macros['carbs_g'],
                "fat_g": macros['fat_g'],
                "current_weight_kg": weight,
                "target_weight_kg": target_weight,
                "estimated_weeks": weeks,
                "projection_summary": projection_text,
                "health_disclaimer": HEALTH_DISCLAIMER
            }
        })
    except Exception as e:
        return jsonify({"success": False, "message": f"Calculation error: {str(e)}"}), 400
