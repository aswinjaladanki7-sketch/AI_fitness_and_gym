"""
AI GYM & FITNESS - Input Validators
Ensures user data integrity and sanitary boundaries.
"""

import re

EMAIL_REGEX = re.compile(r"^[\w\.-]+@([\w\.-]+\.)+[\w-]{2,4}$")

VALID_GENDERS = {'male', 'female', 'other'}
VALID_GOALS = {'weight_loss', 'muscle_building', 'strength', 'general_fitness', 'beginner'}
VALID_ACTIVITY = {'sedentary', 'lightly_active', 'moderately_active', 'very_active', 'extra_active'}
VALID_FITNESS_LEVELS = {'beginner', 'intermediate', 'advanced'}
VALID_DIETS = {'vegetarian', 'non_vegetarian'}
VALID_WORKOUT_TYPES = {'gym', 'home', 'hybrid'}

def validate_registration_data(data: dict):
    """Validates multi-step registration inputs."""
    errors = []

    name = (data.get('name') or '').strip()
    if not name or len(name) < 2:
        errors.append("Full Name must be at least 2 characters long.")

    email = (data.get('email') or '').strip().lower()
    if not email or not EMAIL_REGEX.match(email):
        errors.append("A valid email address is required.")

    password = data.get('password') or ''
    if len(password) < 6:
        errors.append("Password must be at least 6 characters long.")

    # Age
    try:
        age = int(data.get('age', 0))
        if age < 12 or age > 100:
            errors.append("Age must be between 12 and 100.")
    except (ValueError, TypeError):
        errors.append("Age must be a valid integer.")

    # Gender
    gender = (data.get('gender') or '').lower()
    if gender not in VALID_GENDERS:
        errors.append(f"Gender must be one of {list(VALID_GENDERS)}.")

    # Height & Weight
    try:
        height = float(data.get('height_cm', 0))
        if height < 90 or height > 250:
            errors.append("Height must be between 90 cm and 250 cm.")
    except (ValueError, TypeError):
        errors.append("Height must be a valid number in cm.")

    try:
        weight = float(data.get('weight_kg', 0))
        if weight < 30 or weight > 300:
            errors.append("Weight must be between 30 kg and 300 kg.")
    except (ValueError, TypeError):
        errors.append("Weight must be a valid number in kg.")

    try:
        target_weight = float(data.get('target_weight_kg', weight if 'weight' in locals() else 0))
        if target_weight < 30 or target_weight > 300:
            errors.append("Target weight must be between 30 kg and 300 kg.")
    except (ValueError, TypeError):
        errors.append("Target weight must be a valid number in kg.")

    # Fitness goal & Activity
    goal = (data.get('fitness_goal') or 'weight_loss').lower()
    if goal not in VALID_GOALS:
        errors.append(f"Fitness goal must be one of {list(VALID_GOALS)}.")

    activity = (data.get('activity_level') or 'moderately_active').lower()
    if activity not in VALID_ACTIVITY:
        errors.append(f"Activity level must be one of {list(VALID_ACTIVITY)}.")

    # Diet preference
    diet = (data.get('diet_preference') or 'non_vegetarian').lower()
    if diet not in VALID_DIETS:
        errors.append("Diet preference must be 'vegetarian' or 'non_vegetarian'.")

    return errors
