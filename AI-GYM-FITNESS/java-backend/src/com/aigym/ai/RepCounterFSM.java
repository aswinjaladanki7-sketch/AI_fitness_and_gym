package com.aigym.ai;

/**
 * Finite State Machine (FSM) Automaton for Repetition Counting
 * Implements strict eccentric and concentric phase transitions to eliminate
 * false triggers and ensure full range of motion.
 */
public class RepCounterFSM {

    public static class RepUpdateResult {
        public final boolean repCompleted;
        public final int repCount;
        public final int validReps;
        public final int invalidReps;
        public final String state;
        public final double formScore;

        public RepUpdateResult(boolean repCompleted, int repCount, int validReps, 
                                int invalidReps, String state, double formScore) {
            this.repCompleted = repCompleted;
            this.repCount = repCount;
            this.validReps = validReps;
            this.invalidReps = invalidReps;
            this.state = state;
            this.formScore = formScore;
        }

        @Override
        public String toString() {
            return String.format("State: %-10s | Reps: %d (Valid: %d, Invalid: %d) | Score: %.1f%s",
                    state, repCount, validReps, invalidReps, formScore, repCompleted ? " [★ COMPLETED!]" : "");
        }
    }

    private final String exerciseName;
    private String state;
    private int repCount;
    private int validReps;
    private int invalidReps;
    private double lastRepFormScore;

    public RepCounterFSM(String exerciseName) {
        this.exerciseName = (exerciseName != null) ? exerciseName.toLowerCase().replace("_", "-") : "squats";
        reset();
    }

    public void reset() {
        this.state = "INITIAL";
        this.repCount = 0;
        this.validReps = 0;
        this.invalidReps = 0;
        this.lastRepFormScore = 100.0;
    }

    public RepUpdateResult update(double primaryAngle, boolean formValid, double repScore) {
        boolean repCompleted = false;
        String ex = this.exerciseName;

        if (ex.contains("squat") || ex.contains("lunge")) {
            // SQUAT / LUNGE: Standing (>=155) -> Descending (<140) -> Bottom (<95) -> Ascending (>115) -> Standing (>=155)
            if (primaryAngle >= 155.0) {
                if ("ASCENDING".equals(this.state) || "BOTTOM".equals(this.state)) {
                    this.repCount++;
                    if (formValid && repScore >= 65.0) {
                        this.validReps++;
                    } else {
                        this.invalidReps++;
                    }
                    this.lastRepFormScore = repScore;
                    repCompleted = true;
                }
                this.state = "STANDING";
            } else if (primaryAngle < 95.0 && ("STANDING".equals(this.state) || "DESCENDING".equals(this.state))) {
                this.state = "BOTTOM";
            } else if (primaryAngle < 140.0 && "STANDING".equals(this.state)) {
                this.state = "DESCENDING";
            } else if (primaryAngle > 115.0 && "BOTTOM".equals(this.state)) {
                this.state = "ASCENDING";
            }

        } else if (ex.contains("push")) {
            // PUSH-UP: Up (>=150) -> Descending (<135) -> Bottom (<=90) -> Ascending (>110) -> Up (>=150)
            if (primaryAngle >= 150.0) {
                if ("ASCENDING".equals(this.state) || "BOTTOM".equals(this.state)) {
                    this.repCount++;
                    if (formValid && repScore >= 65.0) {
                        this.validReps++;
                    } else {
                        this.invalidReps++;
                    }
                    this.lastRepFormScore = repScore;
                    repCompleted = true;
                }
                this.state = "UP";
            } else if (primaryAngle <= 90.0 && ("UP".equals(this.state) || "DESCENDING".equals(this.state))) {
                this.state = "BOTTOM";
            } else if (primaryAngle < 135.0 && "UP".equals(this.state)) {
                this.state = "DESCENDING";
            } else if (primaryAngle > 110.0 && "BOTTOM".equals(this.state)) {
                this.state = "ASCENDING";
            }

        } else if (ex.contains("curl")) {
            // BICEP CURL: Extended (>=145) -> Curling (<120) -> Peak (<=48) -> Lowering (>80) -> Extended (>=145)
            if (primaryAngle >= 145.0) {
                if ("LOWERING".equals(this.state) || "PEAK".equals(this.state)) {
                    this.repCount++;
                    if (formValid && repScore >= 65.0) {
                        this.validReps++;
                    } else {
                        this.invalidReps++;
                    }
                    this.lastRepFormScore = repScore;
                    repCompleted = true;
                }
                this.state = "EXTENDED";
            } else if (primaryAngle <= 48.0 && ("EXTENDED".equals(this.state) || "CURLING".equals(this.state))) {
                this.state = "PEAK";
            } else if (primaryAngle < 120.0 && "EXTENDED".equals(this.state)) {
                this.state = "CURLING";
            } else if (primaryAngle > 80.0 && "PEAK".equals(this.state)) {
                this.state = "LOWERING";
            }

        } else if (ex.contains("shoulder") || ex.contains("press")) {
            // SHOULDER PRESS: Rack (<=90) -> Pressing (>105) -> Overhead (>=155) -> Lowering (<140) -> Rack (<=90)
            if (primaryAngle >= 155.0) {
                this.state = "OVERHEAD";
            } else if (primaryAngle <= 90.0) {
                if ("LOWERING".equals(this.state) || "OVERHEAD".equals(this.state)) {
                    this.repCount++;
                    if (formValid && repScore >= 65.0) {
                        this.validReps++;
                    } else {
                        this.invalidReps++;
                    }
                    this.lastRepFormScore = repScore;
                    repCompleted = true;
                }
                this.state = "RACK";
            } else if (primaryAngle > 105.0 && "RACK".equals(this.state)) {
                this.state = "PRESSING";
            } else if (primaryAngle < 140.0 && "OVERHEAD".equals(this.state)) {
                this.state = "LOWERING";
            }
        }

        return new RepUpdateResult(repCompleted, repCount, validReps, invalidReps, state, lastRepFormScore);
    }

    public String getExerciseName() { return exerciseName; }
    public String getState() { return state; }
    public int getRepCount() { return repCount; }
    public int getValidReps() { return validReps; }
    public int getInvalidReps() { return invalidReps; }
}
