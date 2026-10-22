"""
AI GYM & FITNESS - User & Profile Model
"""

from ..database.db import query_db, execute_db

def find_user_by_email(email: str):
    return query_db("SELECT * FROM users WHERE email = %s", (email.strip().lower(),), one=True)

def find_user_by_id(user_id: int):
    return query_db("SELECT id, name, email, created_at FROM users WHERE id = %s", (user_id,), one=True)

def create_user(name: str, email: str, password_hash: str):
    user_id, _ = execute_db(
        "INSERT INTO users (name, email, password_hash) VALUES (%s, %s, %s)",
        (name.strip(), email.strip().lower(), password_hash)
    )
    return user_id

def get_user_profile(user_id: int):
    return query_db("SELECT * FROM user_profiles WHERE user_id = %s", (user_id,), one=True)

def upsert_user_profile(user_id: int, profile_data: dict):
    existing = get_user_profile(user_id)
    if existing:
        execute_db("""
            UPDATE user_profiles SET
                age = %s, gender = %s, height_cm = %s, weight_kg = %s,
                target_weight_kg = %s, fitness_goal = %s, workout_type = %s,
                workout_frequency = %s, activity_level = %s, fitness_level = %s,
                diet_preference = %s, food_preferences = %s, food_allergies = %s,
                updated_at = CURRENT_TIMESTAMP
            WHERE user_id = %s
        """, (
            profile_data.get('age'), profile_data.get('gender'),
            profile_data.get('height_cm'), profile_data.get('weight_kg'),
            profile_data.get('target_weight_kg'), profile_data.get('fitness_goal'),
            profile_data.get('workout_type'), profile_data.get('workout_frequency'),
            profile_data.get('activity_level'), profile_data.get('fitness_level'),
            profile_data.get('diet_preference'), profile_data.get('food_preferences'),
            profile_data.get('food_allergies'), user_id
        ))
    else:
        execute_db("""
            INSERT INTO user_profiles (
                user_id, age, gender, height_cm, weight_kg, target_weight_kg,
                fitness_goal, workout_type, workout_frequency, activity_level,
                fitness_level, diet_preference, food_preferences, food_allergies
            ) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
        """, (
            user_id, profile_data.get('age'), profile_data.get('gender'),
            profile_data.get('height_cm'), profile_data.get('weight_kg'),
            profile_data.get('target_weight_kg'), profile_data.get('fitness_goal'),
            profile_data.get('workout_type'), profile_data.get('workout_frequency'),
            profile_data.get('activity_level'), profile_data.get('fitness_level'),
            profile_data.get('diet_preference'), profile_data.get('food_preferences'),
            profile_data.get('food_allergies')
        ))
