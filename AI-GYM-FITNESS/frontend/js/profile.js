/**
 * AI GYM & FITNESS ASSISTANT - User Profile Controller
 * SRM INSTITUTE OF SCIENCE AND TECHNOLOGY
 */

document.addEventListener('DOMContentLoaded', async () => {
  await loadUserProfile();
  initProfileForm();
});

async function loadUserProfile() {
  const res = await window.api.request('/user/profile');
  if (!res.profile) return;

  const u = res.user || {};
  const p = res.profile;

  document.getElementById('profile-name').value = u.name || '';
  document.getElementById('profile-email').value = u.email || '';
  document.getElementById('profile-age').value = p.age || 25;
  document.getElementById('profile-gender').value = p.gender || 'male';
  document.getElementById('profile-height').value = p.height_cm || 175;
  document.getElementById('profile-weight').value = p.weight_kg || 75;
  document.getElementById('profile-target-weight').value = p.target_weight_kg || 70;
  document.getElementById('profile-goal').value = p.fitness_goal || 'weight_loss';
  document.getElementById('profile-workout-type').value = p.workout_type || 'gym';
  document.getElementById('profile-frequency').value = p.workout_frequency || 4;
  document.getElementById('profile-activity').value = p.activity_level || 'moderately_active';
  document.getElementById('profile-diet').value = p.diet_preference || 'vegetarian';
  document.getElementById('profile-allergies').value = p.food_allergies || '';
  document.getElementById('profile-food-prefs').value = p.food_preferences || '';
}

function initProfileForm() {
  const form = document.getElementById('user-profile-form');
  if (!form) return;

  form.addEventListener('submit', async (e) => {
    e.preventDefault();

    const payload = {
      age: parseInt(document.getElementById('profile-age').value),
      gender: document.getElementById('profile-gender').value,
      height_cm: parseFloat(document.getElementById('profile-height').value),
      weight_kg: parseFloat(document.getElementById('profile-weight').value),
      target_weight_kg: parseFloat(document.getElementById('profile-target-weight').value),
      fitness_goal: document.getElementById('profile-goal').value,
      workout_type: document.getElementById('profile-workout-type').value,
      workout_frequency: parseInt(document.getElementById('profile-frequency').value),
      activity_level: document.getElementById('profile-activity').value,
      diet_preference: document.getElementById('profile-diet').value,
      food_allergies: document.getElementById('profile-allergies').value.trim(),
      food_preferences: document.getElementById('profile-food-prefs').value.trim()
    };

    const res = await window.api.request('/user/profile', {
      method: 'PUT',
      body: JSON.stringify(payload)
    });

    if (res.success) {
      showToast("Profile and fitness targets updated successfully!", "success");
    } else {
      showToast(res.message || "Failed to update profile.", "error");
    }
  });
}
