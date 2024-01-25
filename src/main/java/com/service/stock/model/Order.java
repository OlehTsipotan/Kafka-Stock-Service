package com.service.stock.model;

import lombok.Data;

import java.util.UUID;

@Data
public class Order {

    private UUID id;

    private Long customerId;

    private Product product;

    private OrderStatus status;

    private String source;

    private String description;

    public Long getTotalPrice() {
        return product.getTotalPrice();
    }
}

