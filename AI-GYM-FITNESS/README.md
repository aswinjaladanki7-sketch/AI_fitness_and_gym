# AI GYM & FITNESS ASSISTANT

> **Tagline:** *"Train Smarter. Eat Better. Move Better."*

---

### Academic Information
- **Institution:** SRM INSTITUTE OF SCIENCE AND TECHNOLOGY
- **Faculty:** Faculty of Engineering and Technology
- **School:** School of Computer Science and Engineering
- **Department:** Department of Computer Science and Engineering
- **Course Code & Title:** 21CSC203P - PROJECT
- **Project Domain:** Artificial Intelligence, Computer Vision, Full-Stack Web Development, Health Informatics

---

## 1. Introduction

The modern lifestyle and increasing sedentary habits have amplified the necessity of structured physical exercise and balanced nutritional intake. While traditional gym memberships provide access to heavy equipment, personal coaching remains financially inaccessible to a vast majority of the population. Furthermore, unsupervised home and gym workouts frequently lead to acute musculoskeletal injuries, poor biomechanical adaptations, and stagnation due to improper exercise form and lack of progressive overload tracking.

**AI GYM & FITNESS ASSISTANT** is a full-stack, computer-vision-powered health and fitness ecosystem designed to bridge this accessibility gap. It integrates kinematic pose detection, real-time biomechanical analysis, automated repetition counting, tailored vegetarian and non-vegetarian diet synthesis, and long-term biometric progress tracking into a single client-server web platform.

---

## 2. Problem Statement

1. **High Cost & Inaccessibility of Personal Trainers:** Certified strength coaches and clinical nutritionists charge prohibitive rates, preventing wider adoption of guided fitness regimens.
2. **Risk of Injury from Improper Movement Patterns:** Performing compound multi-joint exercises (such as squats, push-ups, and lunges) with suboptimal spinal alignment or improper joint angles causes chronic joint shear, disc herniation, and tendonitis.
3. **Impersonal & Generic Diet Plans:** Most fitness software neglects cultural and dietary preferences (such as strictly Indian vegetarian diets, plant proteins, and specific food allergies like lactose or gluten intolerance).
4. **Lack of Objective Real-Time Feedback:** Static video tutorials provide one-way passive consumption with zero corrective feedback on actual user posture during exercise execution.

---

## 3. Objectives

- **Biomechanical Pose Tracking:** Leverage 33 full-body kinematic landmarks using Google MediaPipe Pose and OpenCV to detect exercise movement paths in real time.
- **Automated Repetition Counting:** Build finite-state machine (FSM) automata that evaluate eccentric and concentric phases, preventing false counts.
- **Kinematic Form Scoring:** Formulate an objective 0–100 form scoring rubric based on joint angles (knee flexion, spinal curvature, elbow alignment, and shoulder stability).
- **Personalized Nutrition Engine:** Synthesize 5-meal daily and 7-day rotational diet protocols respecting vegetarian/non-vegetarian preferences, target calories, macronutrients, and food allergies.
- **Biometric Telemetry & Progress Monitoring:** Track historical bodyweight, BMI categorizations, workout consistency, and caloric burn over time.

---

## 4. Scope

- **Supported Exercises (Vision Engine):** Squats, Push-ups, Lunges, Bicep Curls, and Shoulder Press.
- **Target Users:** Gym-goers, home-workout enthusiasts, university students, sedentary professionals seeking structured wellness programs.
- **Platform Compatibility:** Responsive across desktop, laptop, tablet, and mobile browsers with front-facing camera support.
- **Ethical Privacy Boundaries:** Strict edge-friendly local video frame processing where zero raw video is stored or transmitted.

---

## 5. Literature Survey & Base Papers

| S.No. | Paper Title / Reference | Authors & Year | Key Methodology | Limitations Identified in Prior Art |
|---|---|---|---|---|
| 1 | *MediaPipe: A Framework for Building Perception Pipelines* | Lugaresi et al. (Google Research, 2019) | Graph-based real-time perception pipeline for pose landmarks. | Focused on landmark inference rather than domain-specific exercise state transitions. |
| 2 | *Deep Learning Based Human Pose Estimation for Fitness* | Chen et al. (IEEE Access, 2021) | OpenPose and 2D joint angle analysis for bodyweight training. | High GPU latency; lack of integration with nutritional planning. |
| 3 | *A New Predictive Equation for Resting Energy Expenditure in Healthy Individuals* | Mifflin, M. D., St Jeor, S. T. et al. (Am. J. Clin. Nutr., 1990) | Validated BMR estimation based on weight, height, age, and sex. | Static clinical formula requiring automated software translation for athletic goals. |
| 4 | *Computer Vision-Assisted Physical Rehabilitation* | O'Reilly et al. (Sensors, 2020) | Wearable IMU sensors combined with spatial camera tracking. | Requires expensive hardware sensors; impractical for casual users. |

---

## 6. Proposed System Architecture

```
                    AI GYM & FITNESS ASSISTANT
                               |
             ┌─────────────────┴─────────────────┐
             |                                   |
         FRONTEND                             BACKEND
    HTML5 / CSS3 / JS                      Python Flask
             |                                   |
             |              ┌────────────────────┼────────────────────┐
             |              |                    |                    |
             |       Fitness Calculation     AI Nutrition       AI Vision Coach
             |             Engine               Engine             (MediaPipe)
             |              |                    |                    |
             |              └────────────────────┼────────────────────┘
             |                                   |
             |                            MySQL Database
             |                       (ai_gym_fitness schema)
             |
             └────────────────── REST APIs ──────────────────────┘
```

### Biomechanical AI Vision Pipeline:
```
CAMERA STREAM
      ↓
VIDEO FRAME (RGB)
      ↓
MEDIAPIPE POSE DETECTION (33 Body Landmarks)
      ↓
NORMALIZED 3D SPATIAL COORDINATES (x, y, z, visibility)
      ↓
JOINT ANGLE CALCULATION (Vector Trigonometry at Vertices)
      ↓
FINITE STATE MACHINE (State Transitions: Standing → Descending → Bottom → Ascending)
      ↓
REPETITION COUNTER (+1 on debounced concentric lockout)
      ↓
BIOMECHANICAL FORM ANALYSIS (Depth, Spinal Neutrality, Elbow Flare)
      ↓
0–100 FORM SCORE & LIVE VISUAL/AUDIO SPEECH FEEDBACK
```

---

## 7. Methods & Algorithms

1. **User Biometric Collection:** Height ($H$ in cm), Weight ($W$ in kg), Age ($A$ in years), Gender, Activity Level ($L$), Fitness Objective, Diet Preference, and Allergies.
2. **BMI Computation:**
   $$\text{BMI} = \frac{W}{(H/100)^2}$$
   - Below 18.5: *Underweight*
   - 18.5 – 24.9: *Normal Weight*
   - 25.0 – 29.9: *Overweight*
   - 30.0+: *Obesity*
3. **BMR Computation (Mifflin-St Jeor):**
   - For Males: $\text{BMR} = 10W + 6.25H - 5A + 5$
   - For Females: $\text{BMR} = 10W + 6.25H - 5A - 161$
4. **TDEE & Caloric Allocation:**
   $$\text{TDEE} = \text{BMR} \times \text{Multiplier}$$
   - Sedentary: $1.2$ | Lightly Active: $1.375$ | Moderately Active: $1.55$ | Very Active: $1.725$ | Extra Active: $1.9$
   - *Weight Loss:* Target = $\max(\text{TDEE} - 450, \text{Floor})$ (preventing extreme starvation deficits).
   - *Muscle Building:* Target = $\text{TDEE} + 350$.
5. **Joint Angle Trigonometry:**
   For points $A(x_1, y_1)$, vertex $B(x_2, y_2)$, and $C(x_3, y_3)$:
   $$\vec{BA} = (x_1 - x_2, y_1 - y_2), \quad \vec{BC} = (x_3 - x_2, y_3 - y_2)$$
   $$\theta = \arccos\left(\frac{\vec{BA} \cdot \vec{BC}}{\|\vec{BA}\| \|\vec{BC}\|}\right) \times \frac{180}{\pi}$$
6. **Form Scoring Rubric:**
   - **90–100:** *Excellent Form* (Green Accent)
   - **75–89:** *Good Form* (Cyan Accent)
   - **60–74:** *Needs Improvement* (Amber Warning)
   - **Below 60:** *Poor Form* (Red Alert)

---

## 8. Modules Breakdown

1. **Authentication Module (`routes/auth.py`):**
   - Multi-step onboarding, SHA-256 salted password hashing, JWT bearer token issuance.
2. **User Profile Module (`routes/user.py`):**
   - Biometric record management, activity level updates, and dietary profile preferences.
3. **Fitness Calculation Engine (`services/bmi_service.py`):**
   - Computes BMI, BMR, TDEE, macronutrient distribution (protein, carbohydrates, dietary fats), and progress timelines.
4. **AI Nutrition Module (`services/nutrition_service.py`):**
   - Synthesizes 5 daily meal slots (Breakfast, Mid-Morning Snack, Lunch, Evening Snack, Dinner) with vegetarian vs non-vegetarian partitioning and allergen exclusion.
5. **Workout Planner Module (`services/workout_service.py`):**
   - Generates periodized exercise routines based on goal (Weight Loss, Strength, Hypertrophy, Mobility) and location (Gym / Home).
6. **AI Computer Vision Coach (`ai/` & `js/ai-trainer.js`):**
   - Real-time MediaPipe skeletal rendering, joint angle evaluation, debounced repetition counting, audio speech coaching (`speechSynthesis`), and workout session summaries.
7. **Progress Tracking Module (`routes/progress.py` & `js/progress.js`):**
   - Historical weigh-in logging, BMI trend charts via Chart.js, workout frequency, and total calories burned.

---

## 9. Technology Stack

- **Frontend:** HTML5, CSS3 (Vanilla design tokens, Glassmorphism, CSS Grid/Flexbox), Vanilla JavaScript (ES6+).
- **Backend:** Python 3.10+ with Flask RESTful API, Flask-CORS.
- **Database:** MySQL 8.0+ (relational normalized tables with foreign keys and indexes) with automated SQLite fallback.
- **Computer Vision & ML:** Google MediaPipe Pose, OpenCV, WebGL / HTML5 Canvas API, Web Speech API.
- **Telemetry & Visualization:** Chart.js.

---

## 10. Database Schema (`ai_gym_fitness`)

The database is fully normalized in third normal form (3NF):
- `users`: Core authentication identity credentials.
- `user_profiles`: Detailed biometrics (height, weight, target weight, activity, fitness goal, dietary preferences).
- `fitness_goals`: Calculated BMR, TDEE, calorie target, macro requirements, estimated duration.
- `food_items`: Seed database of Indian and continental vegetarian and non-vegetarian foods with detailed macros and allergen tags.
- `diet_plans` & `diet_meals`: Structured daily and weekly nutrition schedules.
- `exercises`: Catalog of exercises with target muscles, difficulty, instructions, and AI vision flags.
- `workout_sessions` & `ai_workout_sessions`: Workout history with duration, total reps, average form score, and calories burned.
- `weight_history`: Longitudinal weigh-in data points with calculated BMIs and athlete notes.

---

## 11. Novelty & Key Differentiators

1. **Unified Biomechanical & Nutritional Ecosystem:** Unlike existing apps that separate workouts from diet, AI Gym & Fitness combines computer vision form correction with direct metabolic diet synthesis in one platform.
2. **Culturally Informed Indian & Global Dietetics:** Native support for Indian vegetarian options (Paneer, Dal, Soya chunks, Rajma, Chana, Foxnuts) as well as non-vegetarian options (Eggs, Chicken, Salmon, Tuna).
3. **Zero-Latency Client-Side Kinematic Inference:** By leveraging browser-level MediaPipe pipelines and WebGL canvas acceleration, the app achieves smooth 60 FPS skeleton tracking without server video streaming lag.
4. **Transparent Biomechanical Rubrics:** Rather than a black-box percentage, the system explains *why* a form score was deducted (e.g., knee caving, excessive lumbar flexion, elbow flaring).

---

## 12. Feasibility & Viability Analysis

| Challenge | Impact | Technical Mitigation Implemented |
|---|---|---|
| **Low-light Camera Conditions** | Landmark jitter or dropouts | Landmark confidence filtering ($\text{visibility} > 0.4$) and smoothing filters. |
| **Partial Body Occlusion** | Broken angle calculation | Dynamic fallback to most visible side (Left vs Right hip/shoulder comparison). |
| **Varying Body Morphology** | False angle thresholds | Normalized angular vector ratios rather than absolute pixel distances. |
| **Video Privacy Concerns** | User hesitation to permit camera | **100% on-device processing:** No raw video stream or photos leave the user's browser. |

---

## 13. Limitations

- Does not replace emergency medical diagnosis or treatment for chronic metabolic illnesses.
- 2D single-camera tracking cannot fully evaluate depth rotation as accurately as multi-camera motion capture rigs.
- Exercises requiring a perpendicular view (e.g. side-view squats) depend on user positioning the camera at proper height.

---

## 14. Future Enhancements

- **3D Biomechanical Kinematics:** Integration of depth-sensing cameras (e.g., LiDAR / RealSense) for 3D rotational torque evaluation.
- **Heart Rate Monitor BLE Sync:** Bluetooth Low Energy integration with smartwatches (Apple Watch, WearOS) for live heart-rate zone tracking.
- **Multi-Lingual Voice Coach:** Voice corrections translated into Tamil, Hindi, Telugu, and other regional languages.
- **Barbell Path Tracker:** Object detection for barbell trajectory analysis during deadlifts and bench presses.

---

## 15. Setup & Installation Guide

### Prerequisites
- Modern Web Browser (Google Chrome, Microsoft Edge, Firefox, or Safari).
- Python 3.10+ (optional for backend REST API; frontend runs independently via zero-dependency launcher).
- MySQL Server 8.0+ (optional; system includes automatic SQLite fallback).

### Quick 1-Click Launch (Windows)
Double-click `run_app.bat` in the project root:
```cmd
run_app.bat
```
This automatically starts the local web server and opens the application in your browser at `http://localhost:8080/index.html`.

### Manual Python Flask Backend Setup
1. Open a terminal in `AI-GYM-FITNESS/backend/`:
   ```bash
   pip install -r requirements.txt
   ```
2. Configure `.env` with your database credentials:
   ```env
   DB_HOST=localhost
   DB_PORT=3306
   DB_USER=root
   DB_PASSWORD=
   DB_NAME=ai_gym_fitness
   USE_SQLITE_FALLBACK=True
   ```
3. Initialize and run the REST API:
   ```bash
   python app.py
   ```
4. Access the REST API at `http://localhost:5000/api/health`.

---

## 16. References

1. Lugaresi, C. et al. (2019). *MediaPipe: A Framework for Building Perception Pipelines*. arXiv preprint arXiv:1906.08172.
2. Mifflin, M. D. et al. (1990). *A new predictive equation for resting energy expenditure in healthy individuals*. The American Journal of Clinical Nutrition, 51(2), 241-247.
3. World Health Organization (2020). *WHO guidelines on physical activity and sedentary behaviour*. World Health Organization.
4. Helms, E. R. et al. (2014). *Evidence-based recommendations for natural bodybuilding contest preparation: nutrition and supplementation*. Journal of the International Society of Sports Nutrition, 11(1), 20.
