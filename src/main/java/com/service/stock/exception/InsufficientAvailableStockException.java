package com.service.stock.exception;

public class InsufficientAvailableStockException extends RuntimeException {
    public InsufficientAvailableStockException(String errorMessage) {
        super(errorMessage);
    }
}
