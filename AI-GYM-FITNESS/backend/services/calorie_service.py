"""
AI GYM & FITNESS - Calorie Expenditure Service
Calculates calories burned during workouts using MET (Metabolic Equivalent of Task).
"""

EXERCISE_METS = {
    'squats': 5.5,
    'push-ups': 6.0,
    'lunges': 5.0,
    'bicep-curls': 4.0,
    'shoulder-press': 4.5,
    'plank': 3.5,
    'functional-circuit': 7.0,
    'default': 5.0
}

def estimate_workout_calories(exercise_slug: str, weight_kg: float, duration_seconds: int, reps: int = 0):
    """
    Calories burned = (MET * 3.5 * weight_kg / 200) * (duration_minutes)
    With bonus dynamic adjustment for repetition volume.
    """
    duration_min = max(duration_seconds / 60.0, 0.5)
    met = EXERCISE_METS.get(exercise_slug.lower(), EXERCISE_METS['default'])

    # Base metabolic burn
    base_burn = (met * 3.5 * weight_kg / 200.0) * duration_min

    # Repetition intensity multiplier (0.15 kcal per rep average)
    rep_burn = reps * 0.18

    total = round(base_burn + (rep_burn * 0.5), 1)
    return max(total, 5.0)
