package com.aigym.ai;

/**
 * Biomechanical Spatial Landmark Coordinate Point
 * Corresponds to MediaPipe 33-point kinematic pose output
 */
public class Point {
    private double x;
    private double y;
    private double z;
    private double visibility;

    public Point() {}

    public Point(double x, double y) {
        this(x, y, 0.0, 1.0);
    }

    public Point(double x, double y, double z, double visibility) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.visibility = visibility;
    }

    public double getX() { return x; }
    public void setX(double x) { this.x = x; }

    public double getY() { return y; }
    public void setY(double y) { this.y = y; }

    public double getZ() { return z; }
    public void setZ(double z) { this.z = z; }

    public double getVisibility() { return visibility; }
    public void setVisibility(double visibility) { this.visibility = visibility; }

    @Override
    public String toString() {
        return String.format("Point(x=%.3f, y=%.3f, vis=%.2f)", x, y, visibility);
    }
}
