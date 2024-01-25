package com.service.stock.converter;

import com.service.stock.dto.ItemDto;
import com.service.stock.entity.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ItemFromItemDtoUpdaterTest {

    private ItemFromItemDtoUpdater itemFromItemDtoUpdater;

    @BeforeEach
    public void setUp() {
        itemFromItemDtoUpdater = new ItemFromItemDtoUpdater();
    }

    @Test
    public void update_whenItemDtoIsValid_success() {
        Item item = new Item();
        item.setId(1L);
        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemFromItemDtoUpdater.update(itemDto, item);

        assertEquals(itemDto.getName(), item.getName());
    }

    @ParameterizedTest
    @NullSource
    public void update_whenItemDtoIsNull_throwIllegalArgumentException(ItemDto nullItemDto) {
        assertThrows(IllegalArgumentException.class,
                () -> itemFromItemDtoUpdater.update(nullItemDto, new Item()));
    }

    @ParameterizedTest
    @NullSource
    public void update_whenItemIsNull_throwIllegalArgumentException(Item nullItem) {
        assertThrows(IllegalArgumentException.class,
                () -> itemFromItemDtoUpdater.update(new ItemDto(), nullItem));
    }
}
