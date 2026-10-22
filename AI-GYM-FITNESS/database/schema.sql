-- ============================================================
-- AI GYM & FITNESS ASSISTANT
-- Database Schema (MySQL 8.0+)
-- SRM INSTITUTE OF SCIENCE AND TECHNOLOGY
-- Department of Computer Science and Engineering
-- Course: 21CSC203P - PROJECT
-- ============================================================

CREATE DATABASE IF NOT EXISTS ai_gym_fitness;
USE ai_gym_fitness;

-- Disable foreign key checks during initialization
SET FOREIGN_KEY_CHECKS = 0;

-- ------------------------------------------------------------
-- 1. USERS TABLE
-- ------------------------------------------------------------
DROP TABLE IF EXISTS users;
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(120) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
-- 2. USER PROFILES TABLE
-- ------------------------------------------------------------
DROP TABLE IF EXISTS user_profiles;
CREATE TABLE user_profiles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL UNIQUE,
    age INT NOT NULL,
    gender ENUM('male', 'female', 'other') NOT NULL,
    height_cm DECIMAL(5,2) NOT NULL,
    weight_kg DECIMAL(5,2) NOT NULL,
    target_weight_kg DECIMAL(5,2) NOT NULL,
    fitness_goal ENUM('weight_loss', 'muscle_building', 'strength', 'general_fitness', 'beginner') NOT NULL DEFAULT 'weight_loss',
    workout_type ENUM('gym', 'home', 'hybrid') NOT NULL DEFAULT 'gym',
    workout_frequency INT NOT NULL DEFAULT 4, -- days per week
    activity_level ENUM('sedentary', 'lightly_active', 'moderately_active', 'very_active', 'extra_active') NOT NULL DEFAULT 'moderately_active',
    fitness_level ENUM('beginner', 'intermediate', 'advanced') NOT NULL DEFAULT 'beginner',
    diet_preference ENUM('vegetarian', 'non_vegetarian') NOT NULL DEFAULT 'non_vegetarian',
    food_preferences TEXT NULL,
    food_allergies TEXT NULL, -- comma separated, e.g. "peanuts,dairy"
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_profile_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_profile_goal (fitness_goal),
    INDEX idx_profile_diet (diet_preference)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
-- 3. FITNESS CALCULATIONS & GOALS TABLE
-- ------------------------------------------------------------
DROP TABLE IF EXISTS fitness_goals;
CREATE TABLE fitness_goals (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    bmi DECIMAL(4,2) NOT NULL,
    bmi_category VARCHAR(50) NOT NULL,
    bmr DECIMAL(7,2) NOT NULL,
    tdee DECIMAL(7,2) NOT NULL,
    daily_calorie_target DECIMAL(7,2) NOT NULL,
    protein_grams DECIMAL(6,2) NOT NULL,
    carbs_grams DECIMAL(6,2) NOT NULL,
    fat_grams DECIMAL(6,2) NOT NULL,
    target_date DATE NULL,
    estimated_weeks INT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_goals_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_fitness_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
-- 4. FOOD ITEMS LIBRARY TABLE
-- ------------------------------------------------------------
DROP TABLE IF EXISTS food_items;
CREATE TABLE food_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    category ENUM('breakfast', 'lunch', 'dinner', 'snack', 'any') NOT NULL DEFAULT 'any',
    is_vegetarian BOOLEAN NOT NULL DEFAULT TRUE,
    portion_description VARCHAR(100) NOT NULL,
    portion_grams DECIMAL(6,2) NOT NULL DEFAULT 100.0,
    calories DECIMAL(6,2) NOT NULL,
    protein_g DECIMAL(5,2) NOT NULL,
    carbs_g DECIMAL(5,2) NOT NULL,
    fat_g DECIMAL(5,2) NOT NULL,
    fiber_g DECIMAL(5,2) DEFAULT 0.0,
    allergens VARCHAR(200) NULL, -- comma separated: 'dairy', 'gluten', 'peanuts', etc.
    tags VARCHAR(200) NULL, -- e.g. 'high_protein,vegan,quick'
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_food_veg (is_vegetarian),
    INDEX idx_food_cat (category)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
-- 5. DIET PLANS TABLE
-- ------------------------------------------------------------
DROP TABLE IF EXISTS diet_plans;
CREATE TABLE diet_plans (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    plan_name VARCHAR(150) NOT NULL,
    diet_type ENUM('vegetarian', 'non_vegetarian') NOT NULL,
    target_calories DECIMAL(7,2) NOT NULL,
    target_protein DECIMAL(6,2) NOT NULL,
    target_carbs DECIMAL(6,2) NOT NULL,
    target_fat DECIMAL(6,2) NOT NULL,
    plan_day VARCHAR(20) DEFAULT 'Daily', -- e.g. 'Monday' or 'Daily'
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_diet_plan_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_diet_plan_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
-- 6. DIET MEALS TABLE (Items inside a diet plan)
-- ------------------------------------------------------------
DROP TABLE IF EXISTS diet_meals;
CREATE TABLE diet_meals (
    id INT AUTO_INCREMENT PRIMARY KEY,
    diet_plan_id INT NOT NULL,
    meal_type ENUM('breakfast', 'mid_morning', 'lunch', 'evening_snack', 'dinner') NOT NULL,
    food_item_id INT NULL,
    food_name VARCHAR(150) NOT NULL,
    quantity VARCHAR(100) NOT NULL,
    calories DECIMAL(6,2) NOT NULL,
    protein_g DECIMAL(5,2) NOT NULL,
    carbs_g DECIMAL(5,2) NOT NULL,
    fat_g DECIMAL(5,2) NOT NULL,
    CONSTRAINT fk_meal_plan FOREIGN KEY (diet_plan_id) REFERENCES diet_plans(id) ON DELETE CASCADE,
    CONSTRAINT fk_meal_food FOREIGN KEY (food_item_id) REFERENCES food_items(id) ON DELETE SET NULL,
    INDEX idx_meal_plan (diet_plan_id),
    INDEX idx_meal_type (meal_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
-- 7. EXERCISES LIBRARY TABLE
-- ------------------------------------------------------------
DROP TABLE IF EXISTS exercises;
CREATE TABLE exercises (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    slug VARCHAR(80) NOT NULL UNIQUE,
    target_muscle VARCHAR(100) NOT NULL,
    secondary_muscles VARCHAR(150) NULL,
    difficulty ENUM('beginner', 'intermediate', 'advanced') NOT NULL DEFAULT 'beginner',
    location ENUM('gym', 'home', 'both') NOT NULL DEFAULT 'both',
    equipment VARCHAR(100) DEFAULT 'Bodyweight',
    default_sets INT NOT NULL DEFAULT 3,
    default_reps INT NOT NULL DEFAULT 12,
    default_rest_seconds INT NOT NULL DEFAULT 60,
    has_ai_camera BOOLEAN NOT NULL DEFAULT FALSE,
    instructions TEXT NOT NULL,
    form_cues TEXT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_exercise_slug (slug),
    INDEX idx_exercise_ai (has_ai_camera)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
-- 8. WORKOUT PLANS TABLE
-- ------------------------------------------------------------
DROP TABLE IF EXISTS workout_plans;
CREATE TABLE workout_plans (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    title VARCHAR(150) NOT NULL,
    goal VARCHAR(80) NOT NULL,
    difficulty VARCHAR(50) NOT NULL,
    location VARCHAR(50) NOT NULL,
    days_per_week INT NOT NULL DEFAULT 4,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_workout_plan_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_workout_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
-- 9. WORKOUT SESSIONS TABLE
-- ------------------------------------------------------------
DROP TABLE IF EXISTS workout_sessions;
CREATE TABLE workout_sessions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    session_title VARCHAR(150) NOT NULL,
    duration_minutes INT NOT NULL DEFAULT 0,
    total_exercises INT NOT NULL DEFAULT 0,
    total_reps INT NOT NULL DEFAULT 0,
    calories_burned DECIMAL(6,2) DEFAULT 0.0,
    average_form_score DECIMAL(5,2) NULL,
    status ENUM('completed', 'in_progress', 'cancelled') DEFAULT 'completed',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_session_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_session_user (user_id),
    INDEX idx_session_created (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
-- 10. AI WORKOUT SESSIONS (Camera CV logs)
-- ------------------------------------------------------------
DROP TABLE IF EXISTS ai_workout_sessions;
CREATE TABLE ai_workout_sessions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    workout_session_id INT NULL,
    exercise_name VARCHAR(100) NOT NULL,
    repetitions_completed INT NOT NULL DEFAULT 0,
    valid_reps INT NOT NULL DEFAULT 0,
    invalid_reps INT NOT NULL DEFAULT 0,
    duration_seconds INT NOT NULL DEFAULT 0,
    average_form_score DECIMAL(5,2) NOT NULL DEFAULT 0.0,
    form_category ENUM('excellent', 'good', 'needs_improvement', 'poor') NOT NULL DEFAULT 'good',
    calories_burned DECIMAL(6,2) NOT NULL DEFAULT 0.0,
    feedback_summary TEXT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_ai_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_ai_session FOREIGN KEY (workout_session_id) REFERENCES workout_sessions(id) ON DELETE SET NULL,
    INDEX idx_ai_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
-- 11. WEIGHT HISTORY TABLE
-- ------------------------------------------------------------
DROP TABLE IF EXISTS weight_history;
CREATE TABLE weight_history (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    weight_kg DECIMAL(5,2) NOT NULL,
    bmi DECIMAL(4,2) NOT NULL,
    logged_date DATE NOT NULL,
    notes VARCHAR(255) NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_weight_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_weight_user_date (user_id, logged_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Re-enable foreign key checks
SET FOREIGN_KEY_CHECKS = 1;
