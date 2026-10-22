package com.aigym.model;

import java.util.ArrayList;
import java.util.List;

/**
 * DietPlan Model
 * Corresponds to 'diet_plans' table in schema.sql
 */
public class DietPlan {
    private int id;
    private int userId;
    private String planName;
    private String dietType; // 'vegetarian', 'non_vegetarian'
    private double targetCalories;
    private double targetProtein;
    private double targetCarbs;
    private double targetFat;
    private String planDay; // 'Daily', 'Monday', etc.
    private boolean isActive;
    private List<DietMeal> meals = new ArrayList<>();

    public DietPlan() {}

    public DietPlan(int userId, String planName, String dietType, double targetCalories, 
                    double targetProtein, double targetCarbs, double targetFat) {
        this.userId = userId;
        this.planName = planName;
        this.dietType = dietType;
        this.targetCalories = targetCalories;
        this.targetProtein = targetProtein;
        this.targetCarbs = targetCarbs;
        this.targetFat = targetFat;
        this.isActive = true;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getPlanName() { return planName; }
    public void setPlanName(String planName) { this.planName = planName; }

    public String getDietType() { return dietType; }
    public void setDietType(String dietType) { this.dietType = dietType; }

    public double getTargetCalories() { return targetCalories; }
    public void setTargetCalories(double targetCalories) { this.targetCalories = targetCalories; }

    public double getTargetProtein() { return targetProtein; }
    public void setTargetProtein(double targetProtein) { this.targetProtein = targetProtein; }

    public double getTargetCarbs() { return targetCarbs; }
    public void setTargetCarbs(double targetCarbs) { this.targetCarbs = targetCarbs; }

    public double getTargetFat() { return targetFat; }
    public void setTargetFat(double targetFat) { this.targetFat = targetFat; }

    public String getPlanDay() { return planDay; }
    public void setPlanDay(String planDay) { this.planDay = planDay; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public List<DietMeal> getMeals() { return meals; }
    public void setMeals(List<DietMeal> meals) { this.meals = meals; }

    public void addMeal(DietMeal meal) {
        this.meals.add(meal);
    }

    public double getTotalActualCalories() {
        return meals.stream().mapToDouble(DietMeal::getCalories).sum();
    }

    public double getTotalActualProtein() {
        return meals.stream().mapToDouble(DietMeal::getProteinG).sum();
    }

    public double getTotalActualCarbs() {
        return meals.stream().mapToDouble(DietMeal::getCarbsG).sum();
    }

    public double getTotalActualFat() {
        return meals.stream().mapToDouble(DietMeal::getFatG).sum();
    }
}
