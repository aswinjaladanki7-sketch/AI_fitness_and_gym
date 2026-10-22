/**
 * AI GYM & FITNESS ASSISTANT - Main UI & Global Utilities
 * SRM INSTITUTE OF SCIENCE AND TECHNOLOGY | 21CSC203P
 */

document.addEventListener('DOMContentLoaded', () => {
  renderUniversityBar();
  renderHeader();
  renderMobileNav();
  setupToastContainer();
  highlightActiveNav();
});

function renderUniversityBar() {
  const existing = document.getElementById('university-bar');
  if (existing) return;

  const bar = document.createElement('div');
  bar.id = 'university-bar';
  bar.className = 'university-bar';
  bar.innerHTML = `
    <div class="univ-info">
      <span>SRM INSTITUTE OF SCIENCE AND TECHNOLOGY</span>
      <span>•</span>
      <span class="univ-highlight">21CSC203P - PROJECT</span>
      <span>•</span>
      <span>School of Computer Science & Engineering</span>
    </div>
    <div class="univ-action">
      <button class="btn btn-outline btn-sm" onclick="openProjectReviewModal()" style="padding: 0.2rem 0.6rem; font-size: 0.72rem; border-color: rgba(0,212,255,0.3); color: var(--accent-blue);">
        <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path><polyline points="14 2 14 8 20 8"></polyline><line x1="16" y1="13" x2="8" y2="13"></line><line x1="16" y1="17" x2="8" y2="17"></line><polyline points="10 9 9 9 8 9"></polyline></svg>
        Review Viva Info
      </button>
    </div>
  `;
  document.body.prepend(bar);
}

function renderHeader() {
  const headerContainer = document.getElementById('main-header');
  if (!headerContainer) return;

  const token = localStorage.getItem('ai_gym_token');
  const user = JSON.parse(localStorage.getItem('ai_gym_user') || '{}');
  const userName = user.name ? user.name.split(' ')[0] : 'User';

  headerContainer.innerHTML = `
    <div class="header-container">
      <a href="index.html" class="logo-link">
        <div class="logo-icon">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M6 5v14M18 5v14M2 9h4M18 9h4M2 15h4M18 15h4M6 12h12"/></svg>
        </div>
        <div class="logo-text">
          <span>AI GYM</span>
          <span class="logo-sub">& FITNESS</span>
        </div>
      </a>

      <ul class="nav-menu">
        <li><a href="index.html" class="nav-link" data-page="index">Home</a></li>
        <li><a href="dashboard.html" class="nav-link" data-page="dashboard">Dashboard</a></li>
        <li><a href="ai-trainer.html" class="nav-link" data-page="ai-trainer">
          <span style="color: var(--accent-neon);">●</span> AI Trainer
        </a></li>
        <li><a href="fitness.html" class="nav-link" data-page="fitness">Calculator</a></li>
        <li><a href="nutrition.html" class="nav-link" data-page="nutrition">Nutrition</a></li>
        <li><a href="workouts.html" class="nav-link" data-page="workouts">Workouts</a></li>
        <li><a href="progress.html" class="nav-link" data-page="progress">Progress</a></li>
      </ul>

      <div class="nav-actions">
        ${token ? `
          <a href="profile.html" class="btn btn-outline btn-sm">
            <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path><circle cx="12" cy="7" r="4"></circle></svg>
            ${userName}
          </a>
          <button class="btn btn-outline btn-sm" onclick="logout()" style="color: var(--error); border-color: rgba(239,68,68,0.3);">
            Exit
          </button>
        ` : `
          <a href="login.html" class="btn btn-outline btn-sm">Login</a>
          <a href="register.html" class="btn btn-primary btn-sm">Get Started</a>
        `}
      </div>
    </div>
  `;
}

function renderMobileNav() {
  const existing = document.getElementById('mobile-nav-bar');
  if (existing) return;

  const nav = document.createElement('nav');
  nav.id = 'mobile-nav-bar';
  nav.className = 'mobile-nav-bar';
  nav.innerHTML = `
    <a href="dashboard.html" class="mobile-nav-item" data-page="dashboard">
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="3" width="7" height="9"></rect><rect x="14" y="3" width="7" height="5"></rect><rect x="14" y="12" width="7" height="9"></rect><rect x="3" y="16" width="7" height="5"></rect></svg>
      <span>Dashboard</span>
    </a>
    <a href="ai-trainer.html" class="mobile-nav-item" data-page="ai-trainer" style="color: var(--accent-neon);">
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polygon points="23 7 16 12 23 17 23 7"></polygon><rect x="1" y="5" width="15" height="14" rx="2" ry="2"></rect></svg>
      <span>AI Coach</span>
    </a>
    <a href="nutrition.html" class="mobile-nav-item" data-page="nutrition">
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M18 8h1a4 4 0 0 1 0 8h-1"></path><path d="M2 8h16v9a4 4 0 0 1-4 4H6a4 4 0 0 1-4-4V8z"></path><line x1="6" y1="1" x2="6" y2="4"></line><line x1="10" y1="1" x2="10" y2="4"></line><line x1="14" y1="1" x2="14" y2="4"></line></svg>
      <span>Diet</span>
    </a>
    <a href="workouts.html" class="mobile-nav-item" data-page="workouts">
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M6 5v14M18 5v14M2 9h4M18 9h4M2 15h4M18 15h4M6 12h12"/></svg>
      <span>Workouts</span>
    </a>
    <a href="progress.html" class="mobile-nav-item" data-page="progress">
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="23 6 13.5 15.5 8.5 10.5 1 18"></polyline><polyline points="17 6 23 6 23 12"></polyline></svg>
      <span>Progress</span>
    </a>
  `;
  document.body.appendChild(nav);
}

function highlightActiveNav() {
  const currentPath = window.location.pathname.toLowerCase();
  const pageMatch = currentPath.split('/').pop().replace('.html', '') || 'index';

  document.querySelectorAll('.nav-link').forEach(link => {
    if (link.getAttribute('data-page') === pageMatch) {
      link.classList.add('active');
    }
  });

  document.querySelectorAll('.mobile-nav-item').forEach(link => {
    if (link.getAttribute('data-page') === pageMatch) {
      link.classList.add('active');
    }
  });
}

function setupToastContainer() {
  if (!document.getElementById('toast-container')) {
    const container = document.createElement('div');
    container.id = 'toast-container';
    document.body.appendChild(container);
  }
}

function showToast(message, type = 'info') {
  setupToastContainer();
  const container = document.getElementById('toast-container');
  const toast = document.createElement('div');
  toast.className = `toast toast-${type}`;

  const iconSvg = type === 'success' 
    ? `<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="var(--accent-neon)" stroke-width="2.5"><polyline points="20 6 9 17 4 12"></polyline></svg>`
    : (type === 'error' 
      ? `<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="var(--error)" stroke-width="2.5"><circle cx="12" cy="12" r="10"></circle><line x1="15" y1="9" x2="9" y2="15"></line><line x1="9" y1="9" x2="15" y2="15"></line></svg>`
      : `<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="var(--accent-blue)" stroke-width="2.5"><circle cx="12" cy="12" r="10"></circle><line x1="12" y1="16" x2="12" y2="12"></line><line x1="12" y1="8" x2="12.01" y2="8"></line></svg>`);

  toast.innerHTML = `
    ${iconSvg}
    <div style="font-size: 0.9rem; line-height: 1.4;">${message}</div>
  `;
  container.appendChild(toast);

  setTimeout(() => {
    toast.style.opacity = '0';
    toast.style.transform = 'translateY(10px)';
    setTimeout(() => toast.remove(), 300);
  }, 3800);
}

function logout() {
  localStorage.removeItem('ai_gym_token');
  showToast('Logged out successfully.', 'info');
  setTimeout(() => {
    window.location.href = 'login.html';
  }, 600);
}

// College Project Presentation Modal
function openProjectReviewModal() {
  let modal = document.getElementById('project-review-modal');
  if (!modal) {
    modal = document.createElement('div');
    modal.id = 'project-review-modal';
    modal.className = 'modal-backdrop';
    modal.innerHTML = `
      <div class="glass-card" style="max-width: 800px; max-height: 85vh; overflow-y: auto; padding: 2.5rem; border-color: rgba(0,212,255,0.4);">
        <div style="display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 1.5rem;">
          <div>
            <span class="badge badge-blue">SRM IST | 21CSC203P PROJECT REVIEW</span>
            <h2 style="font-size: 1.8rem; margin-top: 0.5rem;">AI GYM & FITNESS ASSISTANT</h2>
            <p style="color: var(--accent-neon); font-size: 0.9rem; font-weight: 600;">"Train Smarter. Eat Better. Move Better."</p>
          </div>
          <button class="btn btn-outline btn-sm" onclick="closeProjectReviewModal()">✕</button>
        </div>

        <div style="display: flex; flex-direction: column; gap: 1.25rem; font-size: 0.92rem; color: var(--text-secondary); line-height: 1.6;">
          <div style="background: rgba(12,16,23,0.7); padding: 1rem; border-radius: var(--radius-md); border-left: 3px solid var(--accent-blue);">
            <strong style="color: var(--text-primary);">Department of Computer Science and Engineering</strong><br>
            School of Computer Science and Engineering, SRM Institute of Science and Technology.
          </div>

          <div>
            <h4 style="color: var(--text-primary); margin-bottom: 0.4rem;">1. Proposed System Architecture</h4>
            <p>Modular client-server pipeline: Responsive HTML5/CSS3/Vanilla JS Frontend ↔ REST APIs ↔ Python Flask Services ↔ Normalized MySQL Database + Computer Vision Engine (MediaPipe Pose 33 Body Landmarks ↔ Real-time Joint Angle Trigonometry ↔ Repetition State Automata ↔ Form Scoring).</p>
          </div>

          <div>
            <h4 style="color: var(--text-primary); margin-bottom: 0.4rem;">2. Core Key Differentiators & Novelty</h4>
            <ul style="padding-left: 1.2rem; display: flex; flex-direction: column; gap: 0.35rem;">
              <li>Unified multi-disciplinary integration: Biomechanical vision coach + personalized macro diet planner + workout programmer.</li>
              <li>Dual Vegetarian and Non-Vegetarian nutrition customization respecting specific allergens.</li>
              <li>Zero latency edge-friendly real-time repetition state machines for Squats, Push-ups, Lunges, Bicep Curls, and Shoulder Press.</li>
              <li>Transparent 0-100 form scoring rubric with live corrective audio/speech cues.</li>
            </ul>
          </div>

          <div>
            <h4 style="color: var(--text-primary); margin-bottom: 0.4rem;">3. Privacy & Technical Feasibility</h4>
            <p>Strict user camera privacy: Video stream stays strictly on-device for real-time coordinate extraction; no raw video frames are permanently recorded or transmitted without consent.</p>
          </div>
        </div>

        <div style="margin-top: 2rem; display: flex; justify-content: flex-end;">
          <button class="btn btn-primary" onclick="closeProjectReviewModal()">Close Review Panel</button>
        </div>
      </div>
    `;
    document.body.appendChild(modal);
  }
  modal.classList.add('active');
}

function closeProjectReviewModal() {
  const modal = document.getElementById('project-review-modal');
  if (modal) modal.classList.remove('active');
}
