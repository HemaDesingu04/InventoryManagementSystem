package com.mycompany;

import com.mycompany.dao.ProductDAO;
import com.mycompany.dao.UserDAO;
import com.mycompany.model.Product;
import com.mycompany.model.User;
import java.util.List;
import java.util.Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {
    private static final Logger logger = LoggerFactory.getLogger(App.class);
    private static final ProductDAO productDAO = new ProductDAO();
    private static final UserDAO userDAO = new UserDAO();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        logger.info("Inventory Management System Started.");
        if (scanner.hasNextLine()) {
            scanner.nextLine();
        }

        boolean running = true;
        while (running) {
            System.out.println("\n===== SYSTEM ACCESS =====");
            System.out.println("1. ADMIN Login (Add/Edit/Delete)");
            System.out.println("2. VIEW Inventory (Customer Access)");
            System.out.println("3. Exit Application");
            System.out.print("Enter choice: ");

            try {
                int choice = scanner.nextInt();
                scanner.nextLine();

                if (choice == 1) {

                    User loggedInUser = authenticateUser();
                    if (loggedInUser != null && loggedInUser.getRole().equals("ADMIN")) {
                        runInventoryMenu();
                    } else {
                        System.err.println("Authentication Failed or insufficient role.");
                    }
                } else if (choice == 2) {

                    System.out.println("\n--- Viewing Current Inventory ---");
                    viewInventory();
                    System.out.println("--- End of Inventory ---\n");
                } else if (choice == 3) {
                    running = false;
                    logger.info("Application Shutting Down.");
                } else {
                    System.out.println("Invalid choice. Please enter 1, 2, or 3.");
                }
            } catch (java.util.InputMismatchException e) {
                System.err.println("Invalid input. Please enter a number.");
                logger.warn("Invalid input received in main menu.", e);
                scanner.nextLine();
            }
        }
        scanner.close();
    }

    private static User authenticateUser() {
        System.out.println("\n===== ADMIN LOGIN =====");
        int attempts = 3;
        while (attempts > 0) {
            System.out.print("Username: ");
            String username = scanner.nextLine();
            System.out.print("Password: ");
            String password = scanner.nextLine();

            User user = userDAO.authenticate(username, password);

            if (user != null) {
                return user;
            }

            attempts--;
            System.err.println("Invalid credentials. Attempts remaining: " + attempts);
        }
        return null;
    }

    private static void runInventoryMenu() {
        boolean adminRunning = true;
        while (adminRunning) {
            System.out.println("\n===== Inventory Management Menu =====");
            System.out.println("1. View All Products");
            System.out.println("2. Add New Product");
            System.out.println("3. Update Existing Product");
            System.out.println("4. Delete Product");
            System.out.println("5. Return to Main Menu");
            System.out.print("Enter choice: ");

            try {
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1: viewInventory(); break;
                    case 2: addProduct(); break;
                    case 3: updateProduct(); break;
                    case 4: deleteProduct(); break;
                    case 5: adminRunning = false; logger.info("Admin returned to main access menu."); break;
                    default: System.out.println("Invalid choice.");
                }
            } catch (java.util.InputMismatchException e) {
                System.err.println("Invalid input. Please enter a number.");
                scanner.nextLine();
            }
        }
    }


    private static void viewInventory() {
        System.out.println("\n---------------------------------------------------------");
        System.out.println("| ID   | Name                 | Quantity   | Price      |");
        System.out.println("---------------------------------------------------------");

        List<Product> products = productDAO.selectAllProducts();
        if (products.isEmpty()) {
            System.out.println("| **Inventory is currently empty.** |");
        } else {
            products.forEach(System.out::println);
        }
        System.out.println("---------------------------------------------------------");
    }

    private static void addProduct() {
        System.out.print("Enter Product Name: ");
        String name = scanner.nextLine();

        int quantity = readIntInput("Enter Quantity: ");
        double price = readDoubleInput("Enter Price: $");

        int id = 0;
        Product newProduct = new Product(id, name, quantity, price);

        if (productDAO.insertProduct(newProduct)) {
            System.out.println("SUCCESS: Product '" + name + "' added with ID: " + newProduct.getId());
        } else {
            System.err.println("FAILURE: Could not add product. Check database connection or logs.");
        }
    }

    private static void updateProduct() {
        int id = readIntInput("Enter ID of Product to Update: ");
        Product existingProduct = productDAO.selectProductById(id);

        if (existingProduct == null) {
            System.err.println("Error: No product found with ID " + id);
            return;
        }

        System.out.println("\n--- Current Details ---");
        System.out.println(existingProduct);
        System.out.println("-----------------------");

        System.out.print("Enter NEW Name (Current: " + existingProduct.getName() + "): ");
        String newName = scanner.nextLine();
        if (!newName.isEmpty()) {
            existingProduct.setName(newName);
        }

        int newQuantity = readIntInput("Enter NEW Quantity (Current: " + existingProduct.getQuantity() + "): ");
        existingProduct.setQuantity(newQuantity);

        double newPrice = readDoubleInput("Enter NEW Price (Current: $" + existingProduct.getPrice() + "): $");
        existingProduct.setPrice(newPrice);

        if (productDAO.updateProduct(existingProduct)) {
            System.out.println("SUCCESS: Product ID " + id + " updated successfully.");
        } else {
            System.err.println("FAILURE: Could not update product.");
        }
    }

    private static void deleteProduct() {
        int id = readIntInput("Enter ID of Product to DELETE: ");

        System.out.print("ARE YOU SURE you want to delete Product ID " + id + "? (Y/N): ");
        String confirmation = scanner.nextLine().trim().toUpperCase();

        if (confirmation.equals("Y")) {
            if (productDAO.deleteProduct(id)) {
                System.out.println("SUCCESS: Product ID " + id + " deleted.");
            } else {
                System.err.println("FAILURE: Could not delete product. ID may not exist.");
            }
        } else {
            System.out.println("Deletion cancelled.");
        }
    }

    private static int readIntInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                int value = scanner.nextInt();
                scanner.nextLine();
                return value;
            } catch (java.util.InputMismatchException e) {
                System.err.println("Invalid input. Please enter a whole number.");
                scanner.nextLine();
            }
        }
    }

    private static double readDoubleInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                double value = scanner.nextDouble();
                scanner.nextLine();
                return value;

            } catch (java.util.InputMismatchException e) {
                System.err.println("Invalid input. Please enter a number (e.g., 10.99).");
                scanner.nextLine();
            }
        }
    }
}