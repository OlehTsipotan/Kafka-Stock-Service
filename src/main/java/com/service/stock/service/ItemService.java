package com.service.stock.service;

import com.service.stock.converter.ConverterService;
import com.service.stock.converter.ItemFromItemDtoUpdater;
import com.service.stock.dto.ItemDto;
import com.service.stock.dto.DtoSearchResponse;
import com.service.stock.entity.Item;
import com.service.stock.exception.EntityAlreadyExistsException;
import com.service.stock.exception.EntityNotFoundException;
import com.service.stock.model.Order;
import com.service.stock.model.Product;
import com.service.stock.repository.ItemRepository;
import com.service.stock.validation.ItemStockValidator;
import com.service.stock.validation.ItemValidator;
import com.service.stock.exception.ServiceException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemService {

    private final ItemValidator itemValidator;

    private final ItemRepository itemRepository;

    private final ItemStockValidator itemStockReservationValidator;

    private final ItemFromItemDtoUpdater itemFromItemDtoUpdater;

    private final ConverterService converter;

    @Transactional
    public Long create(@NonNull ItemDto itemDto) {
        Item item = convertToEntity(itemDto);
        itemValidator.validate(item);
        Item savedItem = execute(() -> {
            if (item.getId() != null && itemRepository.existsById(item.getId())) {
                throw new EntityAlreadyExistsException("Item with id = " + item.getId() + " already exists");
            }
            return itemRepository.save(item);
        });
        log.info("Created Item {}", savedItem);
        return savedItem.getId();
    }

    @Transactional
    public ItemDto update(@NonNull ItemDto itemDto, @NonNull Long id) {
        Item itemToUpdate = execute(() -> {
            Item item = itemRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("There is no Item to update with id = " + id));

            itemFromItemDtoUpdater.update(itemDto, item);
            itemValidator.validate(item);

            return itemRepository.save(item);
        });
        log.info("Updated Item {}", itemToUpdate);
        return convertToDto(itemToUpdate);
    }

    public Item findById(@NonNull Long id) {
        Item item = execute(() -> itemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("There is no Item with id = " + id)));
        log.debug("Retrieved Item by id = {}", id);
        return item;
    }

    public ItemDto findByIdAsDto(@NonNull Long id) {
        Item item = execute(() -> itemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("There is no Item with id = " + id)));
        log.debug("Retrieved Item by id = {}", id);
        return convertToDto(item);
    }

    @Transactional
    public void deleteById(@NonNull Long id) {
        execute(() -> {
            if (!itemRepository.existsById(id)) {
                throw new EntityNotFoundException("There is no Custoemr to delete with id = " + id);
            }
            itemRepository.deleteById(id);
        });
        log.info("Deleted Item id = {}", id);
    }

    public DtoSearchResponse findAll(@NonNull Pageable pageable) {
        List<ItemDto> carDTOList =
                execute(() -> itemRepository.findAll(pageable)).stream().map(this::convertToDto).toList();
        log.debug("Retrieved All {} Cars", carDTOList.size());
        return DtoSearchResponse.builder().offset(pageable.getOffset()).limit(pageable.getPageSize())
                .total(carDTOList.size()).sort(pageable.getSort().toString()).data(carDTOList).build();

    }

    @Transactional
    public void createReservation(@NonNull Order order) {
        Product product = order.getProduct();
        if (product == null) {
            throw new IllegalArgumentException("Product must be provided");
        }
        Item item = findById(product.getId());

        itemStockReservationValidator.validateReservationCreation(item, order);

        item.setStockReserved(item.getStockReserved() + product.getQuantity());
        item.setStockAvailable(item.getStockAvailable() - product.getQuantity());

        execute(() -> itemRepository.save(item));
        log.info("Item reservation created: {} for Order: {}", item, order);
    }

    @Transactional
    public void rollbackReservation(@NonNull Order order) {
        Product product = order.getProduct();
        if (product == null) {
            throw new IllegalArgumentException("Product must be provided");
        }
        Item item = findById(product.getId());

        itemStockReservationValidator.validateReservationRollback(item, order);

        item.setStockReserved(item.getStockReserved() - product.getQuantity());
        item.setStockAvailable(item.getStockAvailable() + product.getQuantity());

        execute(() -> itemRepository.save(item));
        log.info("Item reservation rollbacked: {} for Order: {}", item, order);
    }

    @Transactional
    public void confirmReservation(@NonNull Order order) {
        Product product = order.getProduct();
        if (product == null) {
            throw new IllegalArgumentException("Product must be provided");
        }
        Item item = findById(product.getId());

        itemStockReservationValidator.validateReservationConfirmation(item, order);

        item.setStockReserved(item.getStockReserved() - product.getQuantity());

        execute(() -> itemRepository.save(item));
        log.info("Item reservation confirmed: {} for Order: {}", item, order);
    }

    private ItemDto convertToDto(Item item) {
        return converter.convert(item, ItemDto.class);
    }

    private Item convertToEntity(ItemDto itemDto) {
        return converter.convert(itemDto, Item.class);
    }

    private <T> T execute(DaoSupplier<T> supplier) {
        try {
            return supplier.get();
        } catch (DataAccessException e) {
            throw new ServiceException("DAO operation failed", e);
        }
    }

    private void execute(DaoProcessor processor) {
        try {
            processor.process();
        } catch (DataAccessException e) {
            throw new ServiceException("DAO operation failed", e);
        }
    }

    @FunctionalInterface
    public interface DaoSupplier<T> {
        T get();
    }

    @FunctionalInterface
    public interface DaoProcessor {
        void process();
    }
}
