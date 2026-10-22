package com.aigym;

import com.aigym.ai.FormAnalyzer;
import com.aigym.ai.JointAngleCalculator;
import com.aigym.ai.Point;
import com.aigym.ai.RepCounterFSM;
import com.aigym.api.FitnessHttpServer;
import com.aigym.db.FitnessDAO;
import com.aigym.model.*;
import com.aigym.service.BMIService;
import com.aigym.service.NutritionService;
import com.aigym.service.WorkoutService;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;
import java.util.Scanner;

/**
 * AI GYM & FITNESS ASSISTANT - Main Application Driver
 * 
 * Institution: SRM INSTITUTE OF SCIENCE AND TECHNOLOGY
 * Department:  Department of Computer Science and Engineering
 * Course:      21CSC203P - PROJECT (Advanced Programming Practice)
 * Domain:      AI Health Informatics, Biomechanical Vision, Full-Stack Architecture
 */
public class Main {

    private static final FitnessDAO dao = new FitnessDAO();
    private static User currentUser = null;
    private static FitnessHttpServer restServer = null;

    public static void main(String[] args) {
        printBanner();
        seedDefaultDemoUser();

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            printMainMenu();
            System.out.print("Select an option [1-8]: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    handleUserAuth(scanner);
                    break;
                case "2":
                    handleBiometricCalculation(scanner);
                    break;
                case "3":
                    handleDietSynthesis(scanner);
                    break;
                case "4":
                    handleWorkoutSynthesis(scanner);
                    break;
                case "5":
                    handleAIVisionSimulation(scanner);
                    break;
                case "6":
                    handleTelemetryDashboard();
                    break;
                case "7":
                    handleToggleRestServer(scanner);
                    break;
                case "8":
                    System.out.println("\n[!] Thank you for using AI Gym & Fitness Assistant. Stay healthy!");
                    if (restServer != null) restServer.stop();
                    running = false;
                    break;
                default:
                    System.out.println("[X] Invalid selection. Please enter a number between 1 and 8.");
            }
        }
        scanner.close();
    }

    private static void printBanner() {
        System.out.println("==========================================================================");
        System.out.println("          AI GYM & FITNESS ASSISTANT - JAVA ENTERPRISE CORE");
        System.out.println("               SRM INSTITUTE OF SCIENCE AND TECHNOLOGY");
        System.out.println("        Department of Computer Science and Engineering | 21CSC203P");
        System.out.println("==========================================================================");
        System.out.println("Persistence Mode: " + (dao.isUsingJdbc() ? "[CONNECTED TO MYSQL 8.0+]" : "[IN-MEMORY DAO MODE]"));
        System.out.println("--------------------------------------------------------------------------\n");
    }

    private static void printMainMenu() {
        System.out.println("\n============================= MAIN MENU ==================================");
        System.out.println(" Active User: " + (currentUser != null ? currentUser.getName() + " (" + currentUser.getEmail() + ")" : "Guest / Not Logged In"));
        System.out.println("--------------------------------------------------------------------------");
        System.out.println(" 1. User Registration & Authentication");
        System.out.println(" 2. Biometric Calculator (BMI, BMR, TDEE, Calorie Deficit/Surplus)");
        System.out.println(" 3. AI Nutrition Planner (5-Meal Veg/Non-Veg & Allergy Filter)");
        System.out.println(" 4. AI Workout Routine Generator (Custom Splits & Periodization)");
        System.out.println(" 5. AI Vision Rep Counter & Form Analyzer Simulation");
        System.out.println(" 6. View User Telemetry & Workout Session History");
        System.out.println(" 7. Launch / Stop Embedded REST API Server");
        System.out.println(" 8. Exit Application");
        System.out.println("==========================================================================");
    }

    private static void seedDefaultDemoUser() {
        String hash = hashPassword("password123");
        User demo = dao.registerUser("Aswinkumar", "aswin@srmist.edu.in", hash);
        currentUser = demo;

        UserProfile profile = new UserProfile(
            demo.getId(), 21, "male", 176.0, 72.0, 68.0, 
            "weight_loss", "gym", 4, "moderately_active", 
            "beginner", "vegetarian", "low carb", "peanuts"
        );
        dao.saveProfile(profile);

        FitnessGoal goal = BMIService.generateFullFitnessGoal(
            demo.getId(), 72.0, 176.0, 21, "male", "moderately_active", "weight_loss", 68.0
        );
        dao.saveFitnessGoal(goal);
    }

    private static void handleUserAuth(Scanner sc) {
        System.out.println("\n--- [1] USER AUTHENTICATION ---");
        System.out.println("1. Login Existing User");
        System.out.println("2. Register New User");
        System.out.print("Select choice [1/2]: ");
        String sub = sc.nextLine().trim();

        if ("1".equals(sub)) {
            System.out.print("Enter Email: ");
            String email = sc.nextLine().trim();
            System.out.print("Enter Password: ");
            String pass = sc.nextLine().trim();

            User u = dao.getUserByEmail(email);
            if (u != null && u.getPasswordHash().equals(hashPassword(pass))) {
                currentUser = u;
                System.out.println("[✓] Login successful! Welcome, " + u.getName());
            } else {
                System.out.println("[X] Invalid email or password!");
            }
        } else if ("2".equals(sub)) {
            System.out.print("Enter Full Name: ");
            String name = sc.nextLine().trim();
            System.out.print("Enter Email Address: ");
            String email = sc.nextLine().trim();
            System.out.print("Create Password: ");
            String pass = sc.nextLine().trim();

            if (name.isEmpty() || email.isEmpty() || pass.isEmpty()) {
                System.out.println("[X] All fields are required.");
                return;
            }

            User nu = dao.registerUser(name, email, hashPassword(pass));
            currentUser = nu;
            System.out.println("[✓] User registered successfully! User ID: " + nu.getId());
        }
    }

    private static void handleBiometricCalculation(Scanner sc) {
        System.out.println("\n--- [2] BIOMETRIC & METABOLIC CALCULATOR ---");
        try {
            System.out.print("Enter Body Weight (kg) [e.g. 74.5]: ");
            double weight = Double.parseDouble(sc.nextLine().trim());
            System.out.print("Enter Standing Height (cm) [e.g. 175]: ");
            double height = Double.parseDouble(sc.nextLine().trim());
            System.out.print("Enter Age in Years [e.g. 21]: ");
            int age = Integer.parseInt(sc.nextLine().trim());
            System.out.print("Enter Biological Gender (male/female/other): ");
            String gender = sc.nextLine().trim();
            System.out.print("Activity Level (sedentary/lightly_active/moderately_active/very_active): ");
            String activity = sc.nextLine().trim();
            System.out.print("Fitness Goal (weight_loss/muscle_building/strength/beginner): ");
            String goal = sc.nextLine().trim();
            System.out.print("Target Body Weight (kg): ");
            double targetWeight = Double.parseDouble(sc.nextLine().trim());

            BMIService.BMIResult bmiRes = BMIService.calculateBMI(weight, height);
            double bmr = BMIService.calculateBMR(weight, height, age, gender);
            double tdee = BMIService.calculateTDEE(bmr, activity);
            BMIService.MacroResult macro = BMIService.calculateCaloriesAndMacros(tdee, weight, goal, gender);
            BMIService.ProjectionResult proj = BMIService.calculateProgressProjection(weight, targetWeight);

            System.out.println("\n==================== BIOMETRIC ASSESSMENT RESULTS ====================");
            System.out.printf(" Quetelet BMI Index   : %.2f kg/m² [%s]\n", bmiRes.bmi, bmiRes.category);
            System.out.printf(" Basal Metabolic Rate : %.1f kcal/day (Mifflin-St Jeor)\n", bmr);
            System.out.printf(" Total Daily Expend.  : %.1f kcal/day (TDEE)\n", tdee);
            System.out.println("----------------------------------------------------------------------");
            System.out.printf(" Prescribed Calories  : %.1f kcal/day\n", macro.dailyCalories);
            System.out.printf(" Protein Allocation   : %.1f grams (%.0f kcal)\n", macro.proteinG, macro.proteinG * 4);
            System.out.printf(" Carbohydrate Split   : %.1f grams (%.0f kcal)\n", macro.carbsG, macro.carbsG * 4);
            System.out.printf(" Essential Fatty Acids: %.1f grams (%.0f kcal)\n", macro.fatG, macro.fatG * 9);
            System.out.println("----------------------------------------------------------------------");
            System.out.printf(" Projection Timeline  : %s\n", proj.summary);
            System.out.println("======================================================================");

            if (currentUser != null) {
                FitnessGoal fg = new FitnessGoal(currentUser.getId(), bmiRes.bmi, bmiRes.category, 
                                                 bmr, tdee, macro.dailyCalories, macro.proteinG, 
                                                 macro.carbsG, macro.fatG, proj.weeks);
                dao.saveFitnessGoal(fg);
                System.out.println("[✓] Biometric metrics persisted to user profile database record.");
            }
        } catch (Exception e) {
            System.out.println("[X] Error in numeric input parsing: " + e.getMessage());
        }
    }

    private static void handleDietSynthesis(Scanner sc) {
        System.out.println("\n--- [3] AI NUTRITION & DIET SYNTHESIS ---");
        int uid = (currentUser != null) ? currentUser.getId() : 1;
        FitnessGoal fg = dao.getLatestGoal(uid);

        double targetCal = (fg != null) ? fg.getDailyCalorieTarget() : 2100.0;
        double targetProt = (fg != null) ? fg.getProteinGrams() : 140.0;
        double targetCarbs = (fg != null) ? fg.getCarbsGrams() : 240.0;
        double targetFat = (fg != null) ? fg.getFatGrams() : 55.0;

        System.out.print("Diet Pathway (vegetarian / non_vegetarian) [default: vegetarian]: ");
        String diet = sc.nextLine().trim();
        if (diet.isEmpty()) diet = "vegetarian";

        System.out.print("Food Allergies to Exclude (comma separated, e.g. peanuts, dairy) [None]: ");
        String allergies = sc.nextLine().trim();

        DietPlan plan = NutritionService.generateDailyDiet(uid, targetCal, targetProt, targetCarbs, targetFat, diet, allergies, 0);

        System.out.println("\n========================= 5-MEAL DAILY DIET PROTOCOL =========================");
        System.out.println(" Protocol Name   : " + plan.getPlanName());
        System.out.printf(" Target Calories : %.0f kcal | Actual Plan: %.1f kcal\n", plan.getTargetCalories(), plan.getTotalActualCalories());
        System.out.printf(" Macros Delivered: Protein: %.1fg | Carbs: %.1fg | Fats: %.1fg\n",
                plan.getTotalActualProtein(), plan.getTotalActualCarbs(), plan.getTotalActualFat());
        System.out.println("------------------------------------------------------------------------------");

        for (DietMeal m : plan.getMeals()) {
            System.out.printf(" • %-14s : %-35s [%s]\n", m.getMealType().toUpperCase(), m.getFoodName(), m.getQuantity());
            System.out.printf("                     -> %.1f kcal | P: %.1fg | C: %.1fg | F: %.1fg\n",
                    m.getCalories(), m.getProteinG(), m.getCarbsG(), m.getFatG());
        }
        System.out.println("==============================================================================");
    }

    private static void handleWorkoutSynthesis(Scanner sc) {
        System.out.println("\n--- [4] AI WORKOUT ROUTINE GENERATOR ---");
        int uid = (currentUser != null) ? currentUser.getId() : 1;

        System.out.print("Fitness Goal (weight_loss / muscle_building / strength / beginner) [weight_loss]: ");
        String goal = sc.nextLine().trim();
        if (goal.isEmpty()) goal = "weight_loss";

        System.out.print("Experience Level (beginner / intermediate / advanced) [beginner]: ");
        String level = sc.nextLine().trim();
        if (level.isEmpty()) level = "beginner";

        System.out.print("Training Venue (gym / home / both) [both]: ");
        String location = sc.nextLine().trim();
        if (location.isEmpty()) location = "both";

        WorkoutPlan plan = WorkoutService.generateWorkoutRoutine(uid, goal, level, location, 4);

        System.out.println("\n======================== TAILORED WORKOUT SPLIT ========================");
        System.out.println(" Split Title : " + plan.getTitle());
        System.out.println(" Core Focus  : " + plan.getFocus());
        System.out.printf(" Frequency   : %d Days/Week | Session Duration: ~%d Minutes\n", 
                plan.getDaysPerWeek(), plan.getEstimatedDurationMinutes());
        System.out.println("------------------------------------------------------------------------");

        int index = 1;
        for (Exercise ex : plan.getExercises()) {
            System.out.printf(" %d. %-16s | Target: %-22s | %d sets x %d reps\n",
                    index++, ex.getName(), ex.getTargetMuscle(), ex.getDefaultSets(), ex.getDefaultReps());
            System.out.printf("    Equipment: %-15s | Rest Interval: %ds%s\n",
                    ex.getEquipment(), ex.getDefaultRestSeconds(), ex.isHasAiCamera() ? " [AI Vision Supported]" : "");
            System.out.println("    Form Cue : " + ex.getFormCues());
        }
        System.out.println("========================================================================");
    }

    private static void handleAIVisionSimulation(Scanner sc) {
        System.out.println("\n--- [5] AI COMPUTER VISION & REPETITION COUNTER SIMULATION ---");
        System.out.println("Select exercise to simulate:");
        System.out.println("1. Squats (Knee & Hip Kinematics)");
        System.out.println("2. Push-ups (Elbow & Spinal Alignment)");
        System.out.println("3. Bicep Curls (Elbow Flexion & Shoulder Stability)");
        System.out.print("Choice [1/2/3]: ");
        String choice = sc.nextLine().trim();

        String exercise = "squats";
        if ("2".equals(choice)) exercise = "push-ups";
        else if ("3".equals(choice)) exercise = "bicep-curls";

        RepCounterFSM fsm = new RepCounterFSM(exercise);

        System.out.println("\n[*] Initialized Biomechanical FSM for " + exercise.toUpperCase());
        System.out.println("[*] Simulating full 3-repetition kinematic movement trajectory...\n");

        // Simulating angular movement samples (standing -> descent -> bottom -> ascent -> standing)
        double[][] simulationAngleFrames;
        if ("squats".equals(exercise)) {
            simulationAngleFrames = new double[][] {
                {170.0, 165.0}, // Standing
                {135.0, 140.0}, // Descending
                {88.0, 95.0},   // Bottom depth
                {120.0, 130.0}, // Ascending
                {168.0, 165.0}, // Completed Rep 1!
                {130.0, 135.0}, // Descending
                {86.0, 92.0},   // Bottom depth
                {125.0, 135.0}, // Ascending
                {170.0, 168.0}, // Completed Rep 2!
                {132.0, 135.0}, // Descending
                {110.0, 85.0},  // Shallow bottom (warning cue!)
                {130.0, 135.0}, // Ascending
                {170.0, 168.0}  // Completed Rep 3 (Poor form)!
            };
        } else if ("push-ups".equals(exercise)) {
            simulationAngleFrames = new double[][] {
                {160.0, 180.0}, // Top plank
                {130.0, 178.0}, // Descending
                {88.0, 177.0},  // Bottom depth
                {120.0, 178.0}, // Ascending
                {162.0, 180.0}, // Completed Rep 1!
                {125.0, 150.0}, // Sagging hips error!
                {85.0, 148.0},  // Bottom with hip sag
                {120.0, 155.0}, // Ascending
                {160.0, 175.0}  // Completed Rep 2!
            };
        } else {
            simulationAngleFrames = new double[][] {
                {160.0, 10.0}, // Extended
                {110.0, 12.0}, // Curling
                {42.0, 15.0},  // Peak contraction
                {95.0, 12.0},  // Lowering
                {155.0, 10.0}, // Completed Rep 1!
                {115.0, 42.0}, // Shoulder swing warning!
                {40.0, 45.0},  // Peak with swing
                {90.0, 30.0},  // Lowering
                {155.0, 12.0}  // Completed Rep 2!
            };
        }

        double totalScore = 0.0;
        int frameCount = 0;

        for (int i = 0; i < simulationAngleFrames.length; i++) {
            double primary = simulationAngleFrames[i][0];
            double secondary = simulationAngleFrames[i][1];

            FormAnalyzer.FormResult form = FormAnalyzer.analyzeForm(exercise, primary, secondary, fsm.getState());
            RepCounterFSM.RepUpdateResult res = fsm.update(primary, "correct".equals(form.status), form.score);

            totalScore += form.score;
            frameCount++;

            System.out.printf("Frame %02d | Joint Angle: %5.1f° | %s\n", (i + 1), primary, res);
            System.out.printf("         Feedback: %-35s [Form: %s]\n", form.feedback, form.status.toUpperCase());
            if (!form.cues.isEmpty()) {
                System.out.println("         Cues    : " + form.cues);
            }

            try { Thread.sleep(200); } catch (InterruptedException ignored) {}
        }

        double avgScore = Math.round((totalScore / frameCount) * 10.0) / 10.0;
        String cat = (avgScore >= 85.0) ? "excellent" : (avgScore >= 70.0 ? "good" : "needs_improvement");
        double calories = fsm.getRepCount() * 0.45; // ~0.45 kcal per compound repetition

        System.out.println("\n==================== WORKOUT SUMMARY REPORT ====================");
        System.out.printf(" Exercise         : %s\n", exercise.toUpperCase());
        System.out.printf(" Reps Completed   : %d (Valid: %d, Invalid: %d)\n", fsm.getRepCount(), fsm.getValidReps(), fsm.getInvalidReps());
        System.out.printf(" Mean Form Score  : %.1f%% (%s)\n", avgScore, cat.toUpperCase());
        System.out.printf(" Estimated Burn   : %.2f kcal\n", calories);
        System.out.println("================================================================");

        int uid = (currentUser != null) ? currentUser.getId() : 1;
        AIWorkoutSession sess = new AIWorkoutSession(
            uid, exercise, fsm.getRepCount(), fsm.getValidReps(), fsm.getInvalidReps(), 
            frameCount * 2, avgScore, cat, calories, "Form analyzed with FSM biomechanical validator"
        );
        dao.logAIWorkoutSession(sess);
        System.out.println("[✓] Session telemetry logged to database successfully.");
    }

    private static void handleTelemetryDashboard() {
        System.out.println("\n--- [6] USER TELEMETRY & WORKOUT HISTORY ---");
        int uid = (currentUser != null) ? currentUser.getId() : 1;
        List<AIWorkoutSession> history = dao.getSessionsForUser(uid);

        if (history.isEmpty()) {
            System.out.println("[i] No workout sessions recorded yet. Try running option 5!");
            return;
        }

        System.out.println("============================== SESSION LOGS ==============================");
        for (AIWorkoutSession s : history) {
            System.out.printf(" • [%s] %-14s | %d Reps (%d valid, %d invalid) | Score: %.1f%% (%s) | %.1f kcal\n",
                    s.getId() > 0 ? "#" + s.getId() : "LOG", s.getExerciseName(), 
                    s.getRepetitionsCompleted(), s.getValidReps(), s.getInvalidReps(), 
                    s.getAverageFormScore(), s.getFormCategory(), s.getCaloriesBurned());
        }
        System.out.println("==========================================================================");
    }

    private static void handleToggleRestServer(Scanner sc) {
        System.out.println("\n--- [7] EMBEDDED REST API SERVER (JDK HttpServer) ---");
        if (restServer == null) {
            System.out.print("Enter port to bind REST API server [default: 5050]: ");
            String pStr = sc.nextLine().trim();
            int port = pStr.isEmpty() ? 5050 : Integer.parseInt(pStr);
            try {
                restServer = new FitnessHttpServer(port);
                restServer.start();
                System.out.println("[✓] Endpoints active:");
                System.out.println("    - GET  http://localhost:" + port + "/api/health");
                System.out.println("    - POST http://localhost:" + port + "/api/fitness/calculate?weight=75&height=175&age=21");
                System.out.println("    - GET  http://localhost:" + port + "/api/nutrition/plan?calories=2200&diet=vegetarian");
                System.out.println("    - GET  http://localhost:" + port + "/api/workout/plan?goal=weight_loss");
            } catch (Exception e) {
                System.out.println("[X] Failed to bind server: " + e.getMessage());
                restServer = null;
            }
        } else {
            System.out.print("Server is currently RUNNING. Stop it now? [y/N]: ");
            if ("y".equalsIgnoreCase(sc.nextLine().trim())) {
                restServer.stop();
                restServer = null;
                System.out.println("[✓] REST API server stopped.");
            }
        }
    }

    private static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            return password;
        }
    }
}
