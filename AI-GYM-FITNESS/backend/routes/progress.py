"""
AI GYM & FITNESS - Progress Tracking Routes
"""

from flask import Blueprint, request, jsonify
from .auth import get_current_user_id
from ..models.progress import get_progress_summary, log_weight, get_weight_history
from ..models.user import get_user_profile
from ..services.bmi_service import calculate_bmi
import datetime

progress_bp = Blueprint('progress', __name__)

@progress_bp.route('/progress', methods=['GET'])
def get_progress():
    user_id = get_current_user_id(request) or 1
    summary = get_progress_summary(user_id)

    if not summary:
        profile = get_user_profile(user_id) or {}
        w = profile.get('weight_kg', 75)
        tw = profile.get('target_weight_kg', 70)
        summary = {
            "start_weight": w,
            "current_weight": w,
            "target_weight": tw,
            "weight_lost": 0.0,
            "weight_remaining": round(abs(w - tw), 2),
            "total_workouts": 0,
            "total_calories_burned": 0.0,
            "overall_avg_form_score": 0.0,
            "history": []
        }

    return jsonify({"success": True, "progress": summary})

@progress_bp.route('/progress/weight', methods=['POST'])
def add_weight_entry():
    user_id = get_current_user_id(request) or 1
    data = request.get_json() or {}

    try:
        weight = float(data.get('weight_kg', 0))
        if weight < 30 or weight > 300:
            return jsonify({"success": False, "message": "Weight must be between 30 and 300 kg."}), 400

        logged_date = data.get('logged_date') or datetime.date.today().isoformat()
        notes = (data.get('notes') or '').strip()

        profile = get_user_profile(user_id)
        height = float(profile.get('height_cm', 175)) if profile else 175.0
        bmi, _, _ = calculate_bmi(weight, height)

        entry_id = log_weight(user_id, weight, bmi, logged_date, notes)

        return jsonify({
            "success": True,
            "message": "Weight entry logged successfully.",
            "entry": {
                "id": entry_id,
                "weight_kg": weight,
                "bmi": bmi,
                "logged_date": logged_date,
                "notes": notes
            }
        }), 201
    except Exception as e:
        return jsonify({"success": False, "message": str(e)}), 400
