/**
 * AI GYM & FITNESS ASSISTANT - AI Nutrition Controller
 * SRM INSTITUTE OF SCIENCE AND TECHNOLOGY
 */

let activeDietType = 'vegetarian';
let isWeeklyMode = false;
let currentWeeklyPlans = [];
let activeDayIndex = 0;

document.addEventListener('DOMContentLoaded', async () => {
  initNutritionControls();
  await loadInitialDietPlan();
});

function initNutritionControls() {
  // Diet Toggle (Veg / Non-Veg)
  document.querySelectorAll('.diet-toggle-btn').forEach(btn => {
    btn.addEventListener('click', (e) => {
      document.querySelectorAll('.diet-toggle-btn').forEach(b => b.classList.remove('active'));
      btn.classList.add('active');
      activeDietType = btn.getAttribute('data-type');
      generateCurrentPlan();
    });
  });

  // Action Buttons
  const genTodayBtn = document.getElementById('btn-gen-today-diet');
  if (genTodayBtn) {
    genTodayBtn.addEventListener('click', () => {
      isWeeklyMode = false;
      document.getElementById('weekly-days-nav').style.display = 'none';
      generateCurrentPlan();
    });
  }

  const genWeeklyBtn = document.getElementById('btn-gen-weekly-diet');
  if (genWeeklyBtn) {
    genWeeklyBtn.addEventListener('click', () => {
      generateWeeklyPlan();
    });
  }
}

async function loadInitialDietPlan() {
  const profileRes = await window.api.request('/user/profile');
  if (profileRes.profile) {
    activeDietType = profileRes.profile.diet_preference || 'vegetarian';
    document.querySelectorAll('.diet-toggle-btn').forEach(b => {
      if (b.getAttribute('data-type') === activeDietType) b.classList.add('active');
      else b.classList.remove('active');
    });
  }

  await generateCurrentPlan();
}

async function generateCurrentPlan() {
  const profileRes = await window.api.request('/user/profile');
  const userGoals = profileRes.fitness_goals || {};

  const targetCals = userGoals.daily_calories || 2000;
  const targetProtein = userGoals.protein_g || 130;
  const allergies = profileRes.profile ? (profileRes.profile.food_allergies || '') : '';

  const res = await window.api.request('/diet/generate', {
    method: 'POST',
    body: JSON.stringify({
      target_calories: targetCals,
      target_protein: targetProtein,
      diet_preference: activeDietType,
      allergies: allergies
    })
  });

  if (res.success && res.plan) {
    renderDietPlan(res.plan);
    showToast(`Generated personalized ${activeDietType} diet plan.`, 'success');
  }
}

async function generateWeeklyPlan() {
  isWeeklyMode = true;
  const profileRes = await window.api.request('/user/profile');
  const userGoals = profileRes.fitness_goals || {};

  const targetCals = userGoals.daily_calories || 2000;
  const targetProtein = userGoals.protein_g || 130;
  const allergies = profileRes.profile ? (profileRes.profile.food_allergies || '') : '';

  const res = await window.api.request('/diet/weekly', {
    method: 'POST',
    body: JSON.stringify({
      target_calories: targetCals,
      target_protein: targetProtein,
      diet_preference: activeDietType,
      allergies: allergies
    })
  });

  if (res.success && res.weekly_plan) {
    currentWeeklyPlans = res.weekly_plan;
    renderWeeklyTabs(res.weekly_plan);
    renderDietPlan(res.weekly_plan[0]);
    showToast("7-Day Personalized Meal Plan Ready!", "success");
  }
}

function renderWeeklyTabs(plans) {
  const navContainer = document.getElementById('weekly-days-nav');
  if (!navContainer) return;

  navContainer.style.display = 'flex';
  navContainer.innerHTML = plans.map((p, idx) => `
    <button class="day-tab-btn ${idx === 0 ? 'active' : ''}" onclick="switchWeeklyDay(${idx})">
      ${p.day}
    </button>
  `).join('');
}

function switchWeeklyDay(index) {
  activeDayIndex = index;
  document.querySelectorAll('.day-tab-btn').forEach((btn, i) => {
    if (i === index) btn.classList.add('active');
    else btn.classList.remove('active');
  });
  if (currentWeeklyPlans[index]) {
    renderDietPlan(currentWeeklyPlans[index]);
  }
}

function renderDietPlan(plan) {
  // Update Macro Summary Banner
  document.getElementById('diet-total-cals').innerText = `${Math.round(plan.total_calories)} kcal`;
  document.getElementById('diet-total-prot').innerText = `${Math.round(plan.total_protein)}g`;
  document.getElementById('diet-total-carbs').innerText = `${Math.round(plan.total_carbs)}g`;
  document.getElementById('diet-total-fat').innerText = `${Math.round(plan.total_fat)}g`;

  const container = document.getElementById('meals-list-container');
  if (!container) return;

  container.innerHTML = plan.meals.map(meal => {
    const isVeg = meal.is_vegetarian;
    return `
      <div class="meal-card">
        <div class="meal-slot-info">
          <div class="slot-name">${meal.meal_title}</div>
          <div class="slot-time">${getMealTiming(meal.meal_type)}</div>
          <span class="badge ${isVeg ? 'badge-veg' : 'badge-nonveg'}" style="align-self: flex-start; margin-top: 0.4rem;">
            ${isVeg ? '🌱 Vegetarian' : '🍗 Non-Veg'}
          </span>
        </div>

        <div class="food-details">
          <h4>${meal.food_name}</h4>
          <div class="food-portion">Quantity: <strong style="color: var(--text-primary);">${meal.quantity}</strong></div>
        </div>

        <div class="food-macro-pills">
          <div class="macro-chip">
            <span class="val">${meal.calories}</span>
            <span class="lbl">CALORIES</span>
          </div>
          <div class="macro-chip">
            <span class="val" style="color: var(--accent-neon);">${meal.protein_g}g</span>
            <span class="lbl">PROTEIN</span>
          </div>
          <div class="macro-chip">
            <span class="val" style="color: var(--accent-blue);">${meal.carbs_g}g</span>
            <span class="lbl">CARBS</span>
          </div>
          <div class="macro-chip">
            <span class="val" style="color: #f59e0b;">${meal.fat_g}g</span>
            <span class="lbl">FAT</span>
          </div>
        </div>
      </div>
    `;
  }).join('');
}

function getMealTiming(type) {
  switch (type) {
    case 'breakfast': return '08:00 AM – 09:00 AM';
    case 'mid_morning': return '11:00 AM – 11:30 AM';
    case 'lunch': return '01:00 PM – 02:00 PM';
    case 'evening_snack': return '05:00 PM – 05:30 PM';
    case 'dinner': return '08:00 PM – 09:00 PM';
    default: return 'Anytime';
  }
}
