"""
AI GYM & FITNESS - Fitness Calculation Engine
Handles scientifically grounded calculations for BMI, BMR, TDEE,
Macro distribution, and safe progress projections.
"""

def calculate_bmi(weight_kg: float, height_cm: float):
    """
    Formula: BMI = weight(kg) / height(m)^2
    Categories:
      - Below 18.5: Underweight
      - 18.5 – 24.9: Normal
      - 25 – 29.9: Overweight
      - 30+: Obesity
    """
    height_m = height_cm / 100.0
    if height_m <= 0:
        return 0.0, "Invalid Height"

    bmi = round(weight_kg / (height_m ** 2), 2)

    if bmi < 18.5:
        category = "Underweight"
        color = "#38bdf8"
    elif 18.5 <= bmi <= 24.99:
        category = "Normal"
        color = "#10b981"
    elif 25.0 <= bmi <= 29.99:
        category = "Overweight"
        color = "#f59e0b"
    else:
        category = "Obesity"
        color = "#ef4444"

    return bmi, category, color

def calculate_bmr(weight_kg: float, height_cm: float, age: int, gender: str):
    """
    Mifflin-St Jeor Equation:
      Males:   BMR = 10 * weight + 6.25 * height - 5 * age + 5
      Females: BMR = 10 * weight + 6.25 * height - 5 * age - 161
      Other:   Average of male and female formulas
    """
    base = (10.0 * weight_kg) + (6.25 * height_cm) - (5.0 * age)
    gender_lower = (gender or 'male').lower()

    if gender_lower == 'male':
        bmr = base + 5.0
    elif gender_lower == 'female':
        bmr = base - 161.0
    else:
        bmr = base - 78.0 # midpoint

    return round(bmr, 2)

def calculate_tdee(bmr: float, activity_level: str):
    """
    Total Daily Energy Expenditure multipliers:
      - Sedentary: BMR × 1.2
      - Lightly Active: BMR × 1.375
      - Moderately Active: BMR × 1.55
      - Very Active: BMR × 1.725
      - Extra Active: BMR × 1.9
    """
    multipliers = {
        'sedentary': 1.2,
        'lightly_active': 1.375,
        'moderately_active': 1.55,
        'very_active': 1.725,
        'extra_active': 1.9
    }
    multiplier = multipliers.get(activity_level.lower(), 1.55)
    return round(bmr * multiplier, 2)

def calculate_calorie_and_macros(tdee: float, weight_kg: float, goal: str, gender: str = 'male'):
    """
    Calculates goal-adjusted calories and balanced macronutrients:
    - Weight loss: Safe deficit (~450-500 kcal). Clamped to safe physiological floors.
    - Muscle building: Moderate surplus (+300-350 kcal).
    - General fitness / Strength: Maintenance.
    - Beginner: Maintenance or slight deficit if overweight.
    """
    goal_lower = (goal or 'weight_loss').lower()

    if goal_lower == 'weight_loss':
        raw_target = tdee - 450.0
        # Prevent extreme caloric deficits
        floor = 1500.0 if gender.lower() == 'male' else 1250.0
        daily_calories = max(raw_target, floor)
        protein_per_kg = 2.0  # Preserve lean mass in deficit
    elif goal_lower == 'muscle_building':
        daily_calories = tdee + 350.0
        protein_per_kg = 2.2  # Optimal hyper-trophy synthesis
    elif goal_lower == 'strength':
        daily_calories = tdee + 150.0
        protein_per_kg = 2.0
    else: # general_fitness, beginner
        daily_calories = tdee
        protein_per_kg = 1.6

    daily_calories = round(daily_calories, 2)

    # Protein calculation: protein_per_kg * weight, but not exceeding 35% of calories
    protein_g = round(protein_per_kg * weight_kg, 1)
    protein_calories = protein_g * 4.0

    # Dietary fat: ~25% of total calories (1g fat = 9 kcal)
    fat_calories = daily_calories * 0.25
    fat_g = round(fat_calories / 9.0, 1)

    # Carbohydrates: Remaining calories (1g carb = 4 kcal)
    carb_calories = max(daily_calories - (protein_calories + fat_calories), 200.0)
    carbs_g = round(carb_calories / 4.0, 1)

    return {
        "daily_calories": daily_calories,
        "protein_g": protein_g,
        "carbs_g": carbs_g,
        "fat_g": fat_g
    }

def calculate_progress_projection(current_weight: float, target_weight: float):
    """
    Calculates estimated duration to reach target weight safely:
    Safe rate: 0.5 kg loss or 0.25 kg clean gain per week.
    """
    delta = abs(current_weight - target_weight)
    if delta < 0.2:
        return 0, "Goal already achieved!"

    if current_weight > target_weight:
        # Weight loss
        rate_per_week = 0.5 # kg
        weeks = max(1, round(delta / rate_per_week))
        summary = f"Estimated {weeks} weeks to lose {round(delta, 1)} kg at a healthy pace of ~0.5 kg/week."
    else:
        # Muscle gain
        rate_per_week = 0.3 # kg
        weeks = max(1, round(delta / rate_per_week))
        summary = f"Estimated {weeks} weeks to gain {round(delta, 1)} kg of lean mass at ~0.3 kg/week."

    return weeks, summary

HEALTH_DISCLAIMER = (
    "This application provides general fitness and nutrition estimates for informational purposes only. "
    "It is not a substitute for professional medical, nutritional, or fitness advice. "
    "Users with medical conditions or special health requirements should consult a qualified healthcare professional."
)
