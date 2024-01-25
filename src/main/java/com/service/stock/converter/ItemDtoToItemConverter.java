package com.service.stock.converter;

import com.service.stock.dto.ItemDto;
import com.service.stock.entity.Item;
import lombok.NonNull;
import org.modelmapper.ModelMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ItemDtoToItemConverter implements Converter<ItemDto, Item> {

    private final ModelMapper modelMapper;

    public ItemDtoToItemConverter() {
        this.modelMapper = new ModelMapper();
    }

    @Override
    @NonNull
    public Item convert(@NonNull ItemDto source) {
        return modelMapper.map(source, Item.class);
    }
}
