package com.service.stock.exception;

public class InsufficientReserveStockException extends RuntimeException {
    public InsufficientReserveStockException(String errorMessage) {
        super(errorMessage);
    }
}
