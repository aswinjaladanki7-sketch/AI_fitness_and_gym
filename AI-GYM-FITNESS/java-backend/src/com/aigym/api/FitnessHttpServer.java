package com.aigym.api;

import com.aigym.ai.FormAnalyzer;
import com.aigym.ai.RepCounterFSM;
import com.aigym.model.DietMeal;
import com.aigym.model.DietPlan;
import com.aigym.model.Exercise;
import com.aigym.model.WorkoutPlan;
import com.aigym.service.BMIService;
import com.aigym.service.NutritionService;
import com.aigym.service.WorkoutService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Embedded Zero-Dependency REST API Server
 * Built with standard JDK com.sun.net.httpserver.HttpServer.
 * 
 * Exposes core services via REST endpoints to enable web frontend integration:
 * - GET  /api/health
 * - POST /api/fitness/calculate
 * - GET  /api/nutrition/plan
 * - GET  /api/workout/plan
 */
public class FitnessHttpServer {

    private final int port;
    private HttpServer server;

    public FitnessHttpServer(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);

        server.createContext("/api/health", new HealthHandler());
        server.createContext("/api/fitness/calculate", new FitnessCalculateHandler());
        server.createContext("/api/nutrition/plan", new NutritionPlanHandler());
        server.createContext("/api/workout/plan", new WorkoutPlanHandler());

        server.setExecutor(null); // default executor
        server.start();
        System.out.println(">>> [REST API] Java HTTP Server running on http://localhost:" + port);
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
        }
    }

    private static void sendCorsAndJsonResponse(HttpExchange exchange, int statusCode, String responseJson) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Authorization");

        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        byte[] bytes = responseJson.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private static Map<String, String> parseQueryParams(String query) {
        Map<String, String> map = new HashMap<>();
        if (query == null || query.isEmpty()) return map;
        for (String pair : query.split("&")) {
            String[] kv = pair.split("=");
            if (kv.length == 2) {
                map.put(kv[0].trim(), kv[1].trim());
            } else if (kv.length == 1) {
                map.put(kv[0].trim(), "");
            }
        }
        return map;
    }

    // Handlers
    static class HealthHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String json = "{\n" +
                    "  \"status\": \"online\",\n" +
                    "  \"server\": \"Java SE 21CSC203P REST Server\",\n" +
                    "  \"institution\": \"SRM INSTITUTE OF SCIENCE AND TECHNOLOGY\",\n" +
                    "  \"course\": \"21CSC203P - PROJECT\"\n" +
                    "}";
            sendCorsAndJsonResponse(exchange, 200, json);
        }
    }

    static class FitnessCalculateHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendCorsAndJsonResponse(exchange, 204, "");
                return;
            }

            Map<String, String> params = parseQueryParams(exchange.getRequestURI().getQuery());
            double weight = parseDouble(params.get("weight"), 70.0);
            double height = parseDouble(params.get("height"), 175.0);
            int age = parseInt(params.get("age"), 21);
            String gender = params.getOrDefault("gender", "male");
            String activity = params.getOrDefault("activity", "moderately_active");
            String goal = params.getOrDefault("goal", "weight_loss");

            BMIService.BMIResult bmi = BMIService.calculateBMI(weight, height);
            double bmr = BMIService.calculateBMR(weight, height, age, gender);
            double tdee = BMIService.calculateTDEE(bmr, activity);
            BMIService.MacroResult macro = BMIService.calculateCaloriesAndMacros(tdee, weight, goal, gender);

            String json = String.format("{\n" +
                    "  \"bmi\": %.2f,\n" +
                    "  \"bmi_category\": \"%s\",\n" +
                    "  \"bmr\": %.2f,\n" +
                    "  \"tdee\": %.2f,\n" +
                    "  \"daily_calories\": %.2f,\n" +
                    "  \"protein_g\": %.1f,\n" +
                    "  \"carbs_g\": %.1f,\n" +
                    "  \"fat_g\": %.1f\n" +
                    "}", bmi.bmi, bmi.category, bmr, tdee, macro.dailyCalories, macro.proteinG, macro.carbsG, macro.fatG);

            sendCorsAndJsonResponse(exchange, 200, json);
        }
    }

    static class NutritionPlanHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendCorsAndJsonResponse(exchange, 204, "");
                return;
            }

            Map<String, String> params = parseQueryParams(exchange.getRequestURI().getQuery());
            double targetCalories = parseDouble(params.get("calories"), 2100.0);
            String diet = params.getOrDefault("diet", "non_vegetarian");
            String allergies = params.getOrDefault("allergies", "");

            DietPlan plan = NutritionService.generateDailyDiet(1, targetCalories, 140, 240, 58, diet, allergies, 0);

            StringBuilder sb = new StringBuilder();
            sb.append("{\n");
            sb.append("  \"plan_name\": \"").append(plan.getPlanName()).append("\",\n");
            sb.append("  \"diet_type\": \"").append(plan.getDietType()).append("\",\n");
            sb.append("  \"target_calories\": ").append(plan.getTargetCalories()).append(",\n");
            sb.append("  \"actual_calories\": ").append(Math.round(plan.getTotalActualCalories())).append(",\n");
            sb.append("  \"meals\": [\n");

            for (int i = 0; i < plan.getMeals().size(); i++) {
                DietMeal m = plan.getMeals().get(i);
                sb.append(String.format("    {\"meal_type\": \"%s\", \"food\": \"%s\", \"portion\": \"%s\", \"calories\": %.1f, \"protein\": %.1f}%s\n",
                        m.getMealType(), m.getFoodName(), m.getQuantity(), m.getCalories(), m.getProteinG(), (i == plan.getMeals().size() - 1 ? "" : ",")));
            }
            sb.append("  ]\n}");

            sendCorsAndJsonResponse(exchange, 200, sb.toString());
        }
    }

    static class WorkoutPlanHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendCorsAndJsonResponse(exchange, 204, "");
                return;
            }

            Map<String, String> params = parseQueryParams(exchange.getRequestURI().getQuery());
            String goal = params.getOrDefault("goal", "weight_loss");
            String level = params.getOrDefault("level", "beginner");
            String location = params.getOrDefault("location", "both");

            WorkoutPlan plan = WorkoutService.generateWorkoutRoutine(1, goal, level, location, 4);

            StringBuilder sb = new StringBuilder();
            sb.append("{\n");
            sb.append("  \"title\": \"").append(plan.getTitle()).append("\",\n");
            sb.append("  \"goal\": \"").append(plan.getGoal()).append("\",\n");
            sb.append("  \"focus\": \"").append(plan.getFocus()).append("\",\n");
            sb.append("  \"frequency_days\": ").append(plan.getDaysPerWeek()).append(",\n");
            sb.append("  \"exercises\": [\n");

            for (int i = 0; i < plan.getExercises().size(); i++) {
                Exercise ex = plan.getExercises().get(i);
                sb.append(String.format("    {\"name\": \"%s\", \"target\": \"%s\", \"sets\": %d, \"reps\": %d, \"rest_sec\": %d}%s\n",
                        ex.getName(), ex.getTargetMuscle(), ex.getDefaultSets(), ex.getDefaultReps(), ex.getDefaultRestSeconds(),
                        (i == plan.getExercises().size() - 1 ? "" : ",")));
            }
            sb.append("  ]\n}");

            sendCorsAndJsonResponse(exchange, 200, sb.toString());
        }
    }

    private static double parseDouble(String val, double def) {
        if (val == null) return def;
        try { return Double.parseDouble(val); } catch (Exception e) { return def; }
    }

    private static int parseInt(String val, int def) {
        if (val == null) return def;
        try { return Integer.parseInt(val); } catch (Exception e) { return def; }
    }
}
