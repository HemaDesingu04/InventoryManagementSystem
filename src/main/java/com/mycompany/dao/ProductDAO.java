package com.mycompany.dao;

import com.mycompany.model.Product;
import com.mycompany.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProductDAO {
    private static final Logger logger = LoggerFactory.getLogger(ProductDAO.class);

    // SQL Queries
    private static final String INSERT_SQL = "INSERT INTO products (name, quantity, price) VALUES (?, ?, ?)";
    private static final String SELECT_ALL_SQL = "SELECT * FROM products ORDER BY product_id";
    private static final String SELECT_BY_ID_SQL = "SELECT * FROM products WHERE product_id = ?";
    private static final String UPDATE_SQL = "UPDATE products SET name = ?, quantity = ?, price = ? WHERE product_id = ?";
    private static final String DELETE_SQL = "DELETE FROM products WHERE product_id = ?";

    // --- C (CREATE) ---
    public boolean insertProduct(Product product) {
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, product.getName());
            ps.setInt(2, product.getQuantity());
            ps.setDouble(3, product.getPrice());

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        int generatedId = rs.getInt(1);
                        product.setId(generatedId);
                    }
                }
                logger.info("Product inserted successfully. Name: {} with ID: {}", product.getName(), product.getId());
                return true;
            }
            return false;

        } catch (SQLException e) {
            logger.error("Error inserting product: {}", product.getName(), e);
            return false;
        }
    }

    public Product selectProductById(int id) {
        Product product = null;
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(SELECT_BY_ID_SQL)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String name = rs.getString("name");
                    int quantity = rs.getInt("quantity");
                    double price = rs.getDouble("price");
                    product = new Product(id, name, quantity, price);
                }
            }
        } catch (SQLException e) {
            logger.error("Error selecting product ID: {}", id, e);
        }
        return product;
    }

    public List<Product> selectAllProducts() {
        List<Product> products = new ArrayList<>();
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(SELECT_ALL_SQL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("product_id");
                String name = rs.getString("name");
                int quantity = rs.getInt("quantity");
                double price = rs.getDouble("price");
                products.add(new Product(id, name, quantity, price));
            }
        } catch (SQLException e) {
            logger.error("Error selecting all products.", e);
        }
        return products;
    }

    public boolean updateProduct(Product product) {
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(UPDATE_SQL)) {

            ps.setString(1, product.getName());
            ps.setInt(2, product.getQuantity());
            ps.setDouble(3, product.getPrice());
            ps.setInt(4, product.getId());

            int affectedRows = ps.executeUpdate();
            logger.info("Updated product ID: {}", product.getId());
            return affectedRows > 0;

        } catch (SQLException e) {
            logger.error("Error updating product ID: {}", product.getId(), e);
            return false;
        }
    }

    public boolean deleteProduct(int id) {
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(DELETE_SQL)) {

            ps.setInt(1, id);

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Deleted product ID: {}", id);
            } else {
                logger.warn("No product found with ID: {} for deletion.", id);
            }
            return affectedRows > 0;

        } catch (SQLException e) {
            logger.error("Error deleting product ID: {}", id, e);
            return false;
        }
    }
}