package com.service.stock.controller;

import com.service.stock.dto.ItemDto;
import com.service.stock.dto.DtoSearchResponse;
import com.service.stock.service.ItemService;
import com.service.stock.utils.PaginationSortingUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/items")
@Slf4j
@RequiredArgsConstructor
public class ItemController {

    private final ItemService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Long create(@RequestBody ItemDto itemDto) {
        return service.create(itemDto);
    }

    @PatchMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto update(@RequestBody ItemDto itemDto, @PathVariable Long itemId) {
        return service.update(itemDto, itemId);
    }

    @GetMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto getById(@PathVariable Long itemId) {
        return service.findByIdAsDto(itemId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public DtoSearchResponse getAll(@RequestParam(defaultValue = "100") int limit,
                                    @RequestParam(defaultValue = "0") int offset,
                                    @RequestParam(defaultValue = "id,asc") String[] sort) {
        Pageable pageable = PaginationSortingUtils.getPageable(limit, offset, sort);
        return service.findAll(pageable);
    }

    @DeleteMapping("/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long itemId) {
        service.deleteById(itemId);
    }
}
