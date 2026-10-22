package com.aigym.model;

/**
 * FitnessGoal Entity Model
 * Corresponds to 'fitness_goals' table in MySQL schema.sql
 */
public class FitnessGoal {
    private int id;
    private int userId;
    private double bmi;
    private String bmiCategory;
    private double bmr;
    private double tdee;
    private double dailyCalorieTarget;
    private double proteinGrams;
    private double carbsGrams;
    private double fatGrams;
    private String targetDate;
    private int estimatedWeeks;

    public FitnessGoal() {}

    public FitnessGoal(int userId, double bmi, String bmiCategory, double bmr, 
                       double tdee, double dailyCalorieTarget, double proteinGrams, 
                       double carbsGrams, double fatGrams, int estimatedWeeks) {
        this.userId = userId;
        this.bmi = bmi;
        this.bmiCategory = bmiCategory;
        this.bmr = bmr;
        this.tdee = tdee;
        this.dailyCalorieTarget = dailyCalorieTarget;
        this.proteinGrams = proteinGrams;
        this.carbsGrams = carbsGrams;
        this.fatGrams = fatGrams;
        this.estimatedWeeks = estimatedWeeks;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public double getBmi() { return bmi; }
    public void setBmi(double bmi) { this.bmi = bmi; }

    public String getBmiCategory() { return bmiCategory; }
    public void setBmiCategory(String bmiCategory) { this.bmiCategory = bmiCategory; }

    public double getBmr() { return bmr; }
    public void setBmr(double bmr) { this.bmr = bmr; }

    public double getTdee() { return tdee; }
    public void setTdee(double tdee) { this.tdee = tdee; }

    public double getDailyCalorieTarget() { return dailyCalorieTarget; }
    public void setDailyCalorieTarget(double dailyCalorieTarget) { this.dailyCalorieTarget = dailyCalorieTarget; }

    public double getProteinGrams() { return proteinGrams; }
    public void setProteinGrams(double proteinGrams) { this.proteinGrams = proteinGrams; }

    public double getCarbsGrams() { return carbsGrams; }
    public void setCarbsGrams(double carbsGrams) { this.carbsGrams = carbsGrams; }

    public double getFatGrams() { return fatGrams; }
    public void setFatGrams(double fatGrams) { this.fatGrams = fatGrams; }

    public String getTargetDate() { return targetDate; }
    public void setTargetDate(String targetDate) { this.targetDate = targetDate; }

    public int getEstimatedWeeks() { return estimatedWeeks; }
    public void setEstimatedWeeks(int estimatedWeeks) { this.estimatedWeeks = estimatedWeeks; }

    @Override
    public String toString() {
        return "FitnessGoal [BMI=" + bmi + " (" + bmiCategory + "), BMR=" + bmr + " kcal, TDEE=" + tdee + 
               " kcal, Calories=" + dailyCalorieTarget + " kcal (P: " + proteinGrams + "g, C: " + 
               carbsGrams + "g, F: " + fatGrams + "g)]";
    }
}
