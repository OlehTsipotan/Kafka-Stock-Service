package com.service.stock.validation;

import com.service.stock.entity.Item;
import com.service.stock.exception.InsufficientAvailableStockException;
import com.service.stock.exception.InsufficientReserveStockException;
import com.service.stock.model.Order;
import com.service.stock.model.Product;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ItemStockValidator {

    public void validateReservationCreation(@NonNull Item item, @NonNull Order order) {
        Product product = order.getProduct();
        if (product == null) {
            throw new IllegalArgumentException("Product is null for order with id = " + order.getId());
        }
        if (item.getStockAvailable() < product.getQuantity()) {
            throw new InsufficientAvailableStockException(
                    "Item with id = " + item.getId() + " has not enough stock to fulfill for order with id" + " =" +
                            " " + order.getId());
        }
    }

    public void validateReservationRollback(@NonNull Item item, @NonNull Order order) {
        Product product = order.getProduct();
        if (product == null) {
            throw new IllegalArgumentException("Product is null for order with id = " + order.getId());
        }
        if (item.getStockReserved() < product.getQuantity()) {
            throw new InsufficientReserveStockException(
                    "Item with id = " + item.getId() + " has not enough reserved stock to fulfill " +
                            "order with id" + " =" + " " + order.getId());
        }
    }

    public void validateReservationConfirmation(@NonNull Item item, @NonNull Order order) {
        Product product = order.getProduct();
        if (product == null) {
            throw new IllegalArgumentException("Product is null for order with id = " + order.getId());
        }
        if (item.getStockReserved() < product.getQuantity()) {
            throw new InsufficientReserveStockException(
                    "Item with id = " + item.getId() + " has not enough reserved stock to fulfill for order with id" + " =" +
                            " " + order.getId());
        }
    }
}
