package com.mycompany.dao;

import com.mycompany.model.User;
import com.mycompany.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserDAO {
    private static final Logger logger = LoggerFactory.getLogger(UserDAO.class);
    private static final String LOGIN_SQL =
            "SELECT username, user_role FROM users WHERE username = ? AND password = ?";

    public User authenticate(String username, String password) {
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(LOGIN_SQL)) {

            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String role = rs.getString("user_role");
                    logger.info("User {} successfully authenticated as {}", username, role);
                    return new User(username, role);
                }
            }
        } catch (SQLException e) {
            logger.error("Database error during authentication for user: {}", username, e);
        }
        return null;
    }
}