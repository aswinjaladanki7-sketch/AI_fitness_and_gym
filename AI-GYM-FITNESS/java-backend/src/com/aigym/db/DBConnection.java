package com.aigym.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * AI GYM & FITNESS ASSISTANT - JDBC Connection Manager
 * Course: 21CSC203P - PROJECT
 * 
 * Supports MySQL 8.0+ JDBC connector with graceful fallback detection.
 */
public class DBConnection {

    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 3306;
    private static final String DEFAULT_DATABASE = "ai_gym_fitness";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASSWORD = "";

    private static boolean isMySqlAvailable = false;

    static {
        // Test if MySQL Driver is in classpath
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            isMySqlAvailable = true;
        } catch (ClassNotFoundException e) {
            // Driver not in classpath; application continues in in-memory DAO mode
            isMySqlAvailable = false;
        }
    }

    public static boolean isDriverLoaded() {
        return isMySqlAvailable;
    }

    public static Connection getConnection() throws SQLException {
        if (!isMySqlAvailable) {
            throw new SQLException("MySQL JDBC Driver (com.mysql.cj.jdbc.Driver) not loaded in classpath.");
        }

        String host = System.getenv("DB_HOST") != null ? System.getenv("DB_HOST") : DEFAULT_HOST;
        String portStr = System.getenv("DB_PORT") != null ? System.getenv("DB_PORT") : String.valueOf(DEFAULT_PORT);
        String dbName = System.getenv("DB_NAME") != null ? System.getenv("DB_NAME") : DEFAULT_DATABASE;
        String user = System.getenv("DB_USER") != null ? System.getenv("DB_USER") : DEFAULT_USER;
        String password = System.getenv("DB_PASSWORD") != null ? System.getenv("DB_PASSWORD") : DEFAULT_PASSWORD;

        String url = String.format("jdbc:mysql://%s:%s/%s?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC",
                host, portStr, dbName);

        return DriverManager.getConnection(url, user, password);
    }
}
