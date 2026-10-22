package com.aigym.service;

import com.aigym.model.Exercise;
import com.aigym.model.WorkoutPlan;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * AI GYM & FITNESS - Workout Planner Engine
 * 
 * Features:
 * - Evidence-based resistance training splits
 * - Location adaptation (Gym vs Home bodyweight)
 * - Volume (sets x reps) and rest-period periodization
 */
public class WorkoutService {

    public static class SplitConfig {
        public final String title;
        public final String focus;
        public final double restFactor;
        public final int minReps;
        public final int maxReps;
        public final int defaultDays;

        public SplitConfig(String title, String focus, double restFactor, int minReps, int maxReps, int defaultDays) {
            this.title = title;
            this.focus = focus;
            this.restFactor = restFactor;
            this.minReps = minReps;
            this.maxReps = maxReps;
            this.defaultDays = defaultDays;
        }
    }

    private static final List<Exercise> DEFAULT_EXERCISE_LIBRARY = Arrays.asList(
        new Exercise(1, "Squats", "squats", "Quadriceps / Glutes", "Hamstrings, Core", "beginner", "both", "Bodyweight / Dumbbells", 3, 12, 60, true,
            "1. Stand with feet shoulder-width apart.\n2. Push hips back and bend knees.\n3. Descend until thighs parallel to ground.\n4. Drive through heels to stand.",
            "Keep chest up; Knees tracking over toes; Break parallel at bottom"),

        new Exercise(2, "Push-ups", "push-ups", "Chest (Pectorals)", "Triceps, Shoulders, Core", "beginner", "both", "Bodyweight", 3, 10, 60, true,
            "1. Hands slightly wider than shoulder-width.\n2. Form rigid plank line from neck to ankles.\n3. Lower chest to 2 inches off floor.\n4. Push back up forcefully.",
            "Maintain straight spinal line; Do not sag lower back; 45 degree elbow tuck"),

        new Exercise(3, "Lunges", "lunges", "Quadriceps / Hamstrings", "Glutes, Calves", "beginner", "both", "Bodyweight / Dumbbells", 3, 12, 60, true,
            "1. Step forward with lead leg.\n2. Lower hips until both knees bend at 90 degrees.\n3. Keep front knee behind toes.\n4. Drive up to return.",
            "Torso upright; Front knee directly over ankle; Smooth eccentric control"),

        new Exercise(4, "Bicep Curls", "bicep-curls", "Biceps Brachii", "Brachialis, Forearms", "beginner", "both", "Dumbbells / Resistance Band", 3, 12, 45, true,
            "1. Hold weights at sides with palms forward.\n2. Keep elbows pinned to torso.\n3. Curl weights toward shoulders contracting biceps.\n4. Lower with full control.",
            "Pin elbows to ribcage; No swinging or momentum; Squeeze at peak"),

        new Exercise(5, "Shoulder Press", "shoulder-press", "Deltoids", "Triceps, Upper Chest", "intermediate", "both", "Dumbbells", 3, 10, 60, true,
            "1. Hold dumbbells at shoulder height with elbows at 90 degrees.\n2. Press straight overhead until arms extend.\n3. Lower smoothly back to shoulder level.",
            "Do not arch lumbar spine; Lock core; Press in smooth vertical arc"),

        new Exercise(6, "Plank Hold", "plank-hold", "Core / Transverse Abdominis", "Shoulders, Glutes", "beginner", "both", "Bodyweight", 3, 45, 45, false,
            "1. Support body on forearms and toes.\n2. Maintain flat horizontal bridge.\n3. Engage glutes and abdominals continuously.",
            "Neutral cervical spine; Neutral pelvis; Breathe rhythmically"),

        new Exercise(7, "Glute Bridges", "glute-bridges", "Gluteus Maximus", "Hamstrings, Lower Back", "beginner", "both", "Bodyweight", 3, 15, 45, false,
            "1. Lie on back with knees bent and feet flat on floor.\n2. Drive through heels to raise hips.\n3. Squeeze glutes at top and lower under control.",
            "Full hip extension; Squeeze glutes at top; Avoid excessive lower back arch")
    );

    public static List<Exercise> getDefaultExerciseLibrary() {
        return DEFAULT_EXERCISE_LIBRARY;
    }

    public static SplitConfig getSplitConfig(String goal) {
        String g = (goal != null) ? goal.trim().toLowerCase() : "general_fitness";
        switch (g) {
            case "weight_loss":
                return new SplitConfig("High-Intensity Metabolic & Full Body Circuit", "Calorie burn, core stability & lean muscle retention", 0.8, 12, 15, 4);
            case "muscle_building":
                return new SplitConfig("Hypertrophy Upper / Lower Muscle Builder", "Progressive overload, time under tension & muscle volume", 1.2, 8, 12, 5);
            case "strength":
                return new SplitConfig("Pure Strength & Neural Drive Protocol", "Compound multi-joint power, posture & motor recruitment", 1.5, 5, 8, 4);
            case "beginner":
                return new SplitConfig("Foundational Movement Mastery & Mobility", "Form correction, fundamental patterns & joint health", 1.0, 10, 12, 3);
            case "general_fitness":
            default:
                return new SplitConfig("Balanced Vitality & Cardiovascular Conditioning", "Stamina, core strength & functional longevity", 1.0, 10, 14, 3);
        }
    }

    /**
     * Synthesizes tailored WorkoutPlan
     */
    public static WorkoutPlan generateWorkoutRoutine(int userId, String goal, String fitnessLevel, String location, int workoutFrequency) {
        SplitConfig config = getSplitConfig(goal);
        String loc = (location != null) ? location.trim().toLowerCase() : "both";
        String level = (fitnessLevel != null) ? fitnessLevel.trim().toLowerCase() : "beginner";

        // Filter exercises matching location
        List<Exercise> filtered = DEFAULT_EXERCISE_LIBRARY.stream()
            .filter(ex -> "both".equalsIgnoreCase(ex.getLocation()) || "both".equalsIgnoreCase(loc) || ex.getLocation().equalsIgnoreCase(loc))
            .collect(Collectors.toList());

        if (filtered.isEmpty()) {
            filtered = DEFAULT_EXERCISE_LIBRARY;
        }

        int days = (workoutFrequency > 0) ? workoutFrequency : config.defaultDays;
        int durationMin = "beginner".equals(level) ? 45 : 55;

        WorkoutPlan plan = new WorkoutPlan(
            config.title,
            goal,
            config.focus,
            level,
            loc,
            days,
            durationMin
        );
        plan.setUserId(userId);

        for (Exercise base : filtered) {
            int sets = "beginner".equals(level) ? 3 : 4;
            int restSec = (int) Math.round(base.getDefaultRestSeconds() * config.restFactor);

            Exercise routineEx = new Exercise(
                base.getId(),
                base.getName(),
                base.getSlug(),
                base.getTargetMuscle(),
                base.getSecondaryMuscles(),
                base.getDifficulty(),
                base.getLocation(),
                base.getEquipment(),
                sets,
                config.minReps,
                restSec,
                base.isHasAiCamera(),
                base.getInstructions(),
                base.getFormCues()
            );
            plan.addExercise(routineEx);
        }

        return plan;
    }
}
