"""
AI GYM & FITNESS - Workout Planner Service
Generates targeted workout routines based on fitness goal, experience level,
and training location (Gym vs Home).
"""

from ..database.db import query_db

GOAL_SPLIT_MAP = {
    'weight_loss': {
        'title': 'High-Intensity Metabolic & Full Body Circuit',
        'focus': 'Calorie burn, core stability & lean muscle retention',
        'rest_factor': 0.8,
        'rep_range': (12, 15),
        'days': 4
    },
    'muscle_building': {
        'title': 'Hypertrophy Upper / Lower Muscle Builder',
        'focus': 'Progressive overload, time under tension & muscle volume',
        'rest_factor': 1.2,
        'rep_range': (8, 12),
        'days': 5
    },
    'strength': {
        'title': 'Pure Strength & Neural Drive Protocol',
        'focus': 'Compound multi-joint power, posture & motor unit recruitment',
        'rest_factor': 1.5,
        'rep_range': (5, 8),
        'days': 4
    },
    'beginner': {
        'title': 'Foundational Movement Mastery & Mobility',
        'focus': 'Form correction, movement patterns & joint health',
        'rest_factor': 1.0,
        'rep_range': (10, 12),
        'days': 3
    },
    'general_fitness': {
        'title': 'Balanced Vitality & Cardiovascular Conditioning',
        'focus': 'Stamina, core strength & functional longevity',
        'rest_factor': 1.0,
        'rep_range': (10, 14),
        'days': 3
    }
}

def generate_workout_routine(goal: str, fitness_level: str, location: str, workout_frequency: int = 4):
    """
    Assembles a tailored exercise routine matching goal parameters and venue.
    """
    clean_goal = (goal or 'weight_loss').lower()
    clean_location = (location or 'both').lower()
    split_info = GOAL_SPLIT_MAP.get(clean_goal, GOAL_SPLIT_MAP['general_fitness'])

    # Fetch exercises matching location
    if clean_location in ('gym', 'home'):
        exercises = query_db(
            "SELECT * FROM exercises WHERE location IN (%s, 'both') ORDER BY has_ai_camera DESC, id ASC",
            (clean_location,)
        )
    else:
        exercises = query_db("SELECT * FROM exercises ORDER BY has_ai_camera DESC, id ASC")

    if not exercises:
        exercises = query_db("SELECT * FROM exercises")

    routine_exercises = []
    min_reps, max_reps = split_info['rep_range']

    for ex in exercises:
        # Scale sets and rest by goal
        sets = 3 if fitness_level == 'beginner' else 4
        rest_sec = int(ex['default_rest_seconds'] * split_info['rest_factor'])

        routine_exercises.append({
            "id": ex['id'],
            "name": ex['name'],
            "slug": ex['slug'],
            "target_muscle": ex['target_muscle'],
            "secondary_muscles": ex.get('secondary_muscles') or '',
            "difficulty": ex['difficulty'],
            "location": ex['location'],
            "equipment": ex['equipment'],
            "sets": sets,
            "reps": f"{min_reps}-{max_reps}",
            "rest_seconds": rest_sec,
            "has_ai_camera": bool(ex['has_ai_camera']),
            "instructions": ex['instructions'],
            "form_cues": ex.get('form_cues') or ''
        })

    return {
        "title": split_info['title'],
        "goal": clean_goal,
        "focus": split_info['focus'],
        "location": clean_location.capitalize(),
        "frequency_days_per_week": workout_frequency or split_info['days'],
        "estimated_duration_min": 45 if clean_goal == 'beginner' else 55,
        "total_exercises": len(routine_exercises),
        "exercises": routine_exercises
    }
