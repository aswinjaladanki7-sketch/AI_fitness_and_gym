package com.aigym.model;

/**
 * Exercise Entity Model
 * Corresponds to 'exercises' table in schema.sql
 */
public class Exercise {
    private int id;
    private String name;
    private String slug;
    private String targetMuscle;
    private String secondaryMuscles;
    private String difficulty; // 'beginner', 'intermediate', 'advanced'
    private String location; // 'gym', 'home', 'both'
    private String equipment;
    private int defaultSets;
    private int defaultReps;
    private int defaultRestSeconds;
    private boolean hasAiCamera;
    private String instructions;
    private String formCues;

    public Exercise() {}

    public Exercise(int id, String name, String slug, String targetMuscle, 
                    String secondaryMuscles, String difficulty, String location, 
                    String equipment, int defaultSets, int defaultReps, 
                    int defaultRestSeconds, boolean hasAiCamera, 
                    String instructions, String formCues) {
        this.id = id;
        this.name = name;
        this.slug = slug;
        this.targetMuscle = targetMuscle;
        this.secondaryMuscles = secondaryMuscles;
        this.difficulty = difficulty;
        this.location = location;
        this.equipment = equipment;
        this.defaultSets = defaultSets;
        this.defaultReps = defaultReps;
        this.defaultRestSeconds = defaultRestSeconds;
        this.hasAiCamera = hasAiCamera;
        this.instructions = instructions;
        this.formCues = formCues;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public String getTargetMuscle() { return targetMuscle; }
    public void setTargetMuscle(String targetMuscle) { this.targetMuscle = targetMuscle; }

    public String getSecondaryMuscles() { return secondaryMuscles; }
    public void setSecondaryMuscles(String secondaryMuscles) { this.secondaryMuscles = secondaryMuscles; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getEquipment() { return equipment; }
    public void setEquipment(String equipment) { this.equipment = equipment; }

    public int getDefaultSets() { return defaultSets; }
    public void setDefaultSets(int defaultSets) { this.defaultSets = defaultSets; }

    public int getDefaultReps() { return defaultReps; }
    public void setDefaultReps(int defaultReps) { this.defaultReps = defaultReps; }

    public int getDefaultRestSeconds() { return defaultRestSeconds; }
    public void setDefaultRestSeconds(int defaultRestSeconds) { this.defaultRestSeconds = defaultRestSeconds; }

    public boolean isHasAiCamera() { return hasAiCamera; }
    public void setHasAiCamera(boolean hasAiCamera) { this.hasAiCamera = hasAiCamera; }

    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }

    public String getFormCues() { return formCues; }
    public void setFormCues(String formCues) { this.formCues = formCues; }

    @Override
    public String toString() {
        return name + " (" + targetMuscle + ") [" + equipment + "] " + 
               defaultSets + " sets x " + defaultReps + " reps | Rest: " + defaultRestSeconds + "s" +
               (hasAiCamera ? " [AI Vision Supported]" : "");
    }
}
