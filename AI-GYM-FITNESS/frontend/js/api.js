/**
 * AI GYM & FITNESS ASSISTANT - Unified API Client & Resilient Offline Storage
 * SRM INSTITUTE OF SCIENCE AND TECHNOLOGY | 21CSC203P
 * Connects to Python Flask REST API at http://localhost:5000/api
 * Includes automatic local fallback engine for seamless instant execution.
 */

const API_BASE = 'http://localhost:5000/api';

class ApiClient {
  constructor() {
    this.token = localStorage.getItem('ai_gym_token') || null;
    this.useMock = false;
    this._initStorage();
  }

  _initStorage() {
    if (!localStorage.getItem('ai_gym_user')) {
      const defaultUser = {
        id: 1,
        name: "Aswinkumar J",
        email: "aswin@fitness.srmist.edu.in",
        profile: {
          age: 21,
          gender: "male",
          height_cm: 175.0,
          weight_kg: 78.0,
          target_weight_kg: 70.0,
          fitness_goal: "weight_loss",
          workout_type: "gym",
          workout_frequency: 5,
          activity_level: "moderately_active",
          fitness_level: "intermediate",
          diet_preference: "non_vegetarian",
          food_preferences: "High protein, chicken, eggs, rice",
          food_allergies: "peanuts"
        },
        fitness_goals: {
          bmi: 25.47,
          bmi_category: "Overweight",
          bmr: 1773.75,
          tdee: 2749.31,
          daily_calories: 2249.31,
          protein_g: 140.0,
          carbs_g: 260.0,
          fat_g: 65.0,
          estimated_weeks: 16
        }
      };
      localStorage.setItem('ai_gym_user', JSON.stringify(defaultUser));
    }

    if (!localStorage.getItem('ai_gym_weight_history')) {
      const history = [
        { id: 1, weight_kg: 80.5, bmi: 26.28, logged_date: "2026-09-07", notes: "Baseline check-in" },
        { id: 2, weight_kg: 79.8, bmi: 26.06, logged_date: "2026-09-14", notes: "Cardio added" },
        { id: 3, weight_kg: 79.0, bmi: 25.80, logged_date: "2026-09-21", notes: "Diet adherence 95%" },
        { id: 4, weight_kg: 78.5, bmi: 25.63, logged_date: "2026-09-28", notes: "Strength training" },
        { id: 5, weight_kg: 78.0, bmi: 25.47, logged_date: "2026-10-05", notes: "Current weigh-in" }
      ];
      localStorage.setItem('ai_gym_weight_history', JSON.stringify(history));
    }

    if (!localStorage.getItem('ai_gym_sessions')) {
      const sessions = [
        {
          id: 1,
          session_title: "AI Full Body Functional Circuit",
          duration_minutes: 32,
          total_exercises: 5,
          total_reps: 86,
          calories_burned: 265.0,
          average_form_score: 89.4,
          created_at: "2026-10-04 18:30:00"
        }
      ];
      localStorage.setItem('ai_gym_sessions', JSON.stringify(sessions));
    }
  }

  async request(endpoint, options = {}) {
    const url = `${API_BASE}${endpoint}`;
    const headers = {
      'Content-Type': 'application/json',
      ...(this.token ? { 'Authorization': `Bearer ${this.token}` } : {}),
      ...(options.headers || {})
    };

    if (!this.useMock) {
      try {
        const controller = new AbortController();
        const timeoutId = setTimeout(() => controller.abort(), 1800);
        const res = await fetch(url, { ...options, headers, signal: controller.signal });
        clearTimeout(timeoutId);
        if (res.ok) {
          return await res.json();
        }
      } catch (err) {
        // Backend not currently responding, switch seamlessly to local engine
        this.useMock = true;
      }
    }

    // Client-side fallback handling
    return this._mockHandler(endpoint, options);
  }

  _mockHandler(endpoint, options) {
    const method = (options.method || 'GET').toUpperCase();
    const body = options.body ? JSON.parse(options.body) : {};
    const currentUser = JSON.parse(localStorage.getItem('ai_gym_user'));

    // Auth Register
    if (endpoint === '/auth/register' && method === 'POST') {
      const newUser = {
        id: Date.now(),
        name: body.name,
        email: body.email,
        profile: body,
        fitness_goals: this.calculateFitnessSync(body)
      };
      localStorage.setItem('ai_gym_user', JSON.stringify(newUser));
      localStorage.setItem('ai_gym_token', 'mock_jwt_token_' + Date.now());
      this.token = 'mock_jwt_token_' + Date.now();
      return { success: true, user: newUser, fitness_goals: newUser.fitness_goals };
    }

    // Auth Login
    if (endpoint === '/auth/login' && method === 'POST') {
      this.token = 'mock_jwt_token_' + Date.now();
      localStorage.setItem('ai_gym_token', this.token);
      return { success: true, user: currentUser, profile: currentUser.profile };
    }

    // User Profile
    if (endpoint === '/user/profile') {
      if (method === 'PUT') {
        currentUser.profile = { ...currentUser.profile, ...body };
        currentUser.fitness_goals = this.calculateFitnessSync(currentUser.profile);
        localStorage.setItem('ai_gym_user', JSON.stringify(currentUser));
        return { success: true, profile: currentUser.profile, fitness_goals: currentUser.fitness_goals };
      }
      return { success: true, user: currentUser, profile: currentUser.profile, fitness_goals: currentUser.fitness_goals };
    }

    // Fitness Calculate
    if (endpoint === '/fitness/calculate' && method === 'POST') {
      return { success: true, data: this.calculateFitnessSync(body) };
    }

    // Diet Today & Generate
    if (endpoint.startsWith('/diet')) {
      const dietType = body.diet_preference || currentUser.profile.diet_preference || 'vegetarian';
      const cals = body.target_calories || currentUser.fitness_goals.daily_calories || 2000;
      const protein = body.target_protein || currentUser.fitness_goals.protein_g || 130;
      return { success: true, plan: this.generateDietSync(dietType, cals, protein) };
    }

    // Workout Today & Generate
    if (endpoint.startsWith('/workout')) {
      if (endpoint === '/workout/session' && method === 'POST') {
        const sessions = JSON.parse(localStorage.getItem('ai_gym_sessions') || '[]');
        const newSession = {
          id: Date.now(),
          session_title: body.session_title || 'AI Workout Session',
          duration_minutes: body.duration_minutes || 0,
          total_exercises: body.total_exercises || 1,
          total_reps: body.total_reps || 0,
          calories_burned: body.calories_burned || 0,
          average_form_score: body.average_form_score || 0,
          created_at: new Date().toISOString().replace('T', ' ').substring(0, 19)
        };
        sessions.unshift(newSession);
        localStorage.setItem('ai_gym_sessions', JSON.stringify(sessions));
        return { success: true, session_id: newSession.id };
      }
      const goal = body.goal || currentUser.profile.fitness_goal || 'weight_loss';
      const location = body.location || currentUser.profile.workout_type || 'gym';
      return { success: true, routine: this.generateWorkoutSync(goal, location) };
    }

    // Progress
    if (endpoint === '/progress') {
      const history = JSON.parse(localStorage.getItem('ai_gym_weight_history') || '[]');
      const sessions = JSON.parse(localStorage.getItem('ai_gym_sessions') || '[]');
      const startW = history[0] ? history[0].weight_kg : currentUser.profile.weight_kg;
      const currW = currentUser.profile.weight_kg;
      const targetW = currentUser.profile.target_weight_kg;

      let totalCals = 0;
      let totalScores = 0;
      sessions.forEach(s => {
        totalCals += s.calories_burned || 0;
        totalScores += s.average_form_score || 0;
      });

      return {
        success: true,
        progress: {
          start_weight: startW,
          current_weight: currW,
          target_weight: targetW,
          weight_lost: +(startW - currW).toFixed(1),
          weight_remaining: +(Math.abs(currW - targetW)).toFixed(1),
          total_workouts: sessions.length,
          total_calories_burned: Math.round(totalCals),
          overall_avg_form_score: sessions.length ? Math.round(totalScores / sessions.length) : 0,
          history: history
        }
      };
    }

    // Progress Weight
    if (endpoint === '/progress/weight' && method === 'POST') {
      const history = JSON.parse(localStorage.getItem('ai_gym_weight_history') || '[]');
      const w = parseFloat(body.weight_kg);
      const h_m = (currentUser.profile.height_cm || 175) / 100;
      const bmi = +(w / (h_m * h_m)).toFixed(2);
      const newEntry = {
        id: Date.now(),
        weight_kg: w,
        bmi: bmi,
        logged_date: body.logged_date || new Date().toISOString().split('T')[0],
        notes: body.notes || ''
      };
      history.push(newEntry);
      localStorage.setItem('ai_gym_weight_history', JSON.stringify(history));

      currentUser.profile.weight_kg = w;
      currentUser.fitness_goals = this.calculateFitnessSync(currentUser.profile);
      localStorage.setItem('ai_gym_user', JSON.stringify(currentUser));

      return { success: true, entry: newEntry };
    }

    return { success: true };
  }

  calculateFitnessSync(data) {
    const w = parseFloat(data.weight_kg || 70);
    const h = parseFloat(data.height_cm || 170);
    const age = parseInt(data.age || 25);
    const gender = (data.gender || 'male').toLowerCase();
    const activity = (data.activity_level || 'moderately_active').toLowerCase();
    const goal = (data.fitness_goal || 'weight_loss').toLowerCase();
    const targetW = parseFloat(data.target_weight_kg || w);

    const h_m = h / 100.0;
    const bmi = +(w / (h_m * h_m)).toFixed(2);
    let category = "Normal";
    let color = "#10b981";
    if (bmi < 18.5) { category = "Underweight"; color = "#38bdf8"; }
    else if (bmi >= 25 && bmi < 30) { category = "Overweight"; color = "#f59e0b"; }
    else if (bmi >= 30) { category = "Obesity"; color = "#ef4444"; }

    let bmr = (10 * w) + (6.25 * h) - (5 * age);
    bmr = gender === 'female' ? (bmr - 161) : (bmr + 5);
    bmr = +bmr.toFixed(1);

    const actMap = {
      'sedentary': 1.2,
      'lightly_active': 1.375,
      'moderately_active': 1.55,
      'very_active': 1.725,
      'extra_active': 1.9
    };
    const tdee = +(bmr * (actMap[activity] || 1.55)).toFixed(1);

    let daily_calories = tdee;
    let protein_per_kg = 1.8;
    if (goal === 'weight_loss') {
      daily_calories = Math.max(tdee - 450, gender === 'male' ? 1500 : 1250);
      protein_per_kg = 2.0;
    } else if (goal === 'muscle_building') {
      daily_calories = tdee + 350;
      protein_per_kg = 2.2;
    } else if (goal === 'strength') {
      daily_calories = tdee + 150;
      protein_per_kg = 2.0;
    }

    daily_calories = +daily_calories.toFixed(1);
    const protein_g = +(protein_per_kg * w).toFixed(1);
    const fat_g = +((daily_calories * 0.25) / 9.0).toFixed(1);
    const carbs_g = +((daily_calories - (protein_g * 4 + fat_g * 9)) / 4.0).toFixed(1);

    const delta = Math.abs(w - targetW);
    const weeks = Math.max(1, Math.round(delta / 0.5));

    return {
      bmi,
      bmi_category: category,
      bmi_color: color,
      bmr,
      tdee,
      daily_calories,
      protein_g,
      carbs_g,
      fat_g,
      current_weight_kg: w,
      target_weight_kg: targetW,
      estimated_weeks: weeks,
      projection_summary: `Estimated ${weeks} weeks to reach ${targetW} kg safely.`,
      health_disclaimer: "This application provides general fitness and nutrition estimates for informational purposes only. It is not a substitute for professional medical, nutritional, or fitness advice."
    };
  }

  generateDietSync(dietType, cals, protein) {
    const isVeg = (dietType || '').toLowerCase() === 'vegetarian';
    const meals = isVeg ? [
      { meal_type: 'breakfast', meal_title: 'Breakfast', food_name: 'Paneer Bhurji & Multigrain Roti', quantity: '1 plate (100g paneer + 2 rotis)', calories: Math.round(cals * 0.25), protein_g: Math.round(protein * 0.26), carbs_g: 38, fat_g: 15, is_vegetarian: true },
      { meal_type: 'mid_morning', meal_title: 'Mid-Morning Snack', food_name: 'Greek Yogurt with Chia & Berries', quantity: '1 bowl (180g)', calories: Math.round(cals * 0.10), protein_g: Math.round(protein * 0.12), carbs_g: 16, fat_g: 4, is_vegetarian: true },
      { meal_type: 'lunch', meal_title: 'Lunch', food_name: 'Rajma Masala with Steamed Brown Rice & Salad', quantity: '1 large bowl + 1 cup rice', calories: Math.round(cals * 0.35), protein_g: Math.round(protein * 0.32), carbs_g: 68, fat_g: 9, is_vegetarian: true },
      { meal_type: 'evening_snack', meal_title: 'Evening Snack', food_name: 'Roasted Foxnuts (Makhana) & Green Tea', quantity: '1 bowl (40g)', calories: Math.round(cals * 0.10), protein_g: Math.round(protein * 0.08), carbs_g: 26, fat_g: 3, is_vegetarian: true },
      { meal_type: 'dinner', meal_title: 'Dinner', food_name: 'Palak Tofu Stir-Fry with Quinoa', quantity: '150g tofu + 1 cup quinoa', calories: Math.round(cals * 0.20), protein_g: Math.round(protein * 0.22), carbs_g: 42, fat_g: 12, is_vegetarian: true }
    ] : [
      { meal_type: 'breakfast', meal_title: 'Breakfast', food_name: '3-Egg Vegetable Omelet with Whole Wheat Toast', quantity: '2 whole + 1 white + 2 toasts', calories: Math.round(cals * 0.25), protein_g: Math.round(protein * 0.28), carbs_g: 28, fat_g: 14, is_vegetarian: false },
      { meal_type: 'mid_morning', meal_title: 'Mid-Morning Snack', food_name: 'Sprouted Moong & Chana Chaat', quantity: '1 bowl (150g)', calories: Math.round(cals * 0.10), protein_g: Math.round(protein * 0.12), carbs_g: 25, fat_g: 2, is_vegetarian: true },
      { meal_type: 'lunch', meal_title: 'Lunch', food_name: 'Grilled Herb Chicken Breast with Quinoa & Steamed Greens', quantity: '180g chicken + 100g quinoa', calories: Math.round(cals * 0.35), protein_g: Math.round(protein * 0.38), carbs_g: 45, fat_g: 10, is_vegetarian: false },
      { meal_type: 'evening_snack', meal_title: 'Evening Snack', food_name: 'Mixed Raw Almonds, Walnuts & Apple', quantity: 'Handful (30g nuts + 1 fruit)', calories: Math.round(cals * 0.10), protein_g: Math.round(protein * 0.06), carbs_g: 22, fat_g: 15, is_vegetarian: true },
      { meal_type: 'dinner', meal_title: 'Dinner', food_name: 'Steamed Fish Fillet / Paneer Curry with 2 Rotis', quantity: '150g fish + 2 rotis', calories: Math.round(cals * 0.20), protein_g: Math.round(protein * 0.26), carbs_g: 36, fat_g: 8, is_vegetarian: false }
    ];

    let tCals = 0, tProt = 0, tCarbs = 0, tFat = 0;
    meals.forEach(m => {
      tCals += m.calories;
      tProt += m.protein_g;
      tCarbs += m.carbs_g;
      tFat += m.fat_g;
    });

    return {
      diet_type: isVeg ? 'Vegetarian' : 'Non-Vegetarian',
      target_calories: cals,
      target_protein: protein,
      total_calories: tCals,
      total_protein: tProt,
      total_carbs: tCarbs,
      total_fat: tFat,
      meals: meals
    };
  }

  generateWorkoutSync(goal, location) {
    return {
      title: `${goal.replace('_', ' ').toUpperCase()} Functional Split`,
      goal: goal,
      location: location,
      frequency_days_per_week: 4,
      estimated_duration_min: 45,
      total_exercises: 5,
      exercises: [
        {
          id: 1,
          name: "Squats",
          slug: "squats",
          target_muscle: "Quadriceps / Glutes",
          difficulty: "beginner",
          location: "both",
          sets: 3,
          reps: "12-15",
          rest_seconds: 60,
          has_ai_camera: true,
          instructions: "Stand tall with feet shoulder-width apart. Push hips backward and bend knees until thighs are parallel with the floor. Drive through heels back to upright standing.",
          form_cues: "Keep chest proud; Track knees over toes; Do not collapse inward"
        },
        {
          id: 2,
          name: "Push-ups",
          slug: "push-ups",
          target_muscle: "Pectorals / Triceps",
          difficulty: "beginner",
          location: "both",
          sets: 3,
          reps: "10-12",
          rest_seconds: 60,
          has_ai_camera: true,
          instructions: "Place hands shoulder-width apart on the ground. Maintain a rigid plank line from head to ankles. Lower chest to 2 inches off floor, then push back up.",
          form_cues: "Keep core locked; Do not let hips sag; Flare elbows at ~45 degrees"
        },
        {
          id: 3,
          name: "Lunges",
          slug: "lunges",
          target_muscle: "Quadriceps / Hamstrings",
          difficulty: "beginner",
          location: "both",
          sets: 3,
          reps: "12 per leg",
          rest_seconds: 60,
          has_ai_camera: true,
          instructions: "Step forward into a split stance. Lower body until front and back knees bend to 90 degrees. Push forcefully through front heel to step back.",
          form_cues: "Keep torso upright; Front knee directly above ankle"
        },
        {
          id: 4,
          name: "Bicep Curls",
          slug: "bicep-curls",
          target_muscle: "Biceps Brachii",
          difficulty: "beginner",
          location: "both",
          sets: 3,
          reps: "12",
          rest_seconds: 45,
          has_ai_camera: true,
          instructions: "Hold dumbbells at sides with palms forward. Keep elbows glued to ribcage. Contract biceps to curl weight up, pause, and lower slowly under control.",
          form_cues: "No torso swinging; Pin elbows tight; Slow 2-second negative"
        },
        {
          id: 5,
          name: "Shoulder Press",
          slug: "shoulder-press",
          target_muscle: "Deltoids / Trapezius",
          difficulty: "intermediate",
          location: "both",
          sets: 3,
          reps: "10",
          rest_seconds: 60,
          has_ai_camera: true,
          instructions: "Hold weights at ear height with elbows bent at 90 degrees. Press vertically upward overhead until arms fully extend. Lower smoothly back to ear height.",
          form_cues: "Do not hyperextend lumbar spine; Squeeze glutes and core; Press in vertical plane"
        }
      ]
    };
  }
}

// Global singleton instance
window.api = new ApiClient();
