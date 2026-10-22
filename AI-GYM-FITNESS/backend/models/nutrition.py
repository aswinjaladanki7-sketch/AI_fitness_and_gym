"""
AI GYM & FITNESS - Nutrition & Diet Model
"""

from ..database.db import query_db, execute_db

def get_active_diet_plan(user_id: int):
    plan = query_db(
        "SELECT * FROM diet_plans WHERE user_id = %s AND is_active = 1 ORDER BY id DESC LIMIT 1",
        (user_id,), one=True
    )
    if not plan:
        return None

    meals = query_db(
        "SELECT * FROM diet_meals WHERE diet_plan_id = %s ORDER BY id ASC",
        (plan['id'],)
    )
    plan['meals'] = meals
    return plan

def save_diet_plan(user_id: int, plan_dict: dict):
    # Deactivate existing plans
    execute_db("UPDATE diet_plans SET is_active = 0 WHERE user_id = %s", (user_id,))

    plan_id, _ = execute_db("""
        INSERT INTO diet_plans (
            user_id, plan_name, diet_type, target_calories, target_protein,
            target_carbs, target_fat, is_active
        ) VALUES (%s, %s, %s, %s, %s, %s, %s, 1)
    """, (
        user_id, f"Personalized {plan_dict['diet_type']} Plan",
        plan_dict['diet_type'].lower().replace("-", "_"),
        plan_dict['total_calories'], plan_dict['total_protein'],
        plan_dict['total_carbs'], plan_dict['total_fat']
    ))

    for meal in plan_dict.get('meals', []):
        execute_db("""
            INSERT INTO diet_meals (
                diet_plan_id, meal_type, food_item_id, food_name, quantity,
                calories, protein_g, carbs_g, fat_g
            ) VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s)
        """, (
            plan_id, meal['meal_type'], meal.get('food_id'),
            meal['food_name'], meal['quantity'], meal['calories'],
            meal['protein_g'], meal['carbs_g'], meal['fat_g']
        ))

    return plan_id
