package com.aigym.ai;

import java.util.ArrayList;
import java.util.List;

/**
 * Biomechanical Form Analyzer & Real-Time Technique Coach
 * Assesses joint alignment against kinesiological benchmarks.
 */
public class FormAnalyzer {

    public static class FormResult {
        public final double score;
        public final String category; // 'Excellent', 'Good', 'Needs Improvement', 'Poor'
        public final String status;   // 'correct', 'warning', 'error'
        public final String feedback;
        public final List<String> cues;

        public FormResult(double score, String category, String status, String feedback, List<String> cues) {
            this.score = score;
            this.category = category;
            this.status = status;
            this.feedback = feedback;
            this.cues = cues;
        }

        @Override
        public String toString() {
            return String.format("Score: %.1f [%s - %s] -> \"%s\"", score, category, status.toUpperCase(), feedback);
        }
    }

    public static FormResult analyzeForm(String exerciseName, double primaryAngle, double secondaryAngle, String state) {
        String ex = (exerciseName != null) ? exerciseName.toLowerCase().replace("_", "-") : "squats";
        double score = 95.0;
        String feedback = "Good form";
        String status = "correct";
        List<String> cues = new ArrayList<>();

        if (ex.contains("squat")) {
            double kneeAngle = primaryAngle;
            double hipAngle = (secondaryAngle > 0) ? secondaryAngle : 170.0;

            if ("BOTTOM".equals(state)) {
                if (kneeAngle > 105.0) {
                    score -= 25.0;
                    feedback = "Go lower (thighs parallel to floor)";
                    status = "warning";
                    cues.add("Reach at least 90 degree knee flexion");
                } else if (kneeAngle < 60.0) {
                    score -= 10.0;
                    feedback = "Don't bottom out too deep; preserve knee stability";
                    status = "warning";
                } else {
                    score = 98.0;
                    feedback = "Perfect depth! Drive up through heels";
                    status = "correct";
                }
            }

            if (hipAngle < 70.0 && ("DESCENDING".equals(state) || "BOTTOM".equals(state))) {
                score -= 20.0;
                feedback = "Keep chest lifted, don't bend forward excessively";
                status = "error";
                cues.add("Engage core, lift chest and gaze forward");
            }

            if ("ASCENDING".equals(state) && score >= 80.0) {
                feedback = "Control ascent evenly";
            }

        } else if (ex.contains("push")) {
            double elbowAngle = primaryAngle;
            double bodyLineAngle = (secondaryAngle > 0) ? secondaryAngle : 180.0;

            if (Math.abs(bodyLineAngle - 180.0) > 25.0) {
                score -= 25.0;
                feedback = "Keep body in a rigid straight line";
                status = "error";
                cues.add("Do not let hips sag or pike upward");
            }

            if ("BOTTOM".equals(state)) {
                if (elbowAngle > 100.0) {
                    score -= 20.0;
                    feedback = "Lower chest further to ground";
                    status = "warning";
                    cues.add("Lower until elbows reach ~90 degrees");
                } else {
                    feedback = "Great depth! Push through palms";
                    status = "correct";
                }
            }

            if ("ASCENDING".equals(state) && !"error".equals(status)) {
                feedback = "Keep elbows tucked at 45 degrees";
            }

        } else if (ex.contains("lunge")) {
            double kneeAngle = primaryAngle;
            if ("BOTTOM".equals(state)) {
                if (kneeAngle > 105.0) {
                    score -= 20.0;
                    feedback = "Lower hips further toward floor";
                    status = "warning";
                } else if (kneeAngle < 75.0) {
                    score -= 15.0;
                    feedback = "Keep front knee behind toes";
                    status = "warning";
                    cues.add("Front knee directly over ankle");
                } else {
                    feedback = "Excellent stance and depth";
                    status = "correct";
                }
            } else if ("DESCENDING".equals(state)) {
                feedback = "Maintain balance and upright torso";
            }

        } else if (ex.contains("curl")) {
            double elbowAngle = primaryAngle;
            double shoulderStability = (secondaryAngle > 0) ? secondaryAngle : 15.0;

            if (shoulderStability > 35.0) {
                score -= 25.0;
                feedback = "Keep elbows pinned to your sides";
                status = "error";
                cues.add("Do not swing shoulders or use body momentum");
            }

            if ("PEAK".equals(state)) {
                if (elbowAngle <= 45.0) {
                    feedback = "Peak contraction achieved!";
                    status = "correct";
                } else {
                    feedback = "Squeeze biceps at the top";
                    status = "warning";
                }
            }

        } else if (ex.contains("shoulder") || ex.contains("press")) {
            double armElevation = (secondaryAngle > 0) ? secondaryAngle : 90.0;
            if ("OVERHEAD".equals(state)) {
                if (primaryAngle >= 155.0) {
                    feedback = "Full overhead extension achieved!";
                    status = "correct";
                } else {
                    score -= 15.0;
                    feedback = "Extend arms fully overhead";
                    status = "warning";
                }
            } else if ("RACK".equals(state)) {
                feedback = "Smooth control on descent";
            }
        }

        // Clamp score between 0 and 100
        score = Math.max(0.0, Math.min(100.0, score));

        String category;
        if (score >= 90.0) category = "Excellent";
        else if (score >= 75.0) category = "Good";
        else if (score >= 60.0) category = "Needs Improvement";
        else category = "Poor";

        return new FormResult(score, category, status, feedback, cues);
    }
}
