package io.quarkusdroneshop.qdca10.domain;

import io.quarkusdroneshop.domain.*;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Map;

import static org.junit.Assert.*;

@QuarkusTest @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class InventoryTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(InventoryTest.class);

    @Inject
    Inventory inventory;

    @Test @Order(1)
    public void testStockIsPopulated() {

        Map<Item, Integer> inStock = inventory.getStock();
        assertNotNull(inStock);
        inStock.forEach((k,v) -> {
            LOGGER.info(k + " " + v);
        });
    }

    @Test @Order(2)
    public void testDecrementCoffee() {

        Integer totalCoffee = inventory.getTotalCoffee();
        LOGGER.info("total drone: {}", totalCoffee);
        assertTrue(inventory.decrementItem(Item.QDC_A102));
        Integer updatedCoffee = inventory.getTotalCoffee();
        LOGGER.info("total drone after decrementing: {}", updatedCoffee);
        assertTrue(updatedCoffee == totalCoffee - 1);
    }

    @Test @Order(3)
    public void testEightySixCoffee() {

        Integer totalCoffee = inventory.getTotalCoffee();
        for (int i = 0; i < totalCoffee; i++) {
            assertTrue(inventory.decrementItem(Item.QDC_A101));
        }
        assertFalse(inventory.decrementItem(Item.QDC_A101));
    }
}
