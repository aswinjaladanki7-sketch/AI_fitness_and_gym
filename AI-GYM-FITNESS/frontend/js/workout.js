/**
 * AI GYM & FITNESS ASSISTANT - Workout Planner Controller
 * SRM INSTITUTE OF SCIENCE AND TECHNOLOGY
 */

let currentGoal = 'weight_loss';
let currentLocation = 'gym';

document.addEventListener('DOMContentLoaded', async () => {
  initWorkoutControls();
  await loadInitialWorkoutRoutine();
});

function initWorkoutControls() {
  const goalSelect = document.getElementById('workout-goal-filter');
  const locationSelect = document.getElementById('workout-location-filter');

  if (goalSelect) {
    goalSelect.addEventListener('change', (e) => {
      currentGoal = e.target.value;
      fetchWorkoutRoutine();
    });
  }

  if (locationSelect) {
    locationSelect.addEventListener('change', (e) => {
      currentLocation = e.target.value;
      fetchWorkoutRoutine();
    });
  }
}

async function loadInitialWorkoutRoutine() {
  const profileRes = await window.api.request('/user/profile');
  if (profileRes.profile) {
    currentGoal = profileRes.profile.fitness_goal || 'weight_loss';
    currentLocation = profileRes.profile.workout_type || 'gym';

    const goalSelect = document.getElementById('workout-goal-filter');
    const locationSelect = document.getElementById('workout-location-filter');
    if (goalSelect) goalSelect.value = currentGoal;
    if (locationSelect) locationSelect.value = currentLocation;
  }

  await fetchWorkoutRoutine();
}

async function fetchWorkoutRoutine() {
  const res = await window.api.request('/workout/generate', {
    method: 'POST',
    body: JSON.stringify({
      goal: currentGoal,
      location: currentLocation
    })
  });

  if (res.success && res.routine) {
    renderWorkoutRoutine(res.routine);
  }
}

function renderWorkoutRoutine(routine) {
  // Update Routine Banner
  document.getElementById('routine-title').innerText = routine.title;
  document.getElementById('routine-focus').innerText = `Target Focus: ${routine.focus}`;
  document.getElementById('routine-days').innerText = `${routine.frequency_days_per_week} Days/wk`;
  document.getElementById('routine-duration').innerText = `${routine.estimated_duration_min} min`;
  document.getElementById('routine-count').innerText = `${routine.total_exercises} Exercises`;

  const container = document.getElementById('workout-exercises-container');
  if (!container) return;

  container.innerHTML = routine.exercises.map(ex => `
    <div class="exercise-card ${ex.has_ai_camera ? 'neon-glow' : ''}">
      <div class="exercise-top">
        <div class="exercise-title-group">
          <h3>
            ${ex.name}
            ${ex.has_ai_camera ? `<span class="badge badge-neon" style="font-size: 0.68rem;">AI CAMERA READY</span>` : ''}
          </h3>
          <div class="exercise-muscle-tag">Target: ${ex.target_muscle}</div>
        </div>
        <span class="badge ${ex.difficulty === 'beginner' ? 'badge-blue' : 'badge-neon'}">
          ${ex.difficulty}
        </span>
      </div>

      <div class="exercise-specs-grid">
        <div class="spec-cell">
          <span class="spec-val">${ex.sets}</span>
          <span class="spec-lbl">SETS</span>
        </div>
        <div class="spec-cell">
          <span class="spec-val">${ex.reps}</span>
          <span class="spec-lbl">REPETITIONS</span>
        </div>
        <div class="spec-cell">
          <span class="spec-val">${ex.rest_seconds}s</span>
          <span class="spec-lbl">REST</span>
        </div>
      </div>

      <div class="exercise-instructions">
        <strong>Execution:</strong> ${ex.instructions}
      </div>

      <div class="exercise-card-footer">
        <div style="font-size: 0.78rem; color: var(--text-muted);">
          Equipment: <strong style="color: var(--text-secondary);">${ex.equipment}</strong>
        </div>
        ${ex.has_ai_camera ? `
          <a href="ai-trainer.html?exercise=${ex.slug}" class="btn btn-primary btn-sm">
            Launch AI Coach ➜
          </a>
        ` : `
          <span style="font-size: 0.78rem; color: var(--text-muted);">Standard Exercise</span>
        `}
      </div>
    </div>
  `).join('');
}
