package com.aigym.model;

/**
 * FoodItem Entity Model
 * Corresponds to 'food_items' table in MySQL schema.sql
 */
public class FoodItem {
    private int id;
    private String name;
    private String category; // 'breakfast', 'lunch', 'dinner', 'snack', 'any'
    private boolean isVegetarian;
    private String portionDescription;
    private double portionGrams;
    private double calories;
    private double proteinG;
    private double carbsG;
    private double fatG;
    private double fiberG;
    private String allergens;
    private String tags;

    public FoodItem() {}

    public FoodItem(int id, String name, String category, boolean isVegetarian, 
                    String portionDescription, double portionGrams, double calories, 
                    double proteinG, double carbsG, double fatG, double fiberG, 
                    String allergens, String tags) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.isVegetarian = isVegetarian;
        this.portionDescription = portionDescription;
        this.portionGrams = portionGrams;
        this.calories = calories;
        this.proteinG = proteinG;
        this.carbsG = carbsG;
        this.fatG = fatG;
        this.fiberG = fiberG;
        this.allergens = allergens;
        this.tags = tags;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public boolean isVegetarian() { return isVegetarian; }
    public void setVegetarian(boolean vegetarian) { isVegetarian = vegetarian; }

    public String getPortionDescription() { return portionDescription; }
    public void setPortionDescription(String portionDescription) { this.portionDescription = portionDescription; }

    public double getPortionGrams() { return portionGrams; }
    public void setPortionGrams(double portionGrams) { this.portionGrams = portionGrams; }

    public double getCalories() { return calories; }
    public void setCalories(double calories) { this.calories = calories; }

    public double getProteinG() { return proteinG; }
    public void setProteinG(double proteinG) { this.proteinG = proteinG; }

    public double getCarbsG() { return carbsG; }
    public void setCarbsG(double carbsG) { this.carbsG = carbsG; }

    public double getFatG() { return fatG; }
    public void setFatG(double fatG) { this.fatG = fatG; }

    public double getFiberG() { return fiberG; }
    public void setFiberG(double fiberG) { this.fiberG = fiberG; }

    public String getAllergens() { return allergens; }
    public void setAllergens(String allergens) { this.allergens = allergens; }

    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }

    @Override
    public String toString() {
        return name + " [" + (isVegetarian ? "VEG" : "NON-VEG") + "] - " + 
               calories + " kcal, P: " + proteinG + "g, C: " + carbsG + "g, F: " + fatG + "g";
    }
}
