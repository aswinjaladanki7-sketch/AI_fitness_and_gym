package com.aigym.db;

import com.aigym.model.*;

import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Data Access Object (DAO) Pattern Implementation
 * SRM IST 21CSC203P - PROJECT
 * 
 * Implements persistent MySQL database queries alongside an in-memory cache fallback.
 */
public class FitnessDAO {

    private final boolean useJdbc;

    // In-memory fallback stores
    private final Map<Integer, User> userStore = new ConcurrentHashMap<>();
    private final Map<String, User> userEmailIndex = new ConcurrentHashMap<>();
    private final Map<Integer, UserProfile> profileStore = new ConcurrentHashMap<>();
    private final Map<Integer, FitnessGoal> goalsStore = new ConcurrentHashMap<>();
    private final List<AIWorkoutSession> aiSessionStore = Collections.synchronizedList(new ArrayList<>());
    private final AtomicInteger userSeq = new AtomicInteger(1);
    private final AtomicInteger profileSeq = new AtomicInteger(1);
    private final AtomicInteger goalsSeq = new AtomicInteger(1);
    private final AtomicInteger sessionSeq = new AtomicInteger(1);

    public FitnessDAO() {
        boolean connected = false;
        if (DBConnection.isDriverLoaded()) {
            try (Connection conn = DBConnection.getConnection()) {
                connected = (conn != null && !conn.isClosed());
            } catch (Exception e) {
                connected = false;
            }
        }
        this.useJdbc = connected;
    }

    public boolean isUsingJdbc() {
        return useJdbc;
    }

    // -------------------------------------------------------------
    // USER OPERATIONS
    // -------------------------------------------------------------
    public User registerUser(String name, String email, String passwordHash) {
        if (useJdbc) {
            String sql = "INSERT INTO users (name, email, password_hash) VALUES (?, ?, ?)";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, name);
                ps.setString(2, email);
                ps.setString(3, passwordHash);
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        int id = rs.getInt(1);
                        return new User(id, name, email, passwordHash);
                    }
                }
            } catch (SQLException e) {
                System.err.println("[DAO] JDBC registerUser error: " + e.getMessage());
            }
        }

        // In-memory fallback
        int id = userSeq.getAndIncrement();
        User u = new User(id, name, email, passwordHash);
        userStore.put(id, u);
        userEmailIndex.put(email.toLowerCase(), u);
        return u;
    }

    public User getUserByEmail(String email) {
        if (email == null) return null;
        if (useJdbc) {
            String sql = "SELECT id, name, email, password_hash FROM users WHERE email = ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, email);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new User(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("password_hash")
                        );
                    }
                }
            } catch (SQLException e) {
                System.err.println("[DAO] JDBC getUserByEmail error: " + e.getMessage());
            }
        }

        return userEmailIndex.get(email.toLowerCase());
    }

    // -------------------------------------------------------------
    // PROFILE OPERATIONS
    // -------------------------------------------------------------
    public void saveProfile(UserProfile p) {
        if (useJdbc) {
            String sql = "INSERT INTO user_profiles (user_id, age, gender, height_cm, weight_kg, target_weight_kg, " +
                         "fitness_goal, workout_type, workout_frequency, activity_level, fitness_level, " +
                         "diet_preference, food_preferences, food_allergies) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                         "ON DUPLICATE KEY UPDATE age=VALUES(age), weight_kg=VALUES(weight_kg), target_weight_kg=VALUES(target_weight_kg), " +
                         "fitness_goal=VALUES(fitness_goal), workout_type=VALUES(workout_type), workout_frequency=VALUES(workout_frequency), " +
                         "activity_level=VALUES(activity_level), diet_preference=VALUES(diet_preference), food_allergies=VALUES(food_allergies)";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, p.getUserId());
                ps.setInt(2, p.getAge());
                ps.setString(3, p.getGender());
                ps.setDouble(4, p.getHeightCm());
                ps.setDouble(5, p.getWeightKg());
                ps.setDouble(6, p.getTargetWeightKg());
                ps.setString(7, p.getFitnessGoal());
                ps.setString(8, p.getWorkoutType());
                ps.setInt(9, p.getWorkoutFrequency());
                ps.setString(10, p.getActivityLevel());
                ps.setString(11, p.getFitnessLevel());
                ps.setString(12, p.getDietPreference());
                ps.setString(13, p.getFoodPreferences());
                ps.setString(14, p.getFoodAllergies());
                ps.executeUpdate();
                return;
            } catch (SQLException e) {
                System.err.println("[DAO] JDBC saveProfile error: " + e.getMessage());
            }
        }

        if (p.getId() == 0) {
            p.setId(profileSeq.getAndIncrement());
        }
        profileStore.put(p.getUserId(), p);
    }

    public UserProfile getProfile(int userId) {
        if (useJdbc) {
            String sql = "SELECT * FROM user_profiles WHERE user_id = ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        UserProfile p = new UserProfile(
                            rs.getInt("user_id"),
                            rs.getInt("age"),
                            rs.getString("gender"),
                            rs.getDouble("height_cm"),
                            rs.getDouble("weight_kg"),
                            rs.getDouble("target_weight_kg"),
                            rs.getString("fitness_goal"),
                            rs.getString("workout_type"),
                            rs.getInt("workout_frequency"),
                            rs.getString("activity_level"),
                            rs.getString("fitness_level"),
                            rs.getString("diet_preference"),
                            rs.getString("food_preferences"),
                            rs.getString("food_allergies")
                        );
                        p.setId(rs.getInt("id"));
                        return p;
                    }
                }
            } catch (SQLException e) {
                System.err.println("[DAO] JDBC getProfile error: " + e.getMessage());
            }
        }

        return profileStore.get(userId);
    }

    // -------------------------------------------------------------
    // FITNESS GOAL OPERATIONS
    // -------------------------------------------------------------
    public void saveFitnessGoal(FitnessGoal g) {
        if (useJdbc) {
            String sql = "INSERT INTO fitness_goals (user_id, bmi, bmi_category, bmr, tdee, daily_calorie_target, " +
                         "protein_grams, carbs_grams, fat_grams, estimated_weeks) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, g.getUserId());
                ps.setDouble(2, g.getBmi());
                ps.setString(3, g.getBmiCategory());
                ps.setDouble(4, g.getBmr());
                ps.setDouble(5, g.getTdee());
                ps.setDouble(6, g.getDailyCalorieTarget());
                ps.setDouble(7, g.getProteinGrams());
                ps.setDouble(8, g.getCarbsGrams());
                ps.setDouble(9, g.getFatGrams());
                ps.setInt(10, g.getEstimatedWeeks());
                ps.executeUpdate();
                return;
            } catch (SQLException e) {
                System.err.println("[DAO] JDBC saveFitnessGoal error: " + e.getMessage());
            }
        }

        if (g.getId() == 0) {
            g.setId(goalsSeq.getAndIncrement());
        }
        goalsStore.put(g.getUserId(), g);
    }

    public FitnessGoal getLatestGoal(int userId) {
        if (useJdbc) {
            String sql = "SELECT * FROM fitness_goals WHERE user_id = ? ORDER BY id DESC LIMIT 1";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        FitnessGoal fg = new FitnessGoal(
                            rs.getInt("user_id"),
                            rs.getDouble("bmi"),
                            rs.getString("bmi_category"),
                            rs.getDouble("bmr"),
                            rs.getDouble("tdee"),
                            rs.getDouble("daily_calorie_target"),
                            rs.getDouble("protein_grams"),
                            rs.getDouble("carbs_grams"),
                            rs.getDouble("fat_grams"),
                            rs.getInt("estimated_weeks")
                        );
                        fg.setId(rs.getInt("id"));
                        return fg;
                    }
                }
            } catch (SQLException e) {
                System.err.println("[DAO] JDBC getLatestGoal error: " + e.getMessage());
            }
        }

        return goalsStore.get(userId);
    }

    // -------------------------------------------------------------
    // AI WORKOUT SESSION LOGGING
    // -------------------------------------------------------------
    public void logAIWorkoutSession(AIWorkoutSession s) {
        if (useJdbc) {
            String sql = "INSERT INTO ai_workout_sessions (user_id, exercise_name, repetitions_completed, " +
                         "valid_reps, invalid_reps, duration_seconds, average_form_score, form_category, " +
                         "calories_burned, feedback_summary) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, s.getUserId());
                ps.setString(2, s.getExerciseName());
                ps.setInt(3, s.getRepetitionsCompleted());
                ps.setInt(4, s.getValidReps());
                ps.setInt(5, s.getInvalidReps());
                ps.setInt(6, s.getDurationSeconds());
                ps.setDouble(7, s.getAverageFormScore());
                ps.setString(8, s.getFormCategory());
                ps.setDouble(9, s.getCaloriesBurned());
                ps.setString(10, s.getFeedbackSummary());
                ps.executeUpdate();
                return;
            } catch (SQLException e) {
                System.err.println("[DAO] JDBC logAIWorkoutSession error: " + e.getMessage());
            }
        }

        if (s.getId() == 0) {
            s.setId(sessionSeq.getAndIncrement());
        }
        aiSessionStore.add(s);
    }

    public List<AIWorkoutSession> getSessionsForUser(int userId) {
        List<AIWorkoutSession> list = new ArrayList<>();
        if (useJdbc) {
            String sql = "SELECT * FROM ai_workout_sessions WHERE user_id = ? ORDER BY id DESC";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        AIWorkoutSession s = new AIWorkoutSession(
                            rs.getInt("user_id"),
                            rs.getString("exercise_name"),
                            rs.getInt("repetitions_completed"),
                            rs.getInt("valid_reps"),
                            rs.getInt("invalid_reps"),
                            rs.getInt("duration_seconds"),
                            rs.getDouble("average_form_score"),
                            rs.getString("form_category"),
                            rs.getDouble("calories_burned"),
                            rs.getString("feedback_summary")
                        );
                        s.setId(rs.getInt("id"));
                        list.add(s);
                    }
                    return list;
                }
            } catch (SQLException e) {
                System.err.println("[DAO] JDBC getSessionsForUser error: " + e.getMessage());
            }
        }

        for (AIWorkoutSession s : aiSessionStore) {
            if (s.getUserId() == userId) {
                list.add(s);
            }
        }
        return list;
    }
}
