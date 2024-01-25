package com.service.stock.converter;

import com.service.stock.dto.ItemDto;
import com.service.stock.entity.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;

import static org.junit.jupiter.api.Assertions.*;

public class ItemDtoToItemConverterTest {

    private ItemDtoToItemConverter converter;

    @BeforeEach
    public void setUp() {
        converter = new ItemDtoToItemConverter();
    }

    @ParameterizedTest
    @NullSource
    public void convert_whenItemDtoIsNull_throwIllegalArgumentException(ItemDto itemDto) {
        assertThrows(IllegalArgumentException.class, () -> converter.convert(itemDto));
    }

    @Test
    public void convert_whenItemDtoFieldsAreNull_success() {
        ItemDto itemDto = new ItemDto();

        Item item = converter.convert(itemDto);

        assertNotNull(item);
        assertEquals(item.getId(), itemDto.getId());
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getStockAvailable(), itemDto.getStockAvailable());
    }

    @Test
    public void convert_success() {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Name");
        itemDto.setStockAvailable(1000L);
        itemDto.setStockReserved(1000L);

        Item item = converter.convert(itemDto);

        assertNotNull(item);
        assertEquals(item.getId(), itemDto.getId());
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getStockAvailable(), itemDto.getStockAvailable());
        assertEquals(item.getStockReserved(), itemDto.getStockReserved());
    }

}
