package com.aigym.ai;

/**
 * Biomechanical Joint Angle Calculator
 * Uses vector dot product trigonometry to calculate interior joint angle at vertex B.
 */
public class JointAngleCalculator {

    /**
     * Calculates the angle in degrees (0.0 to 180.0) at vertex point B
     * formed by line segment BA and line segment BC.
     * 
     * Formula:
     *   ba = (ax - bx, ay - by)
     *   bc = (cx - bx, cy - by)
     *   dot = (ba_x * bc_x) + (ba_y * bc_y)
     *   cos(theta) = dot / (|ba| * |bc|)
     *   theta = acos(cos(theta)) in degrees
     */
    public static double calculateAngle(Point a, Point b, Point c) {
        if (a == null || b == null || c == null) {
            return 0.0;
        }

        double baX = a.getX() - b.getX();
        double baY = a.getY() - b.getY();
        double bcX = c.getX() - b.getX();
        double bcY = c.getY() - b.getY();

        double dotProduct = (baX * bcX) + (baY * bcY);
        double magBA = Math.hypot(baX, baY);
        double magBC = Math.hypot(bcX, bcY);

        if (magBA == 0 || magBC == 0) {
            return 0.0;
        }

        double cosine = dotProduct / (magBA * magBC);
        // Clamp to prevent floating-point inaccuracies triggering NaN from acos
        cosine = Math.max(-1.0, Math.min(1.0, cosine));

        double angleRad = Math.acos(cosine);
        double angleDeg = Math.toDegrees(angleRad);

        return Math.round(angleDeg * 10.0) / 10.0;
    }
}
