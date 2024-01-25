package com.service.stock.validation;

import com.service.stock.entity.Item;
import com.service.stock.exception.EntityValidationException;
import com.service.stock.exception.FieldViolation;
import jakarta.validation.Validator;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ItemValidator extends EntityValidator<Item> {

    public ItemValidator(Validator validator) {
        super(validator);
    }

    @Override
    public void validate(Item item) {
        List<FieldViolation> violations = new ArrayList<>();
        try {
            super.validate(item);
        } catch (EntityValidationException e) {
            violations = e.getViolations();
        }

        if (!violations.isEmpty()) {
            throw new EntityValidationException("Item is not valid", violations);
        }

    }
}
