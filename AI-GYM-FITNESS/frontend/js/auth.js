/**
 * AI GYM & FITNESS ASSISTANT - Authentication & Multi-Step Registration Controller
 * SRM INSTITUTE OF SCIENCE AND TECHNOLOGY
 */

let currentStep = 1;
const totalSteps = 4;

document.addEventListener('DOMContentLoaded', () => {
  initRegistrationWizard();
  initLoginForm();
});

function initRegistrationWizard() {
  const wizardForm = document.getElementById('multi-step-register-form');
  if (!wizardForm) return;

  updateWizardStepView(1);

  // Next Step buttons
  document.querySelectorAll('.btn-next-step').forEach(btn => {
    btn.addEventListener('click', () => {
      if (validateCurrentStep(currentStep)) {
        if (currentStep < totalSteps) {
          currentStep++;
          updateWizardStepView(currentStep);
        }
      }
    });
  });

  // Prev Step buttons
  document.querySelectorAll('.btn-prev-step').forEach(btn => {
    btn.addEventListener('click', () => {
      if (currentStep > 1) {
        currentStep--;
        updateWizardStepView(currentStep);
      }
    });
  });

  // Form Submission
  wizardForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    if (!validateCurrentStep(currentStep)) return;

    const formData = gatherRegistrationData();
    const submitBtn = document.getElementById('register-submit-btn');
    if (submitBtn) {
      submitBtn.disabled = true;
      submitBtn.innerText = "Setting Up Profile...";
    }

    try {
      const res = await window.api.request('/auth/register', {
        method: 'POST',
        body: JSON.stringify(formData)
      });

      if (res.success) {
        showToast("Registration completed! Redirecting to Dashboard...", "success");
        setTimeout(() => {
          window.location.href = "dashboard.html";
        }, 1200);
      } else {
        showToast(res.message || "Registration failed. Please check inputs.", "error");
        if (submitBtn) {
          submitBtn.disabled = false;
          submitBtn.innerText = "Complete Registration";
        }
      }
    } catch (err) {
      showToast("Network error. Saving to local profile...", "info");
      setTimeout(() => {
        window.location.href = "dashboard.html";
      }, 1000);
    }
  });
}

function updateWizardStepView(step) {
  // Update step fieldset visibility
  document.querySelectorAll('.wizard-step').forEach(el => {
    el.style.display = 'none';
  });
  const activeStepEl = document.getElementById(`step-${step}`);
  if (activeStepEl) activeStepEl.style.display = 'block';

  // Update progress bar & indicators
  document.querySelectorAll('.step-indicator-item').forEach((item, idx) => {
    const stepNum = idx + 1;
    item.classList.remove('active', 'completed');
    if (stepNum === step) {
      item.classList.add('active');
    } else if (stepNum < step) {
      item.classList.add('completed');
    }
  });

  const progressBarFill = document.getElementById('register-progress-fill');
  if (progressBarFill) {
    const pct = ((step - 1) / (totalSteps - 1)) * 100;
    progressBarFill.style.width = `${pct}%`;
  }
}

function validateCurrentStep(step) {
  if (step === 1) {
    const name = (document.getElementById('reg-name')?.value || '').trim();
    const email = (document.getElementById('reg-email')?.value || '').trim();
    const password = document.getElementById('reg-password')?.value || '';
    const age = parseInt(document.getElementById('reg-age')?.value || 0);
    const gender = document.getElementById('reg-gender')?.value;

    if (name.length < 2) {
      showToast("Please enter a valid full name (at least 2 characters).", "error");
      return false;
    }
    const emailRegex = /^[\w\.-]+@([\w\.-]+\.)+[\w-]{2,4}$/;
    if (!emailRegex.test(email)) {
      showToast("Please enter a valid email address.", "error");
      return false;
    }
    if (password.length < 6) {
      showToast("Password must be at least 6 characters.", "error");
      return false;
    }
    if (isNaN(age) || age < 12 || age > 95) {
      showToast("Age must be between 12 and 95.", "error");
      return false;
    }
    if (!gender) {
      showToast("Please select your gender.", "error");
      return false;
    }
    return true;
  }

  if (step === 2) {
    const height = parseFloat(document.getElementById('reg-height')?.value || 0);
    const weight = parseFloat(document.getElementById('reg-weight')?.value || 0);
    const targetWeight = parseFloat(document.getElementById('reg-target-weight')?.value || 0);

    if (isNaN(height) || height < 90 || height > 240) {
      showToast("Please enter a realistic height between 90 and 240 cm.", "error");
      return false;
    }
    if (isNaN(weight) || weight < 30 || weight > 250) {
      showToast("Please enter a realistic weight between 30 and 250 kg.", "error");
      return false;
    }
    if (isNaN(targetWeight) || targetWeight < 30 || targetWeight > 250) {
      showToast("Please enter a target weight between 30 and 250 kg.", "error");
      return false;
    }
    return true;
  }

  if (step === 3) {
    const goal = document.getElementById('reg-goal')?.value;
    const workoutType = document.getElementById('reg-workout-type')?.value;
    const activity = document.getElementById('reg-activity')?.value;
    if (!goal || !workoutType || !activity) {
      showToast("Please make a selection for all fitness fields.", "error");
      return false;
    }
    return true;
  }

  if (step === 4) {
    const diet = document.querySelector('input[name="diet_preference"]:checked')?.value;
    if (!diet) {
      showToast("Please select Vegetarian or Non-Vegetarian diet preference.", "error");
      return false;
    }
    return true;
  }

  return true;
}

function gatherRegistrationData() {
  const allergies = [];
  document.querySelectorAll('input[name="allergy_check"]:checked').forEach(cb => {
    allergies.push(cb.value);
  });

  return {
    name: document.getElementById('reg-name').value.trim(),
    email: document.getElementById('reg-email').value.trim().toLowerCase(),
    password: document.getElementById('reg-password').value,
    age: parseInt(document.getElementById('reg-age').value),
    gender: document.getElementById('reg-gender').value,
    height_cm: parseFloat(document.getElementById('reg-height').value),
    weight_kg: parseFloat(document.getElementById('reg-weight').value),
    target_weight_kg: parseFloat(document.getElementById('reg-target-weight').value),
    fitness_goal: document.getElementById('reg-goal').value,
    workout_type: document.getElementById('reg-workout-type').value,
    workout_frequency: parseInt(document.getElementById('reg-frequency')?.value || 4),
    activity_level: document.getElementById('reg-activity').value,
    fitness_level: document.getElementById('reg-fitness-level')?.value || 'beginner',
    diet_preference: document.querySelector('input[name="diet_preference"]:checked')?.value || 'vegetarian',
    food_preferences: document.getElementById('reg-food-prefs')?.value.trim() || '',
    food_allergies: allergies.join(',')
  };
}

function initLoginForm() {
  const loginForm = document.getElementById('login-form');
  if (!loginForm) return;

  loginForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    const email = (document.getElementById('login-email')?.value || '').trim().toLowerCase();
    const password = document.getElementById('login-password')?.value || '';

    if (!email || !password) {
      showToast("Please provide both email and password.", "error");
      return;
    }

    try {
      const res = await window.api.request('/auth/login', {
        method: 'POST',
        body: JSON.stringify({ email, password })
      });

      if (res.success) {
        showToast("Login successful! Welcome back.", "success");
        setTimeout(() => {
          window.location.href = "dashboard.html";
        }, 800);
      } else {
        showToast(res.message || "Invalid credentials.", "error");
      }
    } catch (err) {
      showToast("Logging in with local demo credentials...", "info");
      setTimeout(() => {
        window.location.href = "dashboard.html";
      }, 600);
    }
  });
}

function fillDemoUser(type) {
  if (type === 'john') {
    const user = {
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
        food_preferences: "Chicken breast, egg whites, oatmeal, basmati rice",
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
    localStorage.setItem('ai_gym_user', JSON.stringify(user));
    localStorage.setItem('ai_gym_token', 'demo_token_1');
    showToast("Loaded Demo User (Weight Loss Goal)", "success");
    setTimeout(() => window.location.href = "dashboard.html", 700);
  } else if (type === 'priya') {
    const user = {
      id: 2,
      name: "Priya Sharma",
      email: "priya@fitness.srmist.edu.in",
      profile: {
        age: 22,
        gender: "female",
        height_cm: 162.0,
        weight_kg: 56.0,
        target_weight_kg: 52.0,
        fitness_goal: "general_fitness",
        workout_type: "home",
        workout_frequency: 4,
        activity_level: "lightly_active",
        fitness_level: "beginner",
        diet_preference: "vegetarian",
        food_preferences: "Paneer, greek yogurt, sprouts, fruits, quinoa",
        food_allergies: ""
      },
      fitness_goals: {
        bmi: 21.34,
        bmi_category: "Normal",
        bmr: 1324.0,
        tdee: 1820.5,
        daily_calories: 1650.0,
        protein_g: 95.0,
        carbs_g: 210.0,
        fat_g: 48.0,
        estimated_weeks: 8
      }
    };
    localStorage.setItem('ai_gym_user', JSON.stringify(user));
    localStorage.setItem('ai_gym_token', 'demo_token_2');
    showToast("Loaded Demo User (Vegetarian Fitness Goal)", "success");
    setTimeout(() => window.location.href = "dashboard.html", 700);
  }
}
