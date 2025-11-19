package com.mycompany.dao;

import com.mycompany.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserDAOTest {
    private UserDAO userDAO;

    // Credentials
    private static final String ADMIN_USER = "admin";
    private static final String ADMIN_PASS = "inventory123";
    private static final String ADMIN_ROLE = "ADMIN";

    @BeforeEach
    void setUp() {
        userDAO = new UserDAO();
    }

    @AfterEach
    void tearDown() {
        userDAO = null;
    }

    @Test
    public void testSuccessfulAuthentication() {
        System.out.println("Running testSuccessfulAuthentication...");

        User authenticatedUser = userDAO.authenticate(ADMIN_USER, ADMIN_PASS);

        assertNotNull(authenticatedUser, "Authentication should succeed for valid credentials.");

        assertEquals(ADMIN_USER, authenticatedUser.getUsername(), "Username should match.");
        assertEquals(ADMIN_ROLE, authenticatedUser.getRole(), "Role should be ADMIN.");
    }

    @Test
    public void testAuthenticationWithBadPassword() {
        System.out.println("Running testAuthenticationWithBadPassword...");

        User authenticatedUser = userDAO.authenticate(ADMIN_USER, "wrongpassword");

        assertNull(authenticatedUser, "Authentication should fail for incorrect password.");
    }

    @Test
    public void testAuthenticationWithNonExistentUser() {
        System.out.println("Running testAuthenticationWithNonExistentUser...");

        User authenticatedUser = userDAO.authenticate("nonexistent", ADMIN_PASS);

        assertNull(authenticatedUser, "Authentication should fail for non-existent user.");
    }
}