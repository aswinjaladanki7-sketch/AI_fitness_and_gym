"""
AI GYM & FITNESS - Fitness Goals Model
"""

from ..database.db import query_db, execute_db

def get_latest_fitness_goals(user_id: int):
    return query_db(
        "SELECT * FROM fitness_goals WHERE user_id = %s ORDER BY id DESC LIMIT 1",
        (user_id,), one=True
    )

def save_fitness_goals(user_id: int, goals_data: dict):
    goal_id, _ = execute_db("""
        INSERT INTO fitness_goals (
            user_id, bmi, bmi_category, bmr, tdee, daily_calorie_target,
            protein_grams, carbs_grams, fat_grams, estimated_weeks
        ) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
    """, (
        user_id, goals_data['bmi'], goals_data['bmi_category'],
        goals_data['bmr'], goals_data['tdee'], goals_data['daily_calories'],
        goals_data['protein_g'], goals_data['carbs_g'], goals_data['fat_g'],
        goals_data.get('estimated_weeks', 12)
    ))
    return goal_id
