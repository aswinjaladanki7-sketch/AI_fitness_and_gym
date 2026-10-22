package com.aigym.model;

import java.util.ArrayList;
import java.util.List;

/**
 * WorkoutPlan Model
 * Corresponds to 'workout_plans' table in schema.sql
 */
public class WorkoutPlan {
    private int id;
    private int userId;
    private String title;
    private String goal;
    private String focus;
    private String difficulty;
    private String location;
    private int daysPerWeek;
    private int estimatedDurationMinutes;
    private List<Exercise> exercises = new ArrayList<>();

    public WorkoutPlan() {}

    public WorkoutPlan(String title, String goal, String focus, String difficulty, 
                       String location, int daysPerWeek, int estimatedDurationMinutes) {
        this.title = title;
        this.goal = goal;
        this.focus = focus;
        this.difficulty = difficulty;
        this.location = location;
        this.daysPerWeek = daysPerWeek;
        this.estimatedDurationMinutes = estimatedDurationMinutes;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getGoal() { return goal; }
    public void setGoal(String goal) { this.goal = goal; }

    public String getFocus() { return focus; }
    public void setFocus(String focus) { this.focus = focus; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public int getDaysPerWeek() { return daysPerWeek; }
    public void setDaysPerWeek(int daysPerWeek) { this.daysPerWeek = daysPerWeek; }

    public int getEstimatedDurationMinutes() { return estimatedDurationMinutes; }
    public void setEstimatedDurationMinutes(int estimatedDurationMinutes) { this.estimatedDurationMinutes = estimatedDurationMinutes; }

    public List<Exercise> getExercises() { return exercises; }
    public void setExercises(List<Exercise> exercises) { this.exercises = exercises; }

    public void addExercise(Exercise exercise) {
        this.exercises.add(exercise);
    }
}
