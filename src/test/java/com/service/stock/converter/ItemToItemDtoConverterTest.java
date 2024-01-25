package com.service.stock.converter;

import com.service.stock.dto.ItemDto;
import com.service.stock.entity.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;

import static org.junit.jupiter.api.Assertions.*;

public class ItemToItemDtoConverterTest {

    private ItemToItemDtoConverter converter;

    @BeforeEach
    public void setUp() {
        converter = new ItemToItemDtoConverter();
    }

    @ParameterizedTest
    @NullSource
    public void convert_whenItemIsNull_throwIllegalArgumentException(Item item) {
        assertThrows(IllegalArgumentException.class, () -> converter.convert(item));
    }

    @Test
    public void convert_whenItemFieldsAreNull_success() {
        Item item = new Item();

        ItemDto itemDto = converter.convert(item);

        assertNotNull(itemDto);
        assertEquals(itemDto.getId(), item.getId());
        assertEquals(itemDto.getName(), item.getName());
        assertEquals(itemDto.getStockAvailable(), item.getStockAvailable());
    }

    @Test
    public void convert_success() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Name");
        item.setStockAvailable(1000L);
        item.setStockReserved(1000L);

        ItemDto itemDto = converter.convert(item);

        assertNotNull(itemDto);
        assertEquals(itemDto.getId(), item.getId());
        assertEquals(itemDto.getName(), item.getName());
        assertEquals(itemDto.getStockAvailable(), item.getStockAvailable());
        assertEquals(itemDto.getStockReserved(), item.getStockReserved());
    }

}
