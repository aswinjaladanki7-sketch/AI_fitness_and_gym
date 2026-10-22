/**
 * AI GYM & FITNESS ASSISTANT - Personalized Dashboard Controller
 * SRM INSTITUTE OF SCIENCE AND TECHNOLOGY
 */

let weightChartInstance = null;
let calorieChartInstance = null;

document.addEventListener('DOMContentLoaded', async () => {
  await loadDashboardData();
  renderMiniWorkout();
});

async function loadDashboardData() {
  const profileRes = await window.api.request('/user/profile');
  const progressRes = await window.api.request('/progress');

  const user = profileRes.user || { name: "Aswinkumar J" };
  const profile = profileRes.profile || { weight_kg: 78, target_weight_kg: 70, height_cm: 175 };
  const fitness = profileRes.fitness_goals || window.api.calculateFitnessSync(profile);
  const progress = (progressRes && progressRes.progress) ? progressRes.progress : {};

  // Greeting
  const greetingEl = document.getElementById('user-greeting-name');
  if (greetingEl) greetingEl.innerText = user.name || "Athlete";

  // Card: BMI
  const bmiEl = document.getElementById('dash-bmi-val');
  if (bmiEl) bmiEl.innerText = fitness.bmi || 24.2;

  const bmiCatEl = document.getElementById('dash-bmi-cat');
  if (bmiCatEl) {
    bmiCatEl.innerText = fitness.bmi_category || "Normal";
    bmiCatEl.style.color = fitness.bmi_color || "#10b981";
  }

  // Card: Weight
  const currWeightEl = document.getElementById('dash-curr-weight');
  if (currWeightEl) currWeightEl.innerText = profile.weight_kg || 78;

  const targetWeightEl = document.getElementById('dash-target-weight');
  if (targetWeightEl) targetWeightEl.innerText = profile.target_weight_kg || 70;

  const weightToGo = Math.abs((profile.weight_kg || 78) - (profile.target_weight_kg || 70)).toFixed(1);
  const weightToGoEl = document.getElementById('dash-weight-to-go');
  if (weightToGoEl) weightToGoEl.innerText = weightToGo;

  // Card: Daily Calories & Protein
  const calsEl = document.getElementById('dash-daily-cals');
  if (calsEl) calsEl.innerText = Math.round(fitness.daily_calories || 2000);

  const protEl = document.getElementById('dash-protein-target');
  if (protEl) protEl.innerText = Math.round(fitness.protein_g || 130);

  // Card: Form Score
  const scoreEl = document.getElementById('dash-form-score');
  if (scoreEl) scoreEl.innerText = `${progress.overall_avg_form_score || 92}%`;

  // Goal Progress Percentage
  const startW = progress.start_weight || profile.weight_kg;
  const currentW = profile.weight_kg;
  const targetW = profile.target_weight_kg;
  let pct = 0;
  if (startW !== targetW) {
    pct = Math.min(100, Math.max(5, Math.round(((startW - currentW) / (startW - targetW)) * 100)));
  }
  const fillEl = document.getElementById('dash-goal-pct-fill');
  if (fillEl) fillEl.style.width = `${pct}%`;

  const pctLabel = document.getElementById('dash-goal-pct-label');
  if (pctLabel) pctLabel.innerText = `${pct}% completed`;

  // Render Charts
  renderDashboardCharts(progress.history || []);
}

function renderDashboardCharts(history) {
  const chartCanvas = document.getElementById('dash-weight-chart');
  if (!chartCanvas || !window.Chart) return;

  const labels = history.map(h => {
    const d = new Date(h.logged_date);
    return `${d.getDate()}/${d.getMonth() + 1}`;
  });
  const weights = history.map(h => h.weight_kg);

  if (weightChartInstance) {
    weightChartInstance.destroy();
  }

  const ctx = chartCanvas.getContext('2d');
  const gradient = ctx.createLinearGradient(0, 0, 0, 260);
  gradient.addColorStop(0, 'rgba(0, 245, 155, 0.4)');
  gradient.addColorStop(1, 'rgba(0, 245, 155, 0.0)');

  weightChartInstance = new Chart(chartCanvas, {
    type: 'line',
    data: {
      labels: labels.length ? labels : ['W1', 'W2', 'W3', 'W4', 'Current'],
      datasets: [{
        label: 'Body Weight (kg)',
        data: weights.length ? weights : [80.5, 79.8, 79.0, 78.5, 78.0],
        borderColor: '#00f59b',
        borderWidth: 3,
        pointBackgroundColor: '#00f59b',
        pointBorderColor: '#07090e',
        pointBorderWidth: 2,
        pointRadius: 5,
        backgroundColor: gradient,
        fill: true,
        tension: 0.35
      }]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: { display: false },
        tooltip: {
          backgroundColor: 'rgba(12, 16, 23, 0.95)',
          titleColor: '#f8fafc',
          bodyColor: '#00f59b',
          borderColor: 'rgba(0, 245, 155, 0.3)',
          borderWidth: 1,
          padding: 10
        }
      },
      scales: {
        x: {
          grid: { color: 'rgba(255, 255, 255, 0.05)' },
          ticks: { color: '#94a3b8' }
        },
        y: {
          grid: { color: 'rgba(255, 255, 255, 0.05)' },
          ticks: { color: '#94a3b8' }
        }
      }
    }
  });
}

async function renderMiniWorkout() {
  const container = document.getElementById('dash-mini-workout-list');
  if (!container) return;

  const res = await window.api.request('/workout/today');
  const exercises = (res.routine && res.routine.exercises) ? res.routine.exercises.slice(0, 3) : [];

  container.innerHTML = exercises.map(ex => `
    <div class="mini-exercise-item">
      <div class="mini-exercise-info">
        <div style="font-weight: 600; color: var(--text-primary);">${ex.name}</div>
        ${ex.has_ai_camera ? `<span class="mini-badge-ai">AI VISION</span>` : ''}
      </div>
      <div style="color: var(--text-secondary); font-size: 0.82rem;">${ex.sets} sets × ${ex.reps}</div>
    </div>
  `).join('');
}
