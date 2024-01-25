package com.service.stock.validation;

import com.service.stock.entity.Item;
import com.service.stock.exception.InsufficientAvailableStockException;
import com.service.stock.exception.InsufficientReserveStockException;
import com.service.stock.model.Order;
import com.service.stock.model.OrderStatus;
import com.service.stock.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ItemStockValidatorTest {

    private ItemStockValidator itemStockValidator;

    @BeforeEach
    public void setUp() {
        this.itemStockValidator = new ItemStockValidator();
    }

    @Test
    public void validateReservationCreation_whenItemAndOrderAreNull_throwIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> itemStockValidator.validateReservationCreation(null, null));
    }

    @Test
    public void validateReservationRollback_whenItemAndOrderAreNull_throwIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> itemStockValidator.validateReservationRollback(null, null));
    }

    @Test
    public void validateReservationConfirmation_whenItemAndOrderAreNull_throwIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> itemStockValidator.validateReservationConfirmation(null, null));
    }

    @Test
    public void validateReservationCreation_whenValid_doNotThrowAnyExceptions() {
        Item item = new Item(1L, "testName", 100L, 0L);

        Product product = new Product();
        product.setId(1L);
        product.setQuantity(1);
        product.setPrice(50L);

        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setStatus(OrderStatus.NEW);
        order.setSource("OnlineTestShop");
        order.setCustomerId(1L);
        order.setProduct(product);

        assertDoesNotThrow(() -> itemStockValidator.validateReservationCreation(item, order));
    }

    @Test
    public void validateReservationCreation_whenInvalid_throwInsufficientAvailableStockException() {
        Item item = new Item(1L, "testName", 0L, 0L);

        Product product = new Product();
        product.setId(1L);
        product.setQuantity(1);
        product.setPrice(500L);

        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setStatus(OrderStatus.NEW);
        order.setSource("OnlineTestShop");
        order.setCustomerId(1L);
        order.setProduct(product);

        assertThrows(InsufficientAvailableStockException.class,
                () -> itemStockValidator.validateReservationCreation(item, order));
    }

    @Test
    public void validateReservationRollback_whenValid_doNotThrowAnyExceptions() {
        Item item = new Item(1L, "testName", 100L, 50L);

        Product product = new Product();
        product.setId(1L);
        product.setQuantity(1);
        product.setPrice(50L);

        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setStatus(OrderStatus.NEW);
        order.setSource("OnlineTestShop");
        order.setCustomerId(1L);
        order.setProduct(product);

        assertDoesNotThrow(() -> itemStockValidator.validateReservationRollback(item, order));
    }

    @Test
    public void validateReservationRollback_whenInvalid_throwInsufficientReserveStockException() {
        Item item = new Item(1L, "testName", 100L, 0L);

        Product product = new Product();
        product.setId(1L);
        product.setQuantity(1);
        product.setPrice(500L);

        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setStatus(OrderStatus.NEW);
        order.setSource("OnlineTestShop");
        order.setCustomerId(1L);
        order.setProduct(product);

        assertThrows(InsufficientReserveStockException.class,
                () -> itemStockValidator.validateReservationRollback(item, order));
    }

    @Test
    public void validateReservationConfirmation_whenValid_doNotThrowAnyExceptions() {
        Item item = new Item(1L, "testName", 100L, 50L);

        Product product = new Product();
        product.setId(1L);
        product.setQuantity(1);
        product.setPrice(50L);

        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setStatus(OrderStatus.NEW);
        order.setSource("OnlineTestShop");
        order.setCustomerId(1L);
        order.setProduct(product);

        assertDoesNotThrow(() -> itemStockValidator.validateReservationConfirmation(item, order));
    }

    @Test
    public void validateReservationConfirmation_whenInvalid_throwInsufficientReserveStockException() {
        Item item = new Item(1L, "testName", 100L, 0L);

        Product product = new Product();
        product.setId(1L);
        product.setQuantity(1);
        product.setPrice(500L);

        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setStatus(OrderStatus.NEW);
        order.setSource("OnlineTestShop");
        order.setCustomerId(1L);
        order.setProduct(product);

        assertThrows(InsufficientReserveStockException.class,
                () -> itemStockValidator.validateReservationConfirmation(item, order));
    }


}
