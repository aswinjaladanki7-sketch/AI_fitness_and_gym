/**
 * AI GYM & FITNESS ASSISTANT - Progress Tracker Controller
 * SRM INSTITUTE OF SCIENCE AND TECHNOLOGY
 */

let weightTrendChart = null;
let workoutSessionsChart = null;

document.addEventListener('DOMContentLoaded', async () => {
  initProgressForm();
  await loadProgressData();
});

function initProgressForm() {
  const form = document.getElementById('log-weight-form');
  const dateInput = document.getElementById('log-date-input');
  if (dateInput) {
    dateInput.value = new Date().toISOString().split('T')[0];
  }

  if (form) {
    form.addEventListener('submit', async (e) => {
      e.preventDefault();
      const weight = parseFloat(document.getElementById('log-weight-input')?.value || 0);
      const date = document.getElementById('log-date-input')?.value;
      const notes = (document.getElementById('log-notes-input')?.value || '').trim();

      if (isNaN(weight) || weight < 30 || weight > 250) {
        showToast("Please enter a valid weight between 30 and 250 kg.", "error");
        return;
      }

      const res = await window.api.request('/progress/weight', {
        method: 'POST',
        body: JSON.stringify({ weight_kg: weight, logged_date: date, notes })
      });

      if (res.success) {
        showToast("Weight logged successfully!", "success");
        form.reset();
        if (dateInput) dateInput.value = new Date().toISOString().split('T')[0];
        await loadProgressData();
      } else {
        showToast(res.message || "Failed to log weight.", "error");
      }
    });
  }
}

async function loadProgressData() {
  const res = await window.api.request('/progress');
  if (!res.success || !res.progress) return;

  const p = res.progress;

  // Milestone Cards
  document.getElementById('prog-start-weight').innerText = `${p.start_weight} kg`;
  document.getElementById('prog-current-weight').innerText = `${p.current_weight} kg`;
  document.getElementById('prog-target-weight').innerText = `${p.target_weight} kg`;
  document.getElementById('prog-weight-lost').innerText = `${p.weight_lost} kg`;
  document.getElementById('prog-weight-remaining').innerText = `${p.weight_remaining} kg`;

  // Render Charts
  renderProgressCharts(p.history || []);

  // Populate Table
  renderHistoryTable(p.history || []);
}

function renderProgressCharts(history) {
  const weightCanvas = document.getElementById('prog-weight-chart');
  if (weightCanvas && window.Chart) {
    if (weightTrendChart) weightTrendChart.destroy();

    const labels = history.map(h => h.logged_date);
    const weights = history.map(h => h.weight_kg);

    const ctx = weightCanvas.getContext('2d');
    const grad = ctx.createLinearGradient(0, 0, 0, 280);
    grad.addColorStop(0, 'rgba(0, 245, 155, 0.35)');
    grad.addColorStop(1, 'rgba(0, 245, 155, 0.0)');

    weightTrendChart = new Chart(weightCanvas, {
      type: 'line',
      data: {
        labels: labels,
        datasets: [{
          label: 'Weight (kg)',
          data: weights,
          borderColor: '#00f59b',
          backgroundColor: grad,
          borderWidth: 3,
          fill: true,
          tension: 0.3,
          pointBackgroundColor: '#00f59b',
          pointRadius: 5
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: { legend: { display: false } },
        scales: {
          x: { grid: { color: 'rgba(255, 255, 255, 0.05)' }, ticks: { color: '#94a3b8' } },
          y: { grid: { color: 'rgba(255, 255, 255, 0.05)' }, ticks: { color: '#94a3b8' } }
        }
      }
    });
  }
}

function renderHistoryTable(history) {
  const tbody = document.getElementById('prog-history-tbody');
  if (!tbody) return;

  const reversed = [...history].reverse();
  tbody.innerHTML = reversed.map(entry => `
    <tr>
      <td><strong style="color: var(--text-primary);">${entry.logged_date}</strong></td>
      <td><span style="color: var(--accent-neon); font-weight: 700;">${entry.weight_kg} kg</span></td>
      <td><span class="badge badge-neon" style="font-size: 0.75rem;">${entry.bmi}</span></td>
      <td style="color: var(--text-secondary);">${entry.notes || '—'}</td>
    </tr>
  `).join('');
}
