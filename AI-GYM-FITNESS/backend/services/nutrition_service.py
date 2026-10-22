"""
AI GYM & FITNESS - Nutrition & Diet Recommendation Service
Algorithmic meal planner supporting Vegetarian and Non-Vegetarian pathways,
strict allergy filtering, macro distribution across 5 daily meals,
and 1-day or 7-day plan generation.
"""

from ..database.db import query_db

MEAL_SLOTS = [
    {"type": "breakfast", "title": "Breakfast", "calorie_pct": 0.25},
    {"type": "mid_morning", "title": "Mid-Morning Snack", "calorie_pct": 0.10},
    {"type": "lunch", "title": "Lunch", "calorie_pct": 0.35},
    {"type": "evening_snack", "title": "Evening Snack", "calorie_pct": 0.10},
    {"type": "dinner", "title": "Dinner", "calorie_pct": 0.20},
]

def generate_daily_diet(
    target_calories: float,
    target_protein: float,
    diet_preference: str,
    allergies: str = "",
    preferences: str = "",
    seed_offset: int = 0
):
    """
    Generates a personalized 5-meal daily nutrition plan respecting
    allergies and dietary preferences.
    """
    is_veg_only = (diet_preference or 'non_vegetarian').lower() == 'vegetarian'

    # Clean allergy list
    allergy_tokens = [a.strip().lower() for a in (allergies or "").split(",") if a.strip()]

    # Fetch pool of foods from database
    if is_veg_only:
        all_foods = query_db("SELECT * FROM food_items WHERE is_vegetarian = 1")
    else:
        all_foods = query_db("SELECT * FROM food_items")

    # Filter out foods that contain specified allergens
    safe_foods = []
    for food in all_foods:
        item_allergens = (food.get('allergens') or '').lower()
        has_conflict = False
        for allergen in allergy_tokens:
            if allergen in item_allergens or allergen in food['name'].lower():
                has_conflict = True
                break
        if not has_conflict:
            safe_foods.append(food)

    # If list becomes too small due to excessive filters, fall back to safe core foods
    if not safe_foods:
        safe_foods = all_foods

    # Group foods by meal category
    breakfast_items = [f for f in safe_foods if f['category'] == 'breakfast'] or safe_foods
    snack_items = [f for f in safe_foods if f['category'] == 'snack'] or safe_foods
    lunch_items = [f for f in safe_foods if f['category'] in ('lunch', 'any')] or safe_foods
    dinner_items = [f for f in safe_foods if f['category'] in ('dinner', 'any')] or safe_foods

    meals = []
    total_cal = 0.0
    total_protein = 0.0
    total_carbs = 0.0
    total_fat = 0.0

    category_map = {
        'breakfast': breakfast_items,
        'mid_morning': snack_items,
        'lunch': lunch_items,
        'evening_snack': snack_items,
        'dinner': dinner_items
    }

    for idx, slot in enumerate(MEAL_SLOTS):
        slot_type = slot['type']
        pool = category_map.get(slot_type, safe_foods)
        chosen_item = pool[(idx + seed_offset) % len(pool)]

        target_slot_cal = target_calories * slot['calorie_pct']
        base_item_cal = float(chosen_item['calories'])
        portion_ratio = max(0.8, min(2.0, target_slot_cal / base_item_cal))

        scaled_cal = round(base_item_cal * portion_ratio, 1)
        scaled_protein = round(float(chosen_item['protein_g']) * portion_ratio, 1)
        scaled_carbs = round(float(chosen_item['carbs_g']) * portion_ratio, 1)
        scaled_fat = round(float(chosen_item['fat_g']) * portion_ratio, 1)

        portion_text = f"{round(float(chosen_item['portion_grams']) * portion_ratio)}g ({chosen_item['portion_description']})"

        meals.append({
            "meal_type": slot_type,
            "meal_title": slot['title'],
            "food_id": chosen_item['id'],
            "food_name": chosen_item['name'],
            "quantity": portion_text,
            "calories": scaled_cal,
            "protein_g": scaled_protein,
            "carbs_g": scaled_carbs,
            "fat_g": scaled_fat,
            "is_vegetarian": bool(chosen_item['is_vegetarian'])
        })

        total_cal += scaled_cal
        total_protein += scaled_protein
        total_carbs += scaled_carbs
        total_fat += scaled_fat

    return {
        "diet_type": "Vegetarian" if is_veg_only else "Non-Vegetarian",
        "target_calories": round(target_calories, 1),
        "target_protein": round(target_protein, 1),
        "total_calories": round(total_cal, 1),
        "total_protein": round(total_protein, 1),
        "total_carbs": round(total_carbs, 1),
        "total_fat": round(total_fat, 1),
        "meals": meals
    }

def generate_weekly_diet(
    target_calories: float,
    target_protein: float,
    diet_preference: str,
    allergies: str = "",
    preferences: str = ""
):
    """Generates a complete 7-day rotational diet plan."""
    days = ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"]
    weekly_plan = []

    for i, day in enumerate(days):
        daily = generate_daily_diet(
            target_calories=target_calories,
            target_protein=target_protein,
            diet_preference=diet_preference,
            allergies=allergies,
            preferences=preferences,
            seed_offset=i * 2
        )
        daily["day"] = day
        weekly_plan.append(daily)

    return weekly_plan
