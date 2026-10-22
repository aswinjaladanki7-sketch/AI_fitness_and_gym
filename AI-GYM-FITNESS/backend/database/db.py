"""
AI GYM & FITNESS ASSISTANT - Database Engine
SRM INSTITUTE OF SCIENCE AND TECHNOLOGY
Department of Computer Science and Engineering
"""

import os
import sqlite3
import re
from pathlib import Path
from dotenv import load_dotenv

# Load environment
base_dir = Path(__file__).resolve().parent.parent.parent
load_dotenv(base_dir / ".env")

DB_HOST = os.getenv("DB_HOST", "localhost")
DB_PORT = int(os.getenv("DB_PORT", 3306))
DB_USER = os.getenv("DB_USER", "root")
DB_PASSWORD = os.getenv("DB_PASSWORD", "")
DB_NAME = os.getenv("DB_NAME", "ai_gym_fitness")
USE_SQLITE_FALLBACK = os.getenv("USE_SQLITE_FALLBACK", "True").lower() in ("true", "1", "t")
SQLITE_DB_PATH = base_dir / os.getenv("SQLITE_DB_PATH", "ai_gym_fitness.db")

_active_driver = None # 'mysql' or 'sqlite'

def get_connection():
    """
    Attempts to connect to MySQL. If MySQL is unavailable, seamlessly 
    falls back to SQLite.
    """
    global _active_driver

    # Try MySQL first
    try:
        import pymysql
        import pymysql.cursors
        conn = pymysql.connect(
            host=DB_HOST,
            port=DB_PORT,
            user=DB_USER,
            password=DB_PASSWORD,
            database=DB_NAME,
            charset='utf8mb4',
            cursorclass=pymysql.cursors.DictCursor,
            connect_timeout=2
        )
        _active_driver = 'mysql'
        return conn, 'mysql'
    except Exception as e:
        # Fall back to SQLite
        if USE_SQLITE_FALLBACK:
            _active_driver = 'sqlite'
            conn = sqlite3.connect(str(SQLITE_DB_PATH), timeout=10)
            conn.row_factory = sqlite3.Row
            return conn, 'sqlite'
        raise e

def query_db(query, args=(), one=False):
    """Executes a SELECT query and returns list of dictionaries or single dict."""
    conn, driver = get_connection()
    try:
        if driver == 'mysql':
            with conn.cursor() as cursor:
                cursor.execute(query, args)
                result = cursor.fetchall()
                return (result[0] if result else None) if one else result
        else:
            # SQLite query transformation: replace %s with ?
            sqlite_query = query.replace('%s', '?')
            cur = conn.cursor()
            cur.execute(sqlite_query, args)
            rv = cur.fetchall()
            cur.close()
            result = [dict(r) for r in rv]
            return (result[0] if result else None) if one else result
    finally:
        conn.close()

def execute_db(query, args=(), commit=True):
    """Executes an INSERT, UPDATE, or DELETE query and returns lastrowid and rowcount."""
    conn, driver = get_connection()
    try:
        if driver == 'mysql':
            with conn.cursor() as cursor:
                cursor.execute(query, args)
                last_id = cursor.lastrowid
                rowcount = cursor.rowcount
            if commit:
                conn.commit()
            return last_id, rowcount
        else:
            sqlite_query = query.replace('%s', '?')
            cur = conn.cursor()
            cur.execute(sqlite_query, args)
            last_id = cur.lastrowid
            rowcount = cur.rowcount
            if commit:
                conn.commit()
            cur.close()
            return last_id, rowcount
    finally:
        conn.close()

def init_db():
    """Initializes tables and seeds data if running in SQLite or fresh MySQL."""
    conn, driver = get_connection()
    print(f"[*] Database initialized using driver: {driver.upper()}")

    schema_file = base_dir / "database" / "schema.sql"
    seed_file = base_dir / "database" / "seed_data.sql"

    if driver == 'sqlite':
        # Create tables in SQLite if not existing
        cur = conn.cursor()
        cur.executescript("""
        CREATE TABLE IF NOT EXISTS users (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            name TEXT NOT NULL,
            email TEXT NOT NULL UNIQUE,
            password_hash TEXT NOT NULL,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        );

        CREATE TABLE IF NOT EXISTS user_profiles (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            user_id INTEGER NOT NULL UNIQUE,
            age INTEGER NOT NULL,
            gender TEXT NOT NULL,
            height_cm REAL NOT NULL,
            weight_kg REAL NOT NULL,
            target_weight_kg REAL NOT NULL,
            fitness_goal TEXT NOT NULL DEFAULT 'weight_loss',
            workout_type TEXT NOT NULL DEFAULT 'gym',
            workout_frequency INTEGER NOT NULL DEFAULT 4,
            activity_level TEXT NOT NULL DEFAULT 'moderately_active',
            fitness_level TEXT NOT NULL DEFAULT 'beginner',
            diet_preference TEXT NOT NULL DEFAULT 'non_vegetarian',
            food_preferences TEXT,
            food_allergies TEXT,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
        );

        CREATE TABLE IF NOT EXISTS fitness_goals (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            user_id INTEGER NOT NULL,
            bmi REAL NOT NULL,
            bmi_category TEXT NOT NULL,
            bmr REAL NOT NULL,
            tdee REAL NOT NULL,
            daily_calorie_target REAL NOT NULL,
            protein_grams REAL NOT NULL,
            carbs_grams REAL NOT NULL,
            fat_grams REAL NOT NULL,
            target_date TEXT,
            estimated_weeks INTEGER,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
        );

        CREATE TABLE IF NOT EXISTS food_items (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            name TEXT NOT NULL,
            category TEXT NOT NULL,
            is_vegetarian INTEGER NOT NULL DEFAULT 1,
            portion_description TEXT NOT NULL,
            portion_grams REAL NOT NULL DEFAULT 100.0,
            calories REAL NOT NULL,
            protein_g REAL NOT NULL,
            carbs_g REAL NOT NULL,
            fat_g REAL NOT NULL,
            fiber_g REAL DEFAULT 0.0,
            allergens TEXT,
            tags TEXT,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        );

        CREATE TABLE IF NOT EXISTS diet_plans (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            user_id INTEGER NOT NULL,
            plan_name TEXT NOT NULL,
            diet_type TEXT NOT NULL,
            target_calories REAL NOT NULL,
            target_protein REAL NOT NULL,
            target_carbs REAL NOT NULL,
            target_fat REAL NOT NULL,
            plan_day TEXT DEFAULT 'Daily',
            is_active INTEGER DEFAULT 1,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
        );

        CREATE TABLE IF NOT EXISTS diet_meals (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            diet_plan_id INTEGER NOT NULL,
            meal_type TEXT NOT NULL,
            food_item_id INTEGER,
            food_name TEXT NOT NULL,
            quantity TEXT NOT NULL,
            calories REAL NOT NULL,
            protein_g REAL NOT NULL,
            carbs_g REAL NOT NULL,
            fat_g REAL NOT NULL,
            FOREIGN KEY (diet_plan_id) REFERENCES diet_plans(id) ON DELETE CASCADE,
            FOREIGN KEY (food_item_id) REFERENCES food_items(id) ON DELETE SET NULL
        );

        CREATE TABLE IF NOT EXISTS exercises (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            name TEXT NOT NULL,
            slug TEXT NOT NULL UNIQUE,
            target_muscle TEXT NOT NULL,
            secondary_muscles TEXT,
            difficulty TEXT NOT NULL DEFAULT 'beginner',
            location TEXT NOT NULL DEFAULT 'both',
            equipment TEXT DEFAULT 'Bodyweight',
            default_sets INTEGER NOT NULL DEFAULT 3,
            default_reps INTEGER NOT NULL DEFAULT 12,
            default_rest_seconds INTEGER NOT NULL DEFAULT 60,
            has_ai_camera INTEGER NOT NULL DEFAULT 0,
            instructions TEXT NOT NULL,
            form_cues TEXT,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        );

        CREATE TABLE IF NOT EXISTS workout_plans (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            user_id INTEGER NOT NULL,
            title TEXT NOT NULL,
            goal TEXT NOT NULL,
            difficulty TEXT NOT NULL,
            location TEXT NOT NULL,
            days_per_week INTEGER NOT NULL DEFAULT 4,
            is_active INTEGER DEFAULT 1,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
        );

        CREATE TABLE IF NOT EXISTS workout_sessions (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            user_id INTEGER NOT NULL,
            session_title TEXT NOT NULL,
            duration_minutes INTEGER NOT NULL DEFAULT 0,
            total_exercises INTEGER NOT NULL DEFAULT 0,
            total_reps INTEGER NOT NULL DEFAULT 0,
            calories_burned REAL DEFAULT 0.0,
            average_form_score REAL,
            status TEXT DEFAULT 'completed',
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
        );

        CREATE TABLE IF NOT EXISTS ai_workout_sessions (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            user_id INTEGER NOT NULL,
            workout_session_id INTEGER,
            exercise_name TEXT NOT NULL,
            repetitions_completed INTEGER NOT NULL DEFAULT 0,
            valid_reps INTEGER NOT NULL DEFAULT 0,
            invalid_reps INTEGER NOT NULL DEFAULT 0,
            duration_seconds INTEGER NOT NULL DEFAULT 0,
            average_form_score REAL NOT NULL DEFAULT 0.0,
            form_category TEXT NOT NULL DEFAULT 'good',
            calories_burned REAL NOT NULL DEFAULT 0.0,
            feedback_summary TEXT,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
            FOREIGN KEY (workout_session_id) REFERENCES workout_sessions(id) ON DELETE SET NULL
        );

        CREATE TABLE IF NOT EXISTS weight_history (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            user_id INTEGER NOT NULL,
            weight_kg REAL NOT NULL,
            bmi REAL NOT NULL,
            logged_date TEXT NOT NULL,
            notes TEXT,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
        );
        """)
        conn.commit()

        # Seed data if food_items is empty
        cur.execute("SELECT COUNT(*) FROM food_items")
        if cur.fetchone()[0] == 0:
            _seed_sqlite(cur, conn)
        conn.close()

def _seed_sqlite(cur, conn):
    """Populates basic sample foods and exercises for instant zero-config experience."""
    foods = [
        ('Rolled Oats with Almond Milk', 'breakfast', 1, '1 bowl (50g oats + 150ml milk)', 200.0, 240.0, 8.5, 38.0, 5.5, 6.0, 'nuts', 'high_fiber,vegan,quick'),
        ('Paneer Bhurji with 2 Multigrain Roti', 'breakfast', 1, '1 plate (100g paneer + 2 rotis)', 220.0, 380.0, 22.0, 36.0, 16.0, 5.0, 'dairy,gluten', 'high_protein,vegetarian'),
        ('Moong Dal Cheela with Mint Chutney', 'breakfast', 1, '2 pieces (120g)', 150.0, 210.0, 12.0, 28.0, 4.5, 6.5, None, 'high_protein,vegan,gluten_free'),
        ('Greek Yogurt with Chia & Berries', 'breakfast', 1, '1 cup (180g)', 180.0, 165.0, 15.0, 14.0, 4.0, 3.5, 'dairy', 'high_protein,calcium'),
        ('Sprouted Moong & Chana Chaat', 'snack', 1, '1 medium bowl (150g)', 150.0, 185.0, 11.5, 29.0, 2.0, 7.0, None, 'high_fiber,raw,vegan'),
        ('Roasted Makhana (Foxnuts)', 'snack', 1, '1 large bowl (40g)', 40.0, 145.0, 4.0, 25.0, 2.5, 3.0, None, 'low_calorie,vegan,gluten_free'),
        ('Mixed Raw Almonds and Walnuts', 'snack', 1, 'Handful (30g)', 30.0, 190.0, 6.0, 5.0, 17.0, 3.0, 'nuts', 'healthy_fats,keto'),
        ('Peanut Butter on Whole Wheat Toast', 'snack', 1, '2 slices (30g PB)', 110.0, 290.0, 12.0, 30.0, 14.0, 4.5, 'peanuts,gluten', 'energy,protein'),
        ('Yellow Dal Tadka with Brown Rice', 'lunch', 1, '1 bowl dal + 1 cup rice', 300.0, 340.0, 14.0, 58.0, 5.0, 7.0, None, 'staple,comfort,vegan'),
        ('Rajma Masala with Steamed Basmati Rice', 'lunch', 1, '1 bowl rajma + 1 cup rice', 320.0, 390.0, 16.5, 66.0, 6.5, 9.0, None, 'high_fiber,vegan,iron'),
        ('Chole (Chickpea Curry) with 2 Rotis', 'lunch', 1, '1 bowl chole + 2 rotis', 300.0, 410.0, 15.0, 60.0, 10.0, 8.5, 'gluten', 'fiber_rich,filling'),
        ('Soya Chunks Stir-fry with Bell Peppers', 'lunch', 1, '1 bowl (60g soya chunks)', 200.0, 260.0, 31.0, 18.0, 3.5, 8.0, 'soy', 'ultra_high_protein,vegan'),
        ('Palak Paneer with 2 Whole Wheat Rotis', 'dinner', 1, '1 bowl palak paneer + 2 rotis', 280.0, 370.0, 20.0, 32.0, 18.0, 6.0, 'dairy,gluten', 'iron,calcium,protein'),
        ('Tofu Tikka with Grilled Veggies & Quinoa', 'dinner', 1, '1 plate (150g tofu + 100g quinoa)', 300.0, 350.0, 24.0, 35.0, 12.0, 6.5, 'soy', 'vegan,lean_protein'),
        ('Dal Makhani (Light Home Style) with Roti', 'dinner', 1, '1 bowl + 2 rotis', 270.0, 360.0, 13.0, 48.0, 12.0, 7.5, 'dairy,gluten', 'comfort,protein'),
        ('Mixed Vegetable Khichdi with Curd', 'dinner', 1, '1 deep bowl (250g) + 100g curd', 350.0, 320.0, 13.5, 46.0, 7.0, 6.0, 'dairy', 'easy_digestion,light'),
        ('Boiled Egg Whites (4 Eggs)', 'breakfast', 0, '4 large egg whites', 132.0, 68.0, 14.4, 0.8, 0.4, 0.0, 'eggs', 'pure_protein,lean'),
        ('3-Egg Vegetable Omelet with 1 Toast', 'breakfast', 0, '1 whole + 2 whites + veggies + 1 toast', 220.0, 290.0, 21.0, 18.0, 13.0, 2.5, 'eggs,gluten', 'high_protein,keto_friendly'),
        ('Grilled Herb Chicken Breast with Quinoa', 'lunch', 0, '150g chicken + 100g quinoa', 320.0, 420.0, 45.0, 30.0, 9.0, 5.0, None, 'lean_protein,clean_eating'),
        ('Tandoori Chicken Breast with Green Salad', 'lunch', 0, '180g cooked chicken breast', 220.0, 280.0, 48.0, 4.0, 7.5, 2.0, 'dairy', 'high_protein,low_carb'),
        ('Grilled Salmon Fillet with Asparagus', 'dinner', 0, '160g salmon + sautéed asparagus', 240.0, 380.0, 38.0, 5.0, 22.0, 3.0, 'fish', 'omega3,heart_healthy'),
        ('Steamed Fish Curry with Brown Rice', 'dinner', 0, '150g white fish + 1 cup brown rice', 320.0, 370.0, 34.0, 45.0, 6.0, 4.0, 'fish', 'lean,light')
    ]
    cur.executemany("""
        INSERT INTO food_items (name, category, is_vegetarian, portion_description, portion_grams, calories, protein_g, carbs_g, fat_g, fiber_g, allergens, tags)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    """, foods)

    exercises = [
        ('Squats', 'squats', 'Quadriceps / Glutes', 'Hamstrings, Calves, Core', 'beginner', 'both', 'Bodyweight / Dumbbells', 3, 12, 60, 1,
         'Stand tall with feet shoulder-width apart. Push hips back and bend knees until thighs are parallel to floor. Drive through heels back to upright position.',
         'Keep chest high; Do not let knees cave inward; Break parallel at bottom; Keep heels flat on ground'),
        ('Push-ups', 'push-ups', 'Chest (Pectorals)', 'Triceps, Anterior Deltoids, Core', 'beginner', 'both', 'Bodyweight', 3, 10, 60, 1,
         'Place hands slightly wider than shoulder-width. Lower chest until 2 inches above ground. Push forcefully back up maintaining straight spine.',
         'Maintain neutral spine; Avoid sagging lower back; Keep elbows at 45 degree angle; Fully extend arms at top'),
        ('Lunges', 'lunges', 'Quadriceps / Hamstrings', 'Glutes, Calves, Core Stabilizers', 'beginner', 'both', 'Bodyweight / Dumbbells', 3, 12, 60, 1,
         'Step forward with lead leg. Lower hips until both front and back knees form 90-degree angles. Drive through front heel to return to start.',
         'Keep torso upright; Front knee directly above ankle; Do not let front knee pass toes; Maintain balance'),
        ('Bicep Curls', 'bicep-curls', 'Biceps Brachii', 'Brachialis, Forearms', 'beginner', 'both', 'Dumbbells / Resistance Band', 3, 12, 45, 1,
         'Hold weights at thighs. Keep elbows pinned tight to ribcage. Curl upward toward shoulders and lower slowly with control.',
         'Do not swing torso; Keep elbows pinned at sides; Full range of motion; Slow eccentric phase'),
        ('Shoulder Press', 'shoulder-press', 'Deltoids (Shoulders)', 'Triceps, Upper Chest, Trapezius', 'intermediate', 'both', 'Dumbbells / Barbell', 3, 10, 60, 1,
         'Hold weights at shoulder height with elbows at 90 degrees. Press upward vertically overhead until arms extend. Lower back with control.',
         'Do not arch lower back excessively; Press straight overhead; Control descent; Breathe out while pressing')
    ]
    cur.executemany("""
        INSERT INTO exercises (name, slug, target_muscle, secondary_muscles, difficulty, location, equipment, default_sets, default_reps, default_rest_seconds, has_ai_camera, instructions, form_cues)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    """, exercises)

    # Demo user
    cur.execute("""
        INSERT INTO users (id, name, email, password_hash)
        VALUES (1, 'Aswinkumar J', 'aswin@fitness.srmist.edu.in', 'c3ab8ff13720e8ad9047dd39466b3c8974e592f2fa383d4a3960714caef0c4f2$8f142b78')
    """)
    cur.execute("""
        INSERT INTO user_profiles (user_id, age, gender, height_cm, weight_kg, target_weight_kg, fitness_goal, workout_type, workout_frequency, activity_level, fitness_level, diet_preference, food_preferences, food_allergies)
        VALUES (1, 21, 'male', 175.0, 78.0, 70.0, 'weight_loss', 'gym', 5, 'moderately_active', 'intermediate', 'non_vegetarian', 'High protein, chicken, eggs, rice', 'peanuts')
    """)
    cur.execute("""
        INSERT INTO fitness_goals (user_id, bmi, bmi_category, bmr, tdee, daily_calorie_target, protein_grams, carbs_grams, fat_grams, estimated_weeks)
        VALUES (1, 25.47, 'Overweight', 1773.75, 2749.31, 2249.31, 140.0, 260.0, 65.0, 16)
    """)
    cur.execute("""
        INSERT INTO weight_history (user_id, weight_kg, bmi, logged_date, notes)
        VALUES (1, 80.5, 26.28, '2026-09-07', 'Baseline start'),
               (1, 79.2, 25.86, '2026-09-21', 'Phase 1 progress'),
               (1, 78.0, 25.47, '2026-10-05', 'Current weigh-in')
    """)
    cur.execute("""
        INSERT INTO workout_sessions (id, user_id, session_title, duration_minutes, total_exercises, total_reps, calories_burned, average_form_score, status)
        VALUES (1, 1, 'AI Full Body Functional Circuit', 32, 5, 86, 265.0, 89.4, 'completed')
    """)
    conn.commit()
