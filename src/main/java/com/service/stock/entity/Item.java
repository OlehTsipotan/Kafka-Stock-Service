package com.service.stock.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

@Getter
@Setter
@Entity
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "item")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "item_generator")
    @SequenceGenerator(name = "item_generator", sequenceName = "item_seq", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @NotNull(message = "Item name must not be null")
    @Column(name = "name", nullable = false)
    private String name;

    @PositiveOrZero(message = "Item stockAvailable must be positive or zero")
    @NotNull(message = "Item balanceAvailable must not be null")
    @Column(name = "stock_available", nullable = false)
    private Long stockAvailable;

    @PositiveOrZero(message = "Item stockReserved must be positive or zero")
    @NotNull(message = "Item balanceReserved must not be null")
    @Column(name = "stock_reserved", nullable = false)
    private Long stockReserved;
}
