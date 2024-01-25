package com.service.stock.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ValidationException extends ServiceException {

    public ValidationException(String errorMessage) {
        super(errorMessage);
    }

}
