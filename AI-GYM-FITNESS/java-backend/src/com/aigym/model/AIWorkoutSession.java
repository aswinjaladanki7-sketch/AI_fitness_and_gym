package com.aigym.model;

import java.sql.Timestamp;

/**
 * AIWorkoutSession Model (represents camera CV workout session log)
 * Corresponds to 'ai_workout_sessions' table in schema.sql
 */
public class AIWorkoutSession {
    private int id;
    private int userId;
    private Integer workoutSessionId;
    private String exerciseName;
    private int repetitionsCompleted;
    private int validReps;
    private int invalidReps;
    private int durationSeconds;
    private double averageFormScore;
    private String formCategory; // 'excellent', 'good', 'needs_improvement', 'poor'
    private double caloriesBurned;
    private String feedbackSummary;
    private Timestamp createdAt;

    public AIWorkoutSession() {}

    public AIWorkoutSession(int userId, String exerciseName, int repetitionsCompleted, 
                            int validReps, int invalidReps, int durationSeconds, 
                            double averageFormScore, String formCategory, 
                            double caloriesBurned, String feedbackSummary) {
        this.userId = userId;
        this.exerciseName = exerciseName;
        this.repetitionsCompleted = repetitionsCompleted;
        this.validReps = validReps;
        this.invalidReps = invalidReps;
        this.durationSeconds = durationSeconds;
        this.averageFormScore = averageFormScore;
        this.formCategory = formCategory;
        this.caloriesBurned = caloriesBurned;
        this.feedbackSummary = feedbackSummary;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public Integer getWorkoutSessionId() { return workoutSessionId; }
    public void setWorkoutSessionId(Integer workoutSessionId) { this.workoutSessionId = workoutSessionId; }

    public String getExerciseName() { return exerciseName; }
    public void setExerciseName(String exerciseName) { this.exerciseName = exerciseName; }

    public int getRepetitionsCompleted() { return repetitionsCompleted; }
    public void setRepetitionsCompleted(int repetitionsCompleted) { this.repetitionsCompleted = repetitionsCompleted; }

    public int getValidReps() { return validReps; }
    public void setValidReps(int validReps) { this.validReps = validReps; }

    public int getInvalidReps() { return invalidReps; }
    public void setInvalidReps(int invalidReps) { this.invalidReps = invalidReps; }

    public int getDurationSeconds() { return durationSeconds; }
    public void setDurationSeconds(int durationSeconds) { this.durationSeconds = durationSeconds; }

    public double getAverageFormScore() { return averageFormScore; }
    public void setAverageFormScore(double averageFormScore) { this.averageFormScore = averageFormScore; }

    public String getFormCategory() { return formCategory; }
    public void setFormCategory(String formCategory) { this.formCategory = formCategory; }

    public double getCaloriesBurned() { return caloriesBurned; }
    public void setCaloriesBurned(double caloriesBurned) { this.caloriesBurned = caloriesBurned; }

    public String getFeedbackSummary() { return feedbackSummary; }
    public void setFeedbackSummary(String feedbackSummary) { this.feedbackSummary = feedbackSummary; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return String.format("%s: %d Reps (%d valid, %d invalid) in %ds | Score: %.1f%% (%s) | Cal: %.1f kcal",
                exerciseName, repetitionsCompleted, validReps, invalidReps, durationSeconds, 
                averageFormScore, formCategory, caloriesBurned);
    }
}
