package com.aigym.model;

/**
 * UserProfile Entity Model
 * Corresponds to 'user_profiles' table in MySQL schema.sql
 */
public class UserProfile {
    private int id;
    private int userId;
    private int age;
    private String gender; // 'male', 'female', 'other'
    private double heightCm;
    private double weightKg;
    private double targetWeightKg;
    private String fitnessGoal; // 'weight_loss', 'muscle_building', 'strength', 'general_fitness', 'beginner'
    private String workoutType; // 'gym', 'home', 'hybrid'
    private int workoutFrequency; // days per week
    private String activityLevel; // 'sedentary', 'lightly_active', 'moderately_active', 'very_active', 'extra_active'
    private String fitnessLevel; // 'beginner', 'intermediate', 'advanced'
    private String dietPreference; // 'vegetarian', 'non_vegetarian'
    private String foodPreferences;
    private String foodAllergies;

    public UserProfile() {}

    public UserProfile(int userId, int age, String gender, double heightCm, double weightKg, 
                       double targetWeightKg, String fitnessGoal, String workoutType, 
                       int workoutFrequency, String activityLevel, String fitnessLevel, 
                       String dietPreference, String foodPreferences, String foodAllergies) {
        this.userId = userId;
        this.age = age;
        this.gender = gender;
        this.heightCm = heightCm;
        this.weightKg = weightKg;
        this.targetWeightKg = targetWeightKg;
        this.fitnessGoal = fitnessGoal;
        this.workoutType = workoutType;
        this.workoutFrequency = workoutFrequency;
        this.activityLevel = activityLevel;
        this.fitnessLevel = fitnessLevel;
        this.dietPreference = dietPreference;
        this.foodPreferences = foodPreferences;
        this.foodAllergies = foodAllergies;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public double getHeightCm() { return heightCm; }
    public void setHeightCm(double heightCm) { this.heightCm = heightCm; }

    public double getWeightKg() { return weightKg; }
    public void setWeightKg(double weightKg) { this.weightKg = weightKg; }

    public double getTargetWeightKg() { return targetWeightKg; }
    public void setTargetWeightKg(double targetWeightKg) { this.targetWeightKg = targetWeightKg; }

    public String getFitnessGoal() { return fitnessGoal; }
    public void setFitnessGoal(String fitnessGoal) { this.fitnessGoal = fitnessGoal; }

    public String getWorkoutType() { return workoutType; }
    public void setWorkoutType(String workoutType) { this.workoutType = workoutType; }

    public int getWorkoutFrequency() { return workoutFrequency; }
    public void setWorkoutFrequency(int workoutFrequency) { this.workoutFrequency = workoutFrequency; }

    public String getActivityLevel() { return activityLevel; }
    public void setActivityLevel(String activityLevel) { this.activityLevel = activityLevel; }

    public String getFitnessLevel() { return fitnessLevel; }
    public void setFitnessLevel(String fitnessLevel) { this.fitnessLevel = fitnessLevel; }

    public String getDietPreference() { return dietPreference; }
    public void setDietPreference(String dietPreference) { this.dietPreference = dietPreference; }

    public String getFoodPreferences() { return foodPreferences; }
    public void setFoodPreferences(String foodPreferences) { this.foodPreferences = foodPreferences; }

    public String getFoodAllergies() { return foodAllergies; }
    public void setFoodAllergies(String foodAllergies) { this.foodAllergies = foodAllergies; }

    @Override
    public String toString() {
        return "UserProfile [userId=" + userId + ", age=" + age + ", gender=" + gender + 
               ", height=" + heightCm + "cm, weight=" + weightKg + "kg, goal=" + fitnessGoal + 
               ", diet=" + dietPreference + "]";
    }
}
