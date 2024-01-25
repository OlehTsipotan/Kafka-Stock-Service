package com.service.stock.validation;

import com.service.stock.entity.Item;
import com.service.stock.exception.EntityValidationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
public class ItemValidatorTest {

    private final Validator jakartaValidator = Validation.buildDefaultValidatorFactory().getValidator();
    private ItemValidator validator;

    @BeforeEach
    public void setUp() {
        validator = new ItemValidator(jakartaValidator);
    }

    @Test
    public void validate_whenItemIsValid() {
        Item item = new Item();

        item.setId(1L);
        item.setName("testName");
        item.setStockAvailable(100L);
        item.setStockReserved(0L);

        assertDoesNotThrow(() -> validator.validate(item));
    }

    @Test
    public void validate_whenItemIsInvalid() {
        Item item = new Item();

        item.setId(1L);
        item.setName("testName");
        item.setStockAvailable(100L);
        item.setStockReserved(-50L);

        assertThrows(EntityValidationException.class, () -> validator.validate(item));
    }

    @ParameterizedTest
    @NullSource
    public void validate_whenItemIsNull_throwIllegalArgumentException(Item item) {
        assertThrows(IllegalArgumentException.class, () -> validator.validate(item));
    }
}
