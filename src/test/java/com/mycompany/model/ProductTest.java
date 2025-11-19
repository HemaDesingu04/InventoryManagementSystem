package com.mycompany.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ProductTest {

    @Test
    void testProductCreationAndGetters() {
        Product product = new Product(101, "Test Item", 5, 99.99);

        assertEquals(101, product.getId(), "Product ID should match the set value.");
        assertEquals("Test Item", product.getName(), "Product name should match the set value.");
        assertEquals(5, product.getQuantity(), "Product quantity should match the set value.");
        assertEquals(99.99, product.getPrice(), 0.001, "Product price should match the set value.");
    }
}