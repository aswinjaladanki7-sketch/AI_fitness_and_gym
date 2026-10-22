/**
 * AI GYM & FITNESS ASSISTANT - Fitness Calculator Controller
 * SRM INSTITUTE OF SCIENCE AND TECHNOLOGY
 */

document.addEventListener('DOMContentLoaded', () => {
  initCalculatorControls();
  recalculateFitness();
});

function initCalculatorControls() {
  const heightSlider = document.getElementById('calc-height-slider');
  const weightSlider = document.getElementById('calc-weight-slider');
  const ageInput = document.getElementById('calc-age-input');

  const heightDisplay = document.getElementById('calc-height-val');
  const weightDisplay = document.getElementById('calc-weight-val');

  if (heightSlider && heightDisplay) {
    heightSlider.addEventListener('input', (e) => {
      heightDisplay.innerText = `${e.target.value} cm`;
      recalculateFitness();
    });
  }

  if (weightSlider && weightDisplay) {
    weightSlider.addEventListener('input', (e) => {
      weightDisplay.innerText = `${e.target.value} kg`;
      recalculateFitness();
    });
  }

  if (ageInput) {
    ageInput.addEventListener('input', () => recalculateFitness());
  }

  // Radio selection buttons
  document.querySelectorAll('.gender-radio-btn').forEach(btn => {
    btn.addEventListener('click', (e) => {
      document.querySelectorAll('.gender-radio-btn').forEach(b => b.classList.remove('active'));
      btn.classList.add('active');
      recalculateFitness();
    });
  });

  const activitySelect = document.getElementById('calc-activity-select');
  const goalSelect = document.getElementById('calc-goal-select');
  if (activitySelect) activitySelect.addEventListener('change', () => recalculateFitness());
  if (goalSelect) goalSelect.addEventListener('change', () => recalculateFitness());
}

async function recalculateFitness() {
  const height = parseFloat(document.getElementById('calc-height-slider')?.value || 175);
  const weight = parseFloat(document.getElementById('calc-weight-slider')?.value || 75);
  const age = parseInt(document.getElementById('calc-age-input')?.value || 25);
  const gender = document.querySelector('.gender-radio-btn.active')?.getAttribute('data-val') || 'male';
  const activity = document.getElementById('calc-activity-select')?.value || 'moderately_active';
  const goal = document.getElementById('calc-goal-select')?.value || 'weight_loss';
  const targetWeight = parseFloat(document.getElementById('calc-target-weight-input')?.value || (weight - 5));

  const payload = {
    height_cm: height,
    weight_kg: weight,
    target_weight_kg: targetWeight,
    age: age,
    gender: gender,
    activity_level: activity,
    fitness_goal: goal
  };

  const res = await window.api.request('/fitness/calculate', {
    method: 'POST',
    body: JSON.stringify(payload)
  });

  if (res.success && res.data) {
    updateResultsUI(res.data);
  }
}

function updateResultsUI(data) {
  // BMI Value & Category
  const bmiValEl = document.getElementById('res-bmi-val');
  if (bmiValEl) {
    bmiValEl.innerText = data.bmi;
    bmiValEl.style.color = data.bmi_color || '#10b981';
  }

  const bmiCatBadge = document.getElementById('res-bmi-badge');
  if (bmiCatBadge) {
    bmiCatBadge.innerText = data.bmi_category;
    bmiCatBadge.style.color = data.bmi_color || '#10b981';
    bmiCatBadge.style.borderColor = data.bmi_color || '#10b981';
  }

  // Needle positioning on spectrum (14 to 36 scale)
  const needle = document.getElementById('res-bmi-needle');
  if (needle) {
    const minBmi = 14.0;
    const maxBmi = 36.0;
    const clampedBmi = Math.max(minBmi, Math.min(maxBmi, data.bmi));
    const pct = ((clampedBmi - minBmi) / (maxBmi - minBmi)) * 100;
    needle.style.left = `${pct}%`;
  }

  // BMR & TDEE
  const bmrEl = document.getElementById('res-bmr-val');
  if (bmrEl) bmrEl.innerText = `${Math.round(data.bmr)} kcal`;

  const tdeeEl = document.getElementById('res-tdee-val');
  if (tdeeEl) tdeeEl.innerText = `${Math.round(data.tdee)} kcal`;

  // Target Calories & Macros
  const calsEl = document.getElementById('res-daily-cals');
  if (calsEl) calsEl.innerText = `${Math.round(data.daily_calories)} kcal`;

  const protEl = document.getElementById('res-prot-val');
  if (protEl) protEl.innerText = `${data.protein_g}g`;

  const carbsEl = document.getElementById('res-carbs-val');
  if (carbsEl) carbsEl.innerText = `${data.carbs_g}g`;

  const fatEl = document.getElementById('res-fat-val');
  if (fatEl) fatEl.innerText = `${data.fat_g}g`;

  // Progress summary
  const projEl = document.getElementById('res-proj-summary');
  if (projEl) projEl.innerText = data.projection_summary;
}
