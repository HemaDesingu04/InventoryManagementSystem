package com.mycompany.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// OOP: Singleton Design Pattern
public class DBConnection {
    // Logging Setup
    private static final Logger logger = LoggerFactory.getLogger(DBConnection.class);

    private static final String URL = "jdbc:mysql://localhost:3306/inventory_db";
    private static final String USER = "hema";
    private static final String PASSWORD = "Hema08@sql";

    private static DBConnection instance = null;

    private DBConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            logger.info("MySQL JDBC Driver loaded successfully.");
        } catch (ClassNotFoundException e) {
            logger.error("Error loading JDBC Driver.", e);
            throw new RuntimeException("Could not load JDBC Driver", e);
        }
    }

    public static DBConnection getInstance() {
        if (instance == null) {
            instance = new DBConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            logger.error("Error establishing database connection.", e);
            System.err.println("SQL Error: " + e.getMessage());
        }
        return connection;
    }
}