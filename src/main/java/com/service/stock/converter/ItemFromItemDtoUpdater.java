package com.service.stock.converter;

import com.service.stock.dto.ItemDto;
import com.service.stock.entity.Item;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class ItemFromItemDtoUpdater {

    private final ModelMapper modelMapper;

    public ItemFromItemDtoUpdater() {
        this.modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setSkipNullEnabled(true);

    }

    public void update(ItemDto itemDto, Item item) {
        modelMapper.map(itemDto, item);
    }
}
