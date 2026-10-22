-- ============================================================
-- AI GYM & FITNESS ASSISTANT
-- Seed Data for Nutrition & Exercises
-- SRM INSTITUTE OF SCIENCE AND TECHNOLOGY
-- ============================================================

USE ai_gym_fitness;

-- ------------------------------------------------------------
-- SEED FOOD ITEMS (Vegetarian & Non-Vegetarian)
-- ------------------------------------------------------------
INSERT INTO food_items (name, category, is_vegetarian, portion_description, portion_grams, calories, protein_g, carbs_g, fat_g, fiber_g, allergens, tags) VALUES
-- Vegetarian Breakfast & Snacks
('Rolled Oats with Almond Milk', 'breakfast', 1, '1 bowl (50g oats + 150ml milk)', 200.0, 240.0, 8.5, 38.0, 5.5, 6.0, 'nuts', 'high_fiber,vegan,quick'),
('Paneer Bhurji with 2 Multigrain Roti', 'breakfast', 1, '1 plate (100g paneer + 2 rotis)', 220.0, 380.0, 22.0, 36.0, 16.0, 5.0, 'dairy,gluten', 'high_protein,vegetarian'),
('Moong Dal Cheela with Mint Chutney', 'breakfast', 1, '2 pieces (120g)', 150.0, 210.0, 12.0, 28.0, 4.5, 6.5, NULL, 'high_protein,vegan,gluten_free'),
('Greek Yogurt with Chia & Berries', 'breakfast', 1, '1 cup (180g)', 180.0, 165.0, 15.0, 14.0, 4.0, 3.5, 'dairy', 'high_protein,calcium'),
('Sprouted Moong & Chana Chaat', 'snack', 1, '1 medium bowl (150g)', 150.0, 185.0, 11.5, 29.0, 2.0, 7.0, NULL, 'high_fiber,raw,vegan'),
('Roasted Makhana (Foxnuts)', 'snack', 1, '1 large bowl (40g)', 40.0, 145.0, 4.0, 25.0, 2.5, 3.0, NULL, 'low_calorie,vegan,gluten_free'),
('Mixed Raw Almonds and Walnuts', 'snack', 1, 'Handful (30g)', 30.0, 190.0, 6.0, 5.0, 17.0, 3.0, 'nuts', 'healthy_fats,keto'),
('Peanut Butter on Whole Wheat Toast', 'snack', 1, '2 slices (30g PB)', 110.0, 290.0, 12.0, 30.0, 14.0, 4.5, 'peanuts,gluten', 'energy,protein'),

-- Vegetarian Lunch & Dinner
('Yellow Dal Tadka with Brown Rice', 'lunch', 1, '1 bowl dal + 1 cup rice', 300.0, 340.0, 14.0, 58.0, 5.0, 7.0, NULL, 'staple,comfort,vegan'),
('Rajma Masala with Steamed Basmati Rice', 'lunch', 1, '1 bowl rajma + 1 cup rice', 320.0, 390.0, 16.5, 66.0, 6.5, 9.0, NULL, 'high_fiber,vegan,iron'),
('Chole (Chickpea Curry) with 2 Rotis', 'lunch', 1, '1 bowl chole + 2 rotis', 300.0, 410.0, 15.0, 60.0, 10.0, 8.5, 'gluten', 'fiber_rich,filling'),
('Soya Chunks Stir-fry with Bell Peppers', 'lunch', 1, '1 bowl (60g soya chunks)', 200.0, 260.0, 31.0, 18.0, 3.5, 8.0, 'soy', 'ultra_high_protein,vegan'),
('Palak Paneer with 2 Whole Wheat Rotis', 'dinner', 1, '1 bowl palak paneer + 2 rotis', 280.0, 370.0, 20.0, 32.0, 18.0, 6.0, 'dairy,gluten', 'iron,calcium,protein'),
('Tofu Tikka with Grilled Veggies & Quinoa', 'dinner', 1, '1 plate (150g tofu + 100g quinoa)', 300.0, 350.0, 24.0, 35.0, 12.0, 6.5, 'soy', 'vegan,lean_protein'),
('Dal Makhani (Light Home Style) with Roti', 'dinner', 1, '1 bowl + 2 rotis', 270.0, 360.0, 13.0, 48.0, 12.0, 7.5, 'dairy,gluten', 'comfort,protein'),
('Mixed Vegetable Khichdi with Curd', 'dinner', 1, '1 deep bowl (250g) + 100g curd', 350.0, 320.0, 13.5, 46.0, 7.0, 6.0, 'dairy', 'easy_digestion,light'),

-- Non-Vegetarian Items (Eggs, Chicken, Fish)
('Boiled Egg Whites (4 Eggs)', 'breakfast', 0, '4 large egg whites', 132.0, 68.0, 14.4, 0.8, 0.4, 0.0, 'eggs', 'pure_protein,lean'),
('3-Egg Vegetable Omelet with 1 Toast', 'breakfast', 0, '1 whole + 2 whites + veggies + 1 toast', 220.0, 290.0, 21.0, 18.0, 13.0, 2.5, 'eggs,gluten', 'high_protein,keto_friendly'),
('Egg Bhurji with 2 Multigrain Rotis', 'breakfast', 0, '2 whole eggs + veggies + 2 rotis', 240.0, 380.0, 20.0, 34.0, 16.0, 4.0, 'eggs,gluten', 'muscle_building'),
('Grilled Herb Chicken Breast with Quinoa', 'lunch', 0, '150g chicken + 100g quinoa + broccoli', 320.0, 420.0, 45.0, 30.0, 9.0, 5.0, NULL, 'lean_protein,clean_eating'),
('Tandoori Chicken Breast with Green Salad', 'lunch', 0, '180g cooked chicken breast', 220.0, 280.0, 48.0, 4.0, 7.5, 2.0, 'dairy', 'high_protein,low_carb'),
('Chicken Curry (Homestyle) with 2 Rotis', 'lunch', 0, '150g chicken + gravy + 2 rotis', 300.0, 430.0, 36.0, 36.0, 14.0, 4.5, 'gluten', 'traditional,protein'),
('Grilled Salmon Fillet with Asparagus', 'dinner', 0, '160g salmon + sautéed asparagus', 240.0, 380.0, 38.0, 5.0, 22.0, 3.0, 'fish', 'omega3,heart_healthy'),
('Steamed Fish Curry with Brown Rice', 'dinner', 0, '150g white fish + 1 cup brown rice', 320.0, 370.0, 34.0, 45.0, 6.0, 4.0, 'fish', 'lean,light'),
('Chicken Stir-fry with Brown Rice Noodles', 'dinner', 0, '120g chicken + veggies + noodles', 300.0, 410.0, 32.0, 52.0, 8.0, 4.0, 'soy,gluten', 'post_workout');

-- ------------------------------------------------------------
-- SEED EXERCISES (Focus on 5 Core AI Camera Exercises + Essentials)
-- ------------------------------------------------------------
INSERT INTO exercises (name, slug, target_muscle, secondary_muscles, difficulty, location, equipment, default_sets, default_reps, default_rest_seconds, has_ai_camera, instructions, form_cues) VALUES
('Squats', 'squats', 'Quadriceps / Glutes', 'Hamstrings, Calves, Core', 'beginner', 'both', 'Bodyweight / Dumbbells', 3, 12, 60, 1, 
'1. Stand tall with feet shoulder-width apart, toes pointing slightly outward.\n2. Engage your core and keep your chest lifted.\n3. Push your hips back and bend your knees as if sitting into an imaginary chair.\n4. Descend until your thighs are at least parallel to the floor (knees at ~90 degrees).\n5. Drive through your heels to return to the upright starting position.',
'Keep chest high; Do not let knees cave inward; Break parallel at bottom; Keep heels flat on ground'),

('Push-ups', 'push-ups', 'Chest (Pectorals)', 'Triceps, Anterior Deltoids, Core', 'beginner', 'both', 'Bodyweight', 3, 10, 60, 1,
'1. Place hands on the floor slightly wider than shoulder-width.\n2. Extend legs backward with toes on the ground, creating a straight line from neck to ankles.\n3. Lower your body under control until your chest is 2 inches above the ground (elbows at ~90 degrees).\n4. Push forcefully through your palms to return to full arm extension without locking elbows.',
'Maintain neutral spine; Avoid sagging lower back; Keep elbows at 45 degree angle; Fully extend arms at top'),

('Lunges', 'lunges', 'Quadriceps / Hamstrings', 'Glutes, Calves, Core Stabilizers', 'beginner', 'both', 'Bodyweight / Dumbbells', 3, 12, 60, 1,
'1. Stand upright with feet hip-width apart.\n2. Take a controlled step forward with your lead leg.\n3. Lower hips until both front and back knees form 90-degree angles.\n4. The back knee should hover just above the floor without hitting it.\n5. Drive through the front heel to push back to the initial standing position.',
'Keep torso upright; Front knee directly above ankle; Do not let front knee pass toes; Maintain balance'),

('Bicep Curls', 'bicep-curls', 'Biceps Brachii', 'Brachialis, Forearms', 'beginner', 'both', 'Dumbbells / Resistance Band', 3, 12, 45, 1,
'1. Stand with feet shoulder-width apart, holding weights at arm length beside thighs with palms forward.\n2. Keep your upper arms stationary and elbows glued tight to your ribcage.\n3. Exhale and curl the weights up toward shoulder height by contracting the biceps.\n4. Pause briefly at peak contraction, then lower slowly back to full extension under control.',
'Do not swing your torso; Keep elbows pinned at sides; Full range of motion; Slow eccentric phase'),

('Shoulder Press', 'shoulder-press', 'Deltoids (Shoulders)', 'Triceps, Upper Chest, Trapezius', 'intermediate', 'both', 'Dumbbells / Barbell', 3, 10, 60, 1,
'1. Hold dumbbells at shoulder height with palms facing forward and elbows bent at 90 degrees.\n2. Brace your core and keep a steady, neutral spine.\n3. Press the weights upward vertically until your arms are extended directly overhead.\n4. Lower the weights slowly and symmetrically back to ear/shoulder level.',
'Do not arch lower back excessively; Press straight overhead; Control descent; Breathe out while pressing'),

('Plank', 'plank', 'Transverse Abdominis / Core', 'Glutes, Shoulders, Lower Back', 'beginner', 'both', 'Bodyweight', 3, 45, 45, 0,
'1. Lie prone and prop yourself onto forearms and toes.\n2. Elbows directly under shoulders.\n3. Squeeze glutes and abdominals to form a rigid, straight line from head to heels.\n4. Hold steady without letting hips drop or rise.',
'Breathe rhythmically; Lock your ribcage down; Neutral neck position'),

('Dumbbell Romanian Deadlift', 'romanian-deadlift', 'Hamstrings / Posterior Chain', 'Glutes, Erector Spinae, Traps', 'intermediate', 'both', 'Dumbbells', 3, 10, 75, 0,
'1. Hold dumbbells in front of thighs with knees softly unlocked.\n2. Hinge at hips pushing your buttocks backward while keeping spine flat.\n3. Lower weights along shins until you feel a deep hamstring stretch.\n4. Drive hips forward and squeeze glutes to return upright.',
'Hinge at hips, do not squat; Keep dumbbells close to legs; Maintain flat spine');

-- ------------------------------------------------------------
-- SAMPLE DEMO USER (For Instant Verification & Testing)
-- Password: "Password123!" (hashed using sha256:salt)
-- ------------------------------------------------------------
INSERT INTO users (id, name, email, password_hash) VALUES
(1, 'Aswinkumar J', 'aswin@fitness.srmist.edu.in', 'c3ab8ff13720e8ad9047dd39466b3c8974e592f2fa383d4a3960714caef0c4f2$8f142b78'),
(2, 'Priya Sharma', 'priya@fitness.srmist.edu.in', 'c3ab8ff13720e8ad9047dd39466b3c8974e592f2fa383d4a3960714caef0c4f2$8f142b78');

-- User 1 Profile: Non-Veg Weight Loss
INSERT INTO user_profiles (user_id, age, gender, height_cm, weight_kg, target_weight_kg, fitness_goal, workout_type, workout_frequency, activity_level, fitness_level, diet_preference, food_preferences, food_allergies) VALUES
(1, 21, 'male', 175.0, 78.0, 70.0, 'weight_loss', 'gym', 5, 'moderately_active', 'intermediate', 'non_vegetarian', 'High protein, chicken, eggs, rice', 'peanuts'),
(2, 22, 'female', 162.0, 58.0, 54.0, 'general_fitness', 'home', 4, 'lightly_active', 'beginner', 'vegetarian', 'Paneer, lentils, fresh fruits, oats', 'none');

-- User 1 Calculations:
-- BMI = 78 / (1.75^2) = 25.47 (Overweight)
-- BMR = 10*78 + 6.25*175 - 5*21 + 5 = 780 + 1093.75 - 105 + 5 = 1773.75 kcal
-- TDEE = 1773.75 * 1.55 = 2749 kcal
-- Calorie Target (-500 kcal safe deficit) = ~2250 kcal
-- Protein = 78 * 1.8 = ~140g
INSERT INTO fitness_goals (user_id, bmi, bmi_category, bmr, tdee, daily_calorie_target, protein_grams, carbs_grams, fat_grams, estimated_weeks) VALUES
(1, 25.47, 'Overweight', 1773.75, 2749.31, 2249.31, 140.0, 260.0, 65.0, 16),
(2, 22.10, 'Normal', 1339.00, 1841.13, 1650.00, 85.0, 210.0, 48.0, 8);

-- Seed Initial Weight History
INSERT INTO weight_history (user_id, weight_kg, bmi, logged_date, notes) VALUES
(1, 80.5, 26.28, DATE_SUB(CURDATE(), INTERVAL 28 DAY), 'Initial baseline weighing'),
(1, 79.8, 26.06, DATE_SUB(CURDATE(), INTERVAL 21 DAY), 'Week 1 - started workout routine'),
(1, 79.0, 25.80, DATE_SUB(CURDATE(), INTERVAL 14 DAY), 'Week 2 - calorie tracking active'),
(1, 78.5, 25.63, DATE_SUB(CURDATE(), INTERVAL 7 DAY), 'Week 3 - increased cardio'),
(1, 78.0, 25.47, CURDATE(), 'Current weight - 2.5 kg lost so far!');

-- Seed Sample Completed AI Workout Session
INSERT INTO workout_sessions (id, user_id, session_title, duration_minutes, total_exercises, total_reps, calories_burned, average_form_score, status) VALUES
(1, 1, 'AI Full Body Functional Circuit', 32, 5, 86, 265.0, 89.4, 'completed');

INSERT INTO ai_workout_sessions (user_id, workout_session_id, exercise_name, repetitions_completed, valid_reps, invalid_reps, duration_seconds, average_form_score, form_category, calories_burned, feedback_summary) VALUES
(1, 1, 'Squats', 25, 24, 1, 380, 92.5, 'excellent', 85.0, 'Great depth reached. Excellent chest posture.'),
(1, 1, 'Push-ups', 20, 18, 2, 290, 86.0, 'good', 60.0, 'Solid range of motion. Keep core tight.'),
(1, 1, 'Bicep Curls', 24, 23, 1, 310, 90.0, 'excellent', 55.0, 'Controlled eccentric pace. Elbows steady.');
