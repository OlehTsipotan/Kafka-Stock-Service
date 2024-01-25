package com.service.stock.converter;

import com.service.stock.dto.ItemDto;
import com.service.stock.entity.Item;
import lombok.NonNull;
import org.modelmapper.ModelMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ItemToItemDtoConverter implements Converter<Item, ItemDto> {

    private final ModelMapper modelMapper;

    public ItemToItemDtoConverter() {
        this.modelMapper = new ModelMapper();
    }

    @Override
    @NonNull
    public ItemDto convert(@NonNull Item source) {
        return modelMapper.map(source, ItemDto.class);
    }
}
