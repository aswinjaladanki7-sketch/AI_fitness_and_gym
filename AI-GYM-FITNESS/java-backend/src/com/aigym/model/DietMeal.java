package com.aigym.model;

/**
 * DietMeal Model (represents a specific item in a diet plan)
 * Corresponds to 'diet_meals' table in schema.sql
 */
public class DietMeal {
    private int id;
    private int dietPlanId;
    private String mealType; // 'breakfast', 'mid_morning', 'lunch', 'evening_snack', 'dinner'
    private Integer foodItemId;
    private String foodName;
    private String quantity;
    private double calories;
    private double proteinG;
    private double carbsG;
    private double fatG;

    public DietMeal() {}

    public DietMeal(String mealType, Integer foodItemId, String foodName, String quantity, 
                    double calories, double proteinG, double carbsG, double fatG) {
        this.mealType = mealType;
        this.foodItemId = foodItemId;
        this.foodName = foodName;
        this.quantity = quantity;
        this.calories = calories;
        this.proteinG = proteinG;
        this.carbsG = carbsG;
        this.fatG = fatG;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getDietPlanId() { return dietPlanId; }
    public void setDietPlanId(int dietPlanId) { this.dietPlanId = dietPlanId; }

    public String getMealType() { return mealType; }
    public void setMealType(String mealType) { this.mealType = mealType; }

    public Integer getFoodItemId() { return foodItemId; }
    public void setFoodItemId(Integer foodItemId) { this.foodItemId = foodItemId; }

    public String getFoodName() { return foodName; }
    public void setFoodName(String foodName) { this.foodName = foodName; }

    public String getQuantity() { return quantity; }
    public void setQuantity(String quantity) { this.quantity = quantity; }

    public double getCalories() { return calories; }
    public void setCalories(double calories) { this.calories = calories; }

    public double getProteinG() { return proteinG; }
    public void setProteinG(double proteinG) { this.proteinG = proteinG; }

    public double getCarbsG() { return carbsG; }
    public void setCarbsG(double carbsG) { this.carbsG = carbsG; }

    public double getFatG() { return fatG; }
    public void setFatG(double fatG) { this.fatG = fatG; }

    @Override
    public String toString() {
        return String.format("[%s] %s (%s) - %.1f kcal | P: %.1fg, C: %.1fg, F: %.1fg",
                mealType.toUpperCase(), foodName, quantity, calories, proteinG, carbsG, fatG);
    }
}
