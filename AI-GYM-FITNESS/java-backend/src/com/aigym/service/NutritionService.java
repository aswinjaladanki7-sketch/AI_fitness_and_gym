package com.aigym.service;

import com.aigym.model.DietMeal;
import com.aigym.model.DietPlan;
import com.aigym.model.FoodItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * AI GYM & FITNESS - Nutrition & Diet Synthesis Service
 * 
 * Features:
 * - 5-Meal partition (Breakfast, Mid-Morning, Lunch, Evening Snack, Dinner)
 * - Strict Vegetarian / Non-Vegetarian segmentation
 * - Dynamic Allergen exclusion (dairy, gluten, nuts, eggs, fish, soy)
 * - Proportional caloric and macronutrient scaling
 */
public class NutritionService {

    private static class MealSlot {
        String type;
        String title;
        double caloriePct;

        MealSlot(String type, String title, double caloriePct) {
            this.type = type;
            this.title = title;
            this.caloriePct = caloriePct;
        }
    }

    private static final List<MealSlot> MEAL_SLOTS = Arrays.asList(
        new MealSlot("breakfast", "Breakfast", 0.25),
        new MealSlot("mid_morning", "Mid-Morning Snack", 0.10),
        new MealSlot("lunch", "Lunch", 0.35),
        new MealSlot("evening_snack", "Evening Snack", 0.10),
        new MealSlot("dinner", "Dinner", 0.20)
    );

    // Built-in authentic food database (matching seed_data.sql)
    private static final List<FoodItem> DEFAULT_FOOD_LIBRARY = Arrays.asList(
        // Vegetarian Breakfast & Snacks
        new FoodItem(1, "Rolled Oats with Almond Milk", "breakfast", true, "1 bowl (50g oats + 150ml milk)", 200, 240, 8.5, 38.0, 5.5, 6.0, "nuts", "high_fiber,vegan"),
        new FoodItem(2, "Paneer Bhurji with 2 Multigrain Roti", "breakfast", true, "1 plate (100g paneer + 2 rotis)", 220, 380, 22.0, 36.0, 16.0, 5.0, "dairy,gluten", "high_protein,vegetarian"),
        new FoodItem(3, "Moong Dal Cheela with Mint Chutney", "breakfast", true, "2 pieces (120g)", 150, 210, 12.0, 28.0, 4.5, 6.5, null, "high_protein,vegan"),
        new FoodItem(4, "Greek Yogurt with Chia & Berries", "breakfast", true, "1 cup (180g)", 180, 165, 15.0, 14.0, 4.0, 3.5, "dairy", "high_protein,calcium"),
        new FoodItem(5, "Sprouted Moong & Chana Chaat", "snack", true, "1 medium bowl (150g)", 150, 185, 11.5, 29.0, 2.0, 7.0, null, "high_fiber,vegan"),
        new FoodItem(6, "Roasted Makhana (Foxnuts)", "snack", true, "1 large bowl (40g)", 40, 145, 4.0, 25.0, 2.5, 3.0, null, "low_calorie,vegan"),
        new FoodItem(7, "Mixed Raw Almonds and Walnuts", "snack", true, "Handful (30g)", 30, 190, 6.0, 5.0, 17.0, 3.0, "nuts", "healthy_fats"),
        new FoodItem(8, "Peanut Butter on Whole Wheat Toast", "snack", true, "2 slices (30g PB)", 110, 290, 12.0, 30.0, 14.0, 4.5, "peanuts,gluten", "energy,protein"),

        // Vegetarian Lunch & Dinner
        new FoodItem(9, "Yellow Dal Tadka with Brown Rice", "lunch", true, "1 bowl dal + 1 cup rice", 300, 340, 14.0, 58.0, 5.0, 7.0, null, "staple,vegan"),
        new FoodItem(10, "Rajma Masala with Steamed Basmati Rice", "lunch", true, "1 bowl rajma + 1 cup rice", 320, 390, 16.5, 66.0, 6.5, 9.0, null, "high_fiber,vegan"),
        new FoodItem(11, "Chole (Chickpea Curry) with 2 Rotis", "lunch", true, "1 bowl chole + 2 rotis", 300, 410, 15.0, 60.0, 10.0, 8.5, "gluten", "filling"),
        new FoodItem(12, "Soya Chunks Stir-fry with Bell Peppers", "lunch", true, "1 bowl (60g soya chunks)", 200, 260, 31.0, 18.0, 3.5, 8.0, "soy", "ultra_high_protein"),
        new FoodItem(13, "Palak Paneer with 2 Whole Wheat Rotis", "dinner", true, "1 bowl palak paneer + 2 rotis", 280, 370, 20.0, 32.0, 18.0, 6.0, "dairy,gluten", "iron,protein"),
        new FoodItem(14, "Tofu Tikka with Grilled Veggies & Quinoa", "dinner", true, "1 plate (150g tofu + 100g quinoa)", 300, 350, 24.0, 35.0, 12.0, 6.5, "soy", "vegan"),
        new FoodItem(15, "Dal Makhani (Light Home Style) with Roti", "dinner", true, "1 bowl + 2 rotis", 270, 360, 13.0, 48.0, 12.0, 7.5, "dairy,gluten", "comfort"),
        new FoodItem(16, "Mixed Vegetable Khichdi with Curd", "dinner", true, "1 deep bowl (250g) + 100g curd", 350, 320, 13.5, 46.0, 7.0, 6.0, "dairy", "light"),

        // Non-Vegetarian Items
        new FoodItem(17, "Boiled Egg Whites (4 Eggs)", "breakfast", false, "4 large egg whites", 132, 68, 14.4, 0.8, 0.4, 0.0, "eggs", "pure_protein"),
        new FoodItem(18, "3-Egg Vegetable Omelet with 1 Toast", "breakfast", false, "1 whole + 2 whites + toast", 220, 290, 21.0, 18.0, 13.0, 2.5, "eggs,gluten", "high_protein"),
        new FoodItem(19, "Egg Bhurji with 2 Multigrain Rotis", "breakfast", false, "2 eggs + veggies + 2 rotis", 240, 380, 20.0, 34.0, 16.0, 4.0, "eggs,gluten", "muscle_building"),
        new FoodItem(20, "Grilled Herb Chicken Breast with Quinoa", "lunch", false, "150g chicken + 100g quinoa", 320, 420, 45.0, 30.0, 9.0, 5.0, null, "lean_protein"),
        new FoodItem(21, "Tandoori Chicken Breast with Salad", "lunch", false, "180g cooked chicken breast", 220, 280, 48.0, 4.0, 7.5, 2.0, "dairy", "high_protein"),
        new FoodItem(22, "Chicken Curry (Homestyle) with 2 Rotis", "lunch", false, "150g chicken + gravy + 2 rotis", 300, 430, 36.0, 36.0, 14.0, 4.5, "gluten", "protein"),
        new FoodItem(23, "Grilled Salmon Fillet with Asparagus", "dinner", false, "160g salmon + asparagus", 240, 380, 38.0, 5.0, 22.0, 3.0, "fish", "omega3"),
        new FoodItem(24, "Steamed Fish Curry with Brown Rice", "dinner", false, "150g fish + 1 cup brown rice", 320, 370, 34.0, 45.0, 6.0, 4.0, "fish", "light"),
        new FoodItem(25, "Chicken Stir-fry with Brown Rice Noodles", "dinner", false, "120g chicken + noodles", 300, 410, 32.0, 52.0, 8.0, 4.0, "soy,gluten", "post_workout")
    );

    public static List<FoodItem> getDefaultFoodLibrary() {
        return DEFAULT_FOOD_LIBRARY;
    }

    /**
     * Synthesizes an individualized 5-meal daily nutrition plan
     */
    public static DietPlan generateDailyDiet(int userId, double targetCalories, double targetProtein, 
                                            double targetCarbs, double targetFat, String dietPreference, 
                                            String allergies, int dayOffset) {
        boolean vegOnly = "vegetarian".equalsIgnoreCase(dietPreference);
        List<String> allergyTokens = new ArrayList<>();
        if (allergies != null && !allergies.trim().isEmpty()) {
            for (String a : allergies.split(",")) {
                if (!a.trim().isEmpty()) {
                    allergyTokens.add(a.trim().toLowerCase());
                }
            }
        }

        // Filter pool by veg/non-veg and allergens
        List<FoodItem> safeFoods = DEFAULT_FOOD_LIBRARY.stream()
            .filter(food -> !vegOnly || food.isVegetarian())
            .filter(food -> {
                String foodAllergens = (food.getAllergens() != null) ? food.getAllergens().toLowerCase() : "";
                String foodName = food.getName().toLowerCase();
                for (String allergen : allergyTokens) {
                    if (foodAllergens.contains(allergen) || foodName.contains(allergen)) {
                        return false;
                    }
                }
                return true;
            })
            .collect(Collectors.toList());

        if (safeFoods.isEmpty()) {
            safeFoods = DEFAULT_FOOD_LIBRARY.stream()
                .filter(food -> !vegOnly || food.isVegetarian())
                .collect(Collectors.toList());
        }

        DietPlan plan = new DietPlan(
            userId,
            (vegOnly ? "Vegetarian" : "Non-Vegetarian") + " High Performance Diet Plan",
            vegOnly ? "vegetarian" : "non_vegetarian",
            targetCalories,
            targetProtein,
            targetCarbs,
            targetFat
        );

        for (int i = 0; i < MEAL_SLOTS.size(); i++) {
            MealSlot slot = MEAL_SLOTS.get(i);
            List<FoodItem> categoryPool = safeFoods.stream()
                .filter(f -> f.getCategory().equalsIgnoreCase(slot.type) || "any".equalsIgnoreCase(f.getCategory()) ||
                             (("lunch".equals(slot.type) || "dinner".equals(slot.type)) && ("lunch".equals(f.getCategory()) || "dinner".equals(f.getCategory()))))
                .collect(Collectors.toList());

            if (categoryPool.isEmpty()) {
                categoryPool = safeFoods;
            }

            int index = (i + dayOffset) % categoryPool.size();
            FoodItem chosen = categoryPool.get(index);

            double targetSlotCalories = targetCalories * slot.caloriePct;
            double portionRatio = Math.max(0.75, Math.min(2.0, targetSlotCalories / chosen.getCalories()));

            double scaledCal = Math.round((chosen.getCalories() * portionRatio) * 10.0) / 10.0;
            double scaledProtein = Math.round((chosen.getProteinG() * portionRatio) * 10.0) / 10.0;
            double scaledCarbs = Math.round((chosen.getCarbsG() * portionRatio) * 10.0) / 10.0;
            double scaledFat = Math.round((chosen.getFatG() * portionRatio) * 10.0) / 10.0;
            int scaledGrams = (int) Math.round(chosen.getPortionGrams() * portionRatio);

            String quantityStr = scaledGrams + "g (" + chosen.getPortionDescription() + ")";

            DietMeal meal = new DietMeal(
                slot.type,
                chosen.getId(),
                chosen.getName(),
                quantityStr,
                scaledCal,
                scaledProtein,
                scaledCarbs,
                scaledFat
            );
            plan.addMeal(meal);
        }

        return plan;
    }
}
