"""
AI GYM & FITNESS - Progress Tracking Model
"""

from ..database.db import query_db, execute_db

def get_weight_history(user_id: int):
    return query_db(
        "SELECT * FROM weight_history WHERE user_id = %s ORDER BY logged_date ASC",
        (user_id,)
    )

def log_weight(user_id: int, weight_kg: float, bmi: float, logged_date: str, notes: str = ""):
    entry_id, _ = execute_db("""
        INSERT INTO weight_history (user_id, weight_kg, bmi, logged_date, notes)
        VALUES (%s, %s, %s, %s, %s)
    """, (user_id, weight_kg, bmi, logged_date, notes))

    # Update profile weight
    execute_db("UPDATE user_profiles SET weight_kg = %s, updated_at = CURRENT_TIMESTAMP WHERE user_id = %s", (weight_kg, user_id))
    return entry_id

def get_progress_summary(user_id: int):
    profile = query_db("SELECT * FROM user_profiles WHERE user_id = %s", (user_id,), one=True)
    if not profile:
        return None

    history = get_weight_history(user_id)
    start_weight = history[0]['weight_kg'] if history else profile['weight_kg']
    current_weight = profile['weight_kg']
    target_weight = profile['target_weight_kg']

    weight_lost = round(start_weight - current_weight, 2)
    weight_remaining = round(abs(current_weight - target_weight), 2)

    total_sessions = query_db(
        "SELECT COUNT(*) as count, SUM(calories_burned) as total_cals, AVG(average_form_score) as avg_score FROM workout_sessions WHERE user_id = %s",
        (user_id,), one=True
    )

    return {
        "start_weight": start_weight,
        "current_weight": current_weight,
        "target_weight": target_weight,
        "weight_lost": weight_lost,
        "weight_remaining": weight_remaining,
        "total_workouts": (total_sessions.get('count') or 0) if total_sessions else 0,
        "total_calories_burned": round(float(total_sessions.get('total_cals') or 0), 1) if total_sessions else 0.0,
        "overall_avg_form_score": round(float(total_sessions.get('avg_score') or 0), 1) if total_sessions else 0.0,
        "history": history
    }
