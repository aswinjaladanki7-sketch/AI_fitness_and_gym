package com.aigym.service;

import com.aigym.model.FitnessGoal;
import java.util.HashMap;
import java.util.Map;

/**
 * AI GYM & FITNESS - Fitness & Biometric Calculation Engine
 * 
 * Implements scientific algorithms:
 * 1. Quetelet Index (BMI)
 * 2. Mifflin-St Jeor Equation (BMR)
 * 3. Harris-Benedict & Schofield TDEE Activity Multipliers
 * 4. Goal-driven Macronutrient Partitioning
 * 5. Safe Physiological Caloric Deficit & Surplus Floors
 */
public class BMIService {

    public static class BMIResult {
        public final double bmi;
        public final String category;
        public final String colorHex;

        public BMIResult(double bmi, String category, String colorHex) {
            this.bmi = bmi;
            this.category = category;
            this.colorHex = colorHex;
        }
    }

    public static class MacroResult {
        public final double dailyCalories;
        public final double proteinG;
        public final double carbsG;
        public final double fatG;

        public MacroResult(double dailyCalories, double proteinG, double carbsG, double fatG) {
            this.dailyCalories = dailyCalories;
            this.proteinG = proteinG;
            this.carbsG = carbsG;
            this.fatG = fatG;
        }
    }

    public static class ProjectionResult {
        public final int weeks;
        public final String summary;

        public ProjectionResult(int weeks, String summary) {
            this.weeks = weeks;
            this.summary = summary;
        }
    }

    /**
     * Calculates Body Mass Index (BMI = weight_kg / height_m^2)
     */
    public static BMIResult calculateBMI(double weightKg, double heightCm) {
        if (heightCm <= 0 || weightKg <= 0) {
            return new BMIResult(0.0, "Invalid Input", "#999999");
        }
        double heightM = heightCm / 100.0;
        double bmi = Math.round((weightKg / (heightM * heightM)) * 100.0) / 100.0;

        String category;
        String color;
        if (bmi < 18.5) {
            category = "Underweight";
            color = "#38bdf8"; // Sky Blue
        } else if (bmi <= 24.99) {
            category = "Normal";
            color = "#10b981"; // Emerald Green
        } else if (bmi <= 29.99) {
            category = "Overweight";
            color = "#f59e0b"; // Amber
        } else {
            category = "Obesity";
            color = "#ef4444"; // Rose Red
        }

        return new BMIResult(bmi, category, color);
    }

    /**
     * Calculates Basal Metabolic Rate using Mifflin-St Jeor formula:
     * Men:   BMR = 10 * weight(kg) + 6.25 * height(cm) - 5 * age + 5
     * Women: BMR = 10 * weight(kg) + 6.25 * height(cm) - 5 * age - 161
     */
    public static double calculateBMR(double weightKg, double heightCm, int age, String gender) {
        double base = (10.0 * weightKg) + (6.25 * heightCm) - (5.0 * age);
        String g = (gender != null) ? gender.trim().toLowerCase() : "male";

        double bmr;
        if ("male".equals(g)) {
            bmr = base + 5.0;
        } else if ("female".equals(g)) {
            bmr = base - 161.0;
        } else {
            bmr = base - 78.0; // Statistical midpoint
        }

        return Math.round(bmr * 100.0) / 100.0;
    }

    /**
     * Calculates Total Daily Energy Expenditure (TDEE) using activity multipliers
     */
    public static double calculateTDEE(double bmr, String activityLevel) {
        Map<String, Double> multipliers = new HashMap<>();
        multipliers.put("sedentary", 1.20);
        multipliers.put("lightly_active", 1.375);
        multipliers.put("moderately_active", 1.55);
        multipliers.put("very_active", 1.725);
        multipliers.put("extra_active", 1.90);

        String act = (activityLevel != null) ? activityLevel.trim().toLowerCase() : "moderately_active";
        double multiplier = multipliers.getOrDefault(act, 1.55);

        return Math.round((bmr * multiplier) * 100.0) / 100.0;
    }

    /**
     * Calculates goal-tailored daily calories and macronutrient partitions
     */
    public static MacroResult calculateCaloriesAndMacros(double tdee, double weightKg, String goal, String gender) {
        String g = (goal != null) ? goal.trim().toLowerCase() : "weight_loss";
        String gen = (gender != null) ? gender.trim().toLowerCase() : "male";

        double dailyCalories;
        double proteinPerKg;

        switch (g) {
            case "weight_loss":
                double rawTarget = tdee - 450.0;
                double safeFloor = "male".equals(gen) ? 1500.0 : 1250.0;
                dailyCalories = Math.max(rawTarget, safeFloor);
                proteinPerKg = 2.0; // Preserve lean mass during deficit
                break;
            case "muscle_building":
                dailyCalories = tdee + 350.0;
                proteinPerKg = 2.2; // Hypertrophy optimal
                break;
            case "strength":
                dailyCalories = tdee + 150.0;
                proteinPerKg = 2.0;
                break;
            case "beginner":
            case "general_fitness":
            default:
                dailyCalories = tdee;
                proteinPerKg = 1.6;
                break;
        }

        dailyCalories = Math.round(dailyCalories * 10.0) / 10.0;

        // Protein calculation
        double proteinG = Math.round((proteinPerKg * weightKg) * 10.0) / 10.0;
        double proteinCalories = proteinG * 4.0;

        // Dietary fats: ~25% total calories (9 kcal/g)
        double fatCalories = dailyCalories * 0.25;
        double fatG = Math.round((fatCalories / 9.0) * 10.0) / 10.0;

        // Carbohydrates: Remaining calories (4 kcal/g)
        double carbCalories = Math.max(dailyCalories - (proteinCalories + fatCalories), 200.0);
        double carbsG = Math.round((carbCalories / 4.0) * 10.0) / 10.0;

        return new MacroResult(dailyCalories, proteinG, carbsG, fatG);
    }

    /**
     * Calculates safe projection weeks to target bodyweight
     */
    public static ProjectionResult calculateProgressProjection(double currentWeight, double targetWeight) {
        double delta = Math.abs(currentWeight - targetWeight);
        if (delta < 0.2) {
            return new ProjectionResult(0, "Goal already achieved!");
        }

        int weeks;
        String summary;
        if (currentWeight > targetWeight) {
            // Safe fat loss pace: 0.5 kg / week
            weeks = Math.max(1, (int) Math.round(delta / 0.5));
            summary = String.format("Estimated %d weeks to safely lose %.1f kg at a sustainable pace of ~0.5 kg/week.", weeks, delta);
        } else {
            // Lean hypertrophy pace: 0.3 kg / week
            weeks = Math.max(1, (int) Math.round(delta / 0.3));
            summary = String.format("Estimated %d weeks to gain %.1f kg of lean mass at a clean pace of ~0.3 kg/week.", weeks, delta);
        }

        return new ProjectionResult(weeks, summary);
    }

    /**
     * Synthesizes full FitnessGoal object
     */
    public static FitnessGoal generateFullFitnessGoal(int userId, double weightKg, double heightCm, 
                                                      int age, String gender, String activityLevel, 
                                                      String goal, double targetWeightKg) {
        BMIResult bmiRes = calculateBMI(weightKg, heightCm);
        double bmr = calculateBMR(weightKg, heightCm, age, gender);
        double tdee = calculateTDEE(bmr, activityLevel);
        MacroResult macro = calculateCaloriesAndMacros(tdee, weightKg, goal, gender);
        ProjectionResult proj = calculateProgressProjection(weightKg, targetWeightKg);

        return new FitnessGoal(
            userId,
            bmiRes.bmi,
            bmiRes.category,
            bmr,
            tdee,
            macro.dailyCalories,
            macro.proteinG,
            macro.carbsG,
            macro.fatG,
            proj.weeks
        );
    }
}
