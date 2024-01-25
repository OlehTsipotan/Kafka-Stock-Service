package com.service.stock.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.service.stock.dto.ItemDto;
import com.service.stock.dto.DtoSearchResponse;
import com.service.stock.service.ItemService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({ItemController.class})
public class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void create_whenItemDTOIsValid_success() throws Exception {
        when(itemService.create(any(ItemDto.class))).thenReturn(1L);

        ItemDto itemDto = new ItemDto();
        itemDto.setName("name");
        itemDto.setStockAvailable(1000L);
        itemDto.setStockReserved(1000L);

        mockMvc.perform(post("/items").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto))).andExpect(status().isCreated())
                .andExpect(content().string("1"));

        verify(itemService).create(itemDto);
        verifyNoMoreInteractions(itemService);
    }

    @ParameterizedTest
    @NullSource
    public void create_whenItemDTOIsNull_statusIsBadRequest(ItemDto itemDto) throws Exception {
        mockMvc.perform(post("/items").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(itemDto))).andExpect(status().isBadRequest());

        verifyNoInteractions(itemService);
    }

    @Test
    public void delete_success() throws Exception {
        mockMvc.perform(delete("/items/1")).andExpect(status().isNoContent());

        verify(itemService).deleteById(1L);
        verifyNoMoreInteractions(itemService);
    }

    @Test
    public void delete_whenItemIdIsInvalid_statusIsBadRequest() throws Exception {
        mockMvc.perform(delete("/items/invalid")).andExpect(status().isBadRequest());

        verifyNoInteractions(itemService);
    }

    @Test
    public void update_whenItemDTOIsValid_success() throws Exception {
        ItemDto itemDTOToDisplay = new ItemDto();
        itemDTOToDisplay.setName("name");
        itemDTOToDisplay.setStockAvailable(1000L);
        itemDTOToDisplay.setStockReserved(1000L);
        ItemDto itemDto = new ItemDto();
        itemDto.setName("name");
        itemDto.setStockAvailable(1000L);
        itemDto.setStockReserved(1000L);

        when(itemService.update(any(ItemDto.class), any(Long.class))).thenReturn(itemDTOToDisplay);

        mockMvc.perform(patch("/items/1").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto))).andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemDTOToDisplay)));

        verify(itemService).update(itemDto, 1L);
        verifyNoMoreInteractions(itemService);
    }

    @ParameterizedTest
    @NullSource
    public void update_whenItemDTOIsNull_statusIsBadRequest(ItemDto itemDto) throws Exception {
        mockMvc.perform(patch("/items/1").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(itemDto))).andExpect(status().isBadRequest());

        verifyNoInteractions(itemService);
    }

    @Test
    public void update_whenItemIdIsInvalid_statusIsBadRequest() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("name");
        itemDto.setStockAvailable(1000L);
        itemDto.setStockReserved(1000L);

        mockMvc.perform(patch("/items/invalid").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(itemDto))).andExpect(status().isBadRequest());

        verifyNoInteractions(itemService);
    }

    @Test
    public void getById_success() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("name");
        itemDto.setStockAvailable(1000L);
        itemDto.setStockReserved(1000L);

        when(itemService.findByIdAsDto(any(Long.class))).thenReturn(itemDto);

        mockMvc.perform(get("/items/1")).andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemDto)));

        verify(itemService).findByIdAsDto(1L);
        verifyNoMoreInteractions(itemService);
    }

    @Test
    public void getById_whenItemIdIsInvalid_statusIsBadRequest() throws Exception {
        mockMvc.perform(get("/items/invalid")).andExpect(status().isBadRequest());

        verifyNoInteractions(itemService);
    }

    @Test
    public void getAll_success() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("name");
        itemDto.setStockAvailable(1000L);
        itemDto.setStockReserved(1000L);
        List<ItemDto> itemDTOList = List.of(itemDto);
        DtoSearchResponse dtoSearchResponse = DtoSearchResponse.builder().data(itemDTOList).build();

        when(itemService.findAll(any())).thenReturn(dtoSearchResponse);

        mockMvc.perform(get("/items")).andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dtoSearchResponse)));

        verify(itemService).findAll(any());
        verifyNoMoreInteractions(itemService);
    }

    // It is not depends on the parameter selection.
    @Test
    public void getAll_whenLimitIsInvalid_statusIsBadRequest() throws Exception {
        mockMvc.perform(get("/items?limit=invalid")).andExpect(status().isBadRequest());

        verifyNoInteractions(itemService);
    }

}