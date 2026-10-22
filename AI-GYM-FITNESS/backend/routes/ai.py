"""
AI GYM & FITNESS - AI Computer Vision & Analysis Routes
"""

from flask import Blueprint, request, jsonify
from ..ai.exercise_detection import extract_exercise_angles
from ..ai.rep_counter import RepCounter
from ..ai.form_analyzer import analyze_form
from ..models.workout import get_all_exercises

ai_bp = Blueprint('ai', __name__)

# Cache of active session rep counters in memory
_rep_counters = {}

@ai_bp.route('/exercises', methods=['GET'])
def list_exercises():
    exercises = get_all_exercises()
    return jsonify({"success": True, "exercises": exercises})

@ai_bp.route('/ai/analyze', methods=['POST'])
def analyze_pose_frame():
    """
    Real-time pose analysis endpoint.
    Accepts:
      - exercise_name: 'squats' | 'push-ups' | 'lunges' | 'bicep-curls' | 'shoulder-press'
      - landmarks: Array of 33 normalized landmark objects [{x, y, z, visibility}]
      - session_token: optional string identifier for continuous rep state
    """
    data = request.get_json() or {}
    exercise = data.get('exercise_name', 'squats')
    landmarks = data.get('landmarks', [])
    session_id = data.get('session_id', 'default_session')

    if not landmarks or len(landmarks) < 25:
        return jsonify({
            "success": False,
            "message": "Insufficient body landmarks detected. Ensure full body is visible."
        }), 400

    # Retrieve or create session rep counter
    counter_key = f"{session_id}_{exercise}"
    if counter_key not in _rep_counters:
        _rep_counters[counter_key] = RepCounter(exercise)
    counter = _rep_counters[counter_key]

    # Calculate biomechanical joint angles
    angles = extract_exercise_angles(exercise, landmarks)

    # Form analysis
    form_eval = analyze_form(exercise, angles, state=counter.state)

    # Update state machine and repetition counter
    rep_completed, current_reps, new_state, valid_reps = counter.update(
        angles=angles,
        form_valid=(form_eval['status'] != 'error'),
        rep_score=form_eval['score']
    )

    return jsonify({
        "success": True,
        "exercise": exercise,
        "state": new_state,
        "rep_count": current_reps,
        "valid_reps": valid_reps,
        "rep_completed": rep_completed,
        "angles": angles,
        "form_score": form_eval['score'],
        "form_category": form_eval['category'],
        "status": form_eval['status'],
        "feedback": form_eval['feedback'],
        "cues": form_eval['cues']
    })

@ai_bp.route('/ai/reset', methods=['POST'])
def reset_ai_session():
    data = request.get_json() or {}
    session_id = data.get('session_id', 'default_session')
    exercise = data.get('exercise_name', 'squats')
    counter_key = f"{session_id}_{exercise}"
    if counter_key in _rep_counters:
        _rep_counters[counter_key].reset()
    return jsonify({"success": True, "message": "Rep counter reset."})
