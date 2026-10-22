"""
AI GYM & FITNESS - Workout Model
"""

from ..database.db import query_db, execute_db

def get_all_exercises():
    return query_db("SELECT * FROM exercises ORDER BY has_ai_camera DESC, name ASC")

def get_exercise_by_slug(slug: str):
    return query_db("SELECT * FROM exercises WHERE slug = %s", (slug,), one=True)

def save_workout_session(user_id: int, session_data: dict):
    session_id, _ = execute_db("""
        INSERT INTO workout_sessions (
            user_id, session_title, duration_minutes, total_exercises,
            total_reps, calories_burned, average_form_score, status
        ) VALUES (%s, %s, %s, %s, %s, %s, %s, 'completed')
    """, (
        user_id, session_data.get('session_title', 'AI Workout Session'),
        session_data.get('duration_minutes', 0), session_data.get('total_exercises', 1),
        session_data.get('total_reps', 0), session_data.get('calories_burned', 0.0),
        session_data.get('average_form_score', 0.0)
    ))

    # Also log exercise details if provided
    ai_details = session_data.get('ai_session')
    if ai_details:
        execute_db("""
            INSERT INTO ai_workout_sessions (
                user_id, workout_session_id, exercise_name, repetitions_completed,
                valid_reps, invalid_reps, duration_seconds, average_form_score,
                form_category, calories_burned, feedback_summary
            ) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
        """, (
            user_id, session_id, ai_details.get('exercise_name', 'Exercise'),
            ai_details.get('repetitions', 0), ai_details.get('valid_reps', 0),
            ai_details.get('invalid_reps', 0), ai_details.get('duration_seconds', 0),
            ai_details.get('form_score', 0.0), ai_details.get('form_category', 'good'),
            ai_details.get('calories_burned', 0.0), ai_details.get('feedback', '')
        ))

    return session_id

def get_user_workout_sessions(user_id: int, limit: int = 10):
    return query_db("""
        SELECT ws.*, ai.exercise_name, ai.valid_reps, ai.form_category
        FROM workout_sessions ws
        LEFT JOIN ai_workout_sessions ai ON ai.workout_session_id = ws.id
        WHERE ws.user_id = %s
        ORDER BY ws.id DESC LIMIT %s
    """, (user_id, limit))
