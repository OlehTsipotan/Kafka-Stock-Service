package com.service.stock.controller;

import com.service.stock.dto.ItemDto;
import com.service.stock.dto.DtoSearchResponse;
import com.service.stock.entity.Item;
import com.service.stock.service.ItemService;
import com.service.stock.utils.PaginationSortingUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/items")
@Slf4j
@RequiredArgsConstructor
public class ItemController {

    private final ItemService service;

    @Operation(summary = "Create the Item")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Item created successfully", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Long.class))}),
            @ApiResponse(responseCode = "409", description = "Item already exists", content = @Content)})
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Long create(@RequestBody ItemDto itemDto) {
        return service.create(itemDto);
    }

    @Operation(summary = "Update the Item")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item updated successfully", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ItemDto.class))}),
            @ApiResponse(responseCode = "404", description = "Item not found", content = @Content)})
    @PatchMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto update(@RequestBody ItemDto itemDto, @PathVariable Long itemId) {
        return service.update(itemDto, itemId);
    }

    @Operation(summary = "Retrieve the Item by Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item retrieved successfully", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ItemDto.class))}),
            @ApiResponse(responseCode = "404", description = "Item not found", content = @Content)})
    @GetMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto getById(@PathVariable Long itemId) {
        return service.findByIdAsDto(itemId);
    }

    @Operation(summary = "Retrieve the Items")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Items retrieved successfully", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = DtoSearchResponse.class))})})
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public DtoSearchResponse getAll(@RequestParam(defaultValue = "100") int limit,
                                    @RequestParam(defaultValue = "0") int offset,
                                    @RequestParam(defaultValue = "id,asc") String[] sort) {
        Pageable pageable = PaginationSortingUtils.getPageable(limit, offset, sort);
        return service.findAll(pageable);
    }

    @Operation(summary = "Delete the Item by Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Item deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Item not found", content = @Content)})
    @DeleteMapping("/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long itemId) {
        service.deleteById(itemId);
    }
}
