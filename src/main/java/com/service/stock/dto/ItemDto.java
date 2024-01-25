package com.service.stock.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.io.Serializable;

/**
 * DTO for {@link com.service.stock.entity.Item}
 */
@Data
public class ItemDto extends Dto implements Serializable {
    @NotNull(message = "item id must not be null") @PositiveOrZero Long id;
    @NotNull(message = "item name must not be null") @NotBlank String name;
    @NotNull(message = "item balanceAvailable must not be null") @PositiveOrZero Long stockAvailable;
    @NotNull(message = "item balanceReserved must not be null") @PositiveOrZero Long stockReserved;
}
