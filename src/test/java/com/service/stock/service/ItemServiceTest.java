package com.service.stock.service;

import com.service.stock.converter.ConverterService;
import com.service.stock.converter.ItemFromItemDtoUpdater;
import com.service.stock.dto.ItemDto;
import com.service.stock.entity.Item;
import com.service.stock.exception.*;
import com.service.stock.model.Order;
import com.service.stock.model.Product;
import com.service.stock.repository.ItemRepository;
import com.service.stock.validation.ItemStockValidator;
import com.service.stock.validation.ItemValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.Mock;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.query.BadJpqlGrammarException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class ItemServiceTest {

    private ItemService itemService;

    @Mock
    private ItemValidator itemValidator;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ItemStockValidator itemStockValidator;

    @Mock
    private ItemFromItemDtoUpdater itemFromItemDtoUpdater;

    @Mock
    private ConverterService converter;

    @BeforeEach
    public void setUp() {
        this.itemService = new ItemService(itemValidator, itemRepository, itemStockValidator,
                itemFromItemDtoUpdater, converter);
    }

    @ParameterizedTest
    @NullSource
    public void create_whenItemDtoIsNull_throwIllegalArgumentException(ItemDto nullItemDto) {
        assertThrows(IllegalArgumentException.class, () -> itemService.create(nullItemDto));
    }

    @Test
    public void create_whenItemRepositoryThrowsExceptionExtendsDataAccessException_throwServiceException() {
        doThrow(BadJpqlGrammarException.class).when(itemRepository).existsById(any());

        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        Item item = new Item();
        item.setId(1L);

        when(converter.convert(itemDto, Item.class)).thenReturn(item);

        assertThrows(ServiceException.class, () -> itemService.create(itemDto));

        verify(itemValidator).validate(any());
        verify(itemRepository).existsById(any());
        verifyNoMoreInteractions(itemRepository);
    }

    @Test
    public void create_whenValidatorThrowsEntityValidationException_throwEntityValidationException() {
        doThrow(EntityValidationException.class).when(itemValidator).validate(any());

        assertThrows(EntityValidationException.class, () -> itemService.create(new ItemDto()));

        verify(itemValidator).validate(any());
    }

    @Test
    public void create_whenItemAlreadyExists_throwEntityAlreadyExistsException() {
        when(itemRepository.existsById(any())).thenReturn(true);

        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        Item item = new Item();
        item.setId(1L);

        when(converter.convert(itemDto, Item.class)).thenReturn(item);

        assertThrows(EntityAlreadyExistsException.class, () -> itemService.create(itemDto));

        verify(itemValidator).validate(any());
    }

    @Test
    public void create_success() {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        Item item = new Item();
        item.setId(1L);

        when(converter.convert(itemDto, Item.class)).thenReturn(item);
        when(itemRepository.save(any())).thenReturn(item);
        when(itemRepository.existsById(any())).thenReturn(false);

        assertEquals(itemDto.getId(), itemService.create(itemDto));

        verify(itemRepository).save(item);
        verify(itemRepository).existsById(any());
        verifyNoMoreInteractions(itemRepository);

        verify(itemValidator).validate(item);
        verifyNoMoreInteractions(itemStockValidator);

        verify(converter).convert(itemDto, Item.class);
        verifyNoMoreInteractions(converter);
    }

    @ParameterizedTest
    @NullSource
    public void update_whenItemDtoIsNull_throwIllegalArgumentException(ItemDto ItemDto) {
        assertThrows(IllegalArgumentException.class, () -> itemService.update(ItemDto, 1L));

        verifyNoInteractions(itemRepository);
        verifyNoInteractions(itemStockValidator);
    }

    @Test
    public void update_whenRepositoryThrowsExceptionExtendsDataAccessException_throwServiceException() {
        doThrow(BadJpqlGrammarException.class).when(itemRepository).findById(any());

        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);

        assertThrows(ServiceException.class, () -> itemService.update(itemDto, 1L));

        verify(itemRepository).findById(any());
        verifyNoMoreInteractions(itemRepository);
    }

    @Test
    public void update_whenValidatorThrowsEntityValidationException_throwValidationException() {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        Item item = new Item();
        item.setId(1L);

        when(itemRepository.findById(any())).thenReturn(Optional.of(item));

        doThrow(EntityValidationException.class).when(itemValidator).validate(any());

        assertThrows(EntityValidationException.class, () -> itemService.update(itemDto, 1L));

        verify(itemValidator).validate(any());

        verify(itemRepository).findById(any());
        verifyNoMoreInteractions(itemRepository);

        verify(itemFromItemDtoUpdater).update(any(), any());
        verifyNoMoreInteractions(itemFromItemDtoUpdater);

        verifyNoInteractions(converter);
    }

    @Test
    public void update_whenItemDoesNotExists_throwEntityNotFoundException() {
        when(itemRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemService.update(new ItemDto(), 1L));

        verify(itemRepository).findById(any());
        verifyNoMoreInteractions(itemRepository);

        verifyNoInteractions(itemFromItemDtoUpdater);
        verifyNoInteractions(converter);
        verifyNoInteractions(itemStockValidator);
    }

    @Test
    public void update_success() {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        Item item = new Item();
        item.setId(1L);

        when(itemRepository.findById(any())).thenReturn(Optional.ofNullable(item));
        when(itemRepository.save(any())).thenReturn(item);
        when(converter.convert(item, ItemDto.class)).thenReturn(itemDto);

        assertEquals(itemDto, itemService.update(itemDto, 1L));

        verify(itemRepository).findById(any());
        verify(itemRepository).save(item);
        verifyNoMoreInteractions(itemRepository);

        verify(itemValidator).validate(item);
        verifyNoMoreInteractions(itemStockValidator);

        verify(itemFromItemDtoUpdater).update(itemDto, item);
        verifyNoMoreInteractions(itemFromItemDtoUpdater);

        verify(converter).convert(item, ItemDto.class);
        verifyNoMoreInteractions(converter);
    }

    @Test
    public void deleteById_whenItemRepositoryThrowsExceptionExtendsDataAccessException_throwServiceException() {
        doThrow(BadJpqlGrammarException.class).when(itemRepository).deleteById(any());

        assertThrows(ServiceException.class, () -> itemService.deleteById(1L));

        verify(itemRepository).existsById(any());
    }

    @Test
    public void deleteById_whenItemDoesNotExists_throwEntityDoesNotExistsException() {
        when(itemRepository.existsById(any())).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> itemService.deleteById(1L));

        verify(itemRepository).existsById(any());
    }

    @ParameterizedTest
    @NullSource
    public void deleteById_whenIdIsNull_throwIllegalArgumentException(Long nullId) {
        assertThrows(IllegalArgumentException.class, () -> itemService.deleteById(nullId));

        verifyNoInteractions(itemRepository);
    }

    @Test
    public void deleteById_success() {
        when(itemRepository.existsById(any())).thenReturn(true);

        itemService.deleteById(1L);

        verify(itemRepository).deleteById(any());
    }

    @ParameterizedTest
    @NullSource
    public void findById_whenIdIsNull_throwIllegalArgumentException(Long nullId) {
        assertThrows(IllegalArgumentException.class, () -> itemService.findById(nullId));
    }

    @Test
    public void findById_whenItemRepositoryThrowsExceptionExtendsDataAccessException_throwServiceException() {
        doThrow(BadJpqlGrammarException.class).when(itemRepository).findById(any());

        assertThrows(ServiceException.class, () -> itemService.findById(1L));

        verify(itemRepository).findById(any());
    }

    @Test
    public void findById_success() {
        Item item = new Item();
        item.setId(1L);

        when(itemRepository.findById(any())).thenReturn(Optional.ofNullable(item));

        assertEquals(item, itemService.findById(1L));

        verify(itemRepository).findById(any());
        verifyNoMoreInteractions(itemRepository);

        verifyNoMoreInteractions(converter);
    }

    @Test
    public void findAll_whenRepositoryThrowsExceptionExtendsDataAccessException_throwServiceException() {
        when(itemRepository.findAll(any(Pageable.class))).thenThrow(BadJpqlGrammarException.class);

        assertThrows(ServiceException.class, () -> itemService.findAll(Pageable.unpaged()));

        verify(itemRepository).findAll(any(Pageable.class));
    }

    @ParameterizedTest
    @NullSource
    public void findAll_whenPageableIsNull_throwIllegalArgumentException(Pageable nullPageable) {
        assertThrows(IllegalArgumentException.class, () -> itemService.findAll(nullPageable));

        verifyNoInteractions(itemRepository);
    }

    @Test
    public void findAll_success() {
        when(itemRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of()));

        Pageable pageable = mock(Pageable.class);
        when(pageable.getSort()).thenReturn(mock(org.springframework.data.domain.Sort.class));

        assertDoesNotThrow(() -> itemService.findAll(pageable));

        verify(itemRepository).findAll(any(Pageable.class));
    }

    @ParameterizedTest
    @NullSource
    public void findByIdAsDto_whenIdIsNull_throwIllegalArgumentException(Long nullId) {
        assertThrows(IllegalArgumentException.class, () -> itemService.findByIdAsDto(nullId));
    }

    @Test
    public void findByIdAsDto_whenItemRepositoryThrowsExceptionExtendsDataAccessException_throwServiceException() {
        doThrow(BadJpqlGrammarException.class).when(itemRepository).findById(any());

        assertThrows(ServiceException.class, () -> itemService.findByIdAsDto(1L));

        verify(itemRepository).findById(any());
    }

    @Test
    public void findByIdAsDto_success() {
        Item item = new Item();
        item.setId(1L);
        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);

        when(itemRepository.findById(any())).thenReturn(Optional.ofNullable(item));
        when(converter.convert(item, ItemDto.class)).thenReturn(itemDto);

        assertEquals(itemDto, itemService.findByIdAsDto(1L));

        verify(converter).convert(item, ItemDto.class);

        verify(itemRepository).findById(any());
        verifyNoMoreInteractions(itemRepository);

        verifyNoMoreInteractions(converter);
    }

    @ParameterizedTest
    @NullSource
    public void createReservation_whenOrderIsNull_throwIllegalArgumentException(Order nullOrder) {
        assertThrows(IllegalArgumentException.class, () -> itemService.createReservation(nullOrder));
    }

    @Test
    public void createReservation_whenItemReservationValidatorThrowsValidationException_throwValidationException() {
        Product product = new Product();
        product.setId(1L);
        product.setQuantity(1);
        Order order = new Order();
        order.setProduct(product);

        Item item = new Item();
        item.setStockReserved(1L);
        item.setStockAvailable(1L);
        item.setId(1L);

        when(itemRepository.findById(any())).thenReturn(Optional.of(item));

        doThrow(ValidationException.class).when(itemStockValidator).validateReservationCreation(item, order);

        assertThrows(ValidationException.class, () -> itemService.createReservation(order));

        verify(itemStockValidator).validateReservationCreation(item, order);
    }

    @Test
    public void createReservation_whenItemDoesNotExists_throwEntityNotFoundException() {
        Product product = new Product();
        product.setId(1L);
        product.setQuantity(1);
        Order order = new Order();
        order.setProduct(product);

        Item item = new Item();
        item.setStockReserved(1L);
        item.setStockAvailable(1L);
        item.setId(1L);

        when(itemRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemService.createReservation(order));

        verify(itemRepository).findById(any());
    }

    @Test
    public void createReservation_whenItemRepositoryThrowsExceptionExtendsDataAccessException_throwServiceException() {
        Product product = new Product();
        product.setId(1L);
        product.setQuantity(1);
        Order order = new Order();
        order.setProduct(product);

        Item item = new Item();
        item.setStockReserved(1L);
        item.setStockAvailable(1L);
        item.setId(1L);

        when(itemRepository.save(any())).thenThrow(BadJpqlGrammarException.class);

        assertThrows(ServiceException.class, () -> itemService.createReservation(order));

        verify(itemRepository).findById(any());
    }

    @Test
    public void createReservation_success() {
        Order order = new Order();
        order.setCustomerId(1L);

        Product product = new Product();
        product.setId(1L);
        product.setQuantity(1);
        product.setPrice(100L);

        order.setProduct(product);

        Item item = new Item();
        item.setId(1L);
        item.setStockAvailable(1000L);
        item.setStockReserved(0L);

        when(itemRepository.findById(any())).thenReturn(Optional.of(item));

        itemService.createReservation(order);

        verify(itemRepository).findById(any());
        verify(itemRepository).save(item);
        verifyNoMoreInteractions(itemRepository);

        verify(itemStockValidator).validateReservationCreation(item, order);
        verifyNoMoreInteractions(itemStockValidator);
    }

    @ParameterizedTest
    @NullSource
    public void rollbackReservation_whenOrderIsNull_throwIllegalArgumentException(Order nullOrder) {
        assertThrows(IllegalArgumentException.class, () -> itemService.rollbackReservation(nullOrder));
    }

    @Test
    public void rollbackReservation_whenItemReservationValidatorThrowsValidationException_throwValidationException() {
        Product product = new Product();
        product.setId(1L);
        product.setQuantity(1);
        Order order = new Order();
        order.setProduct(product);

        Item item = new Item();
        item.setStockReserved(1L);
        item.setStockAvailable(1L);
        item.setId(1L);

        when(itemRepository.findById(any())).thenReturn(Optional.of(item));

        doThrow(ValidationException.class).when(itemStockValidator).validateReservationRollback(item, order);

        assertThrows(ValidationException.class, () -> itemService.rollbackReservation(order));

        verify(itemStockValidator).validateReservationRollback(item, order);
    }

    @Test
    public void rollbackReservation_whenItemDoesNotExists_throwEntityNotFoundException() {
        Product product = new Product();
        product.setId(1L);
        product.setQuantity(1);
        Order order = new Order();
        order.setProduct(product);

        Item item = new Item();
        item.setStockReserved(1L);
        item.setStockAvailable(1L);
        item.setId(1L);

        when(itemRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemService.rollbackReservation(order));

        verify(itemRepository).findById(any());
    }

    @Test
    public void rollbackReservation_whenItemRepositoryThrowsExceptionExtendsDataAccessException_throwServiceException() {
        Product product = new Product();
        product.setId(1L);
        product.setQuantity(1);
        Order order = new Order();
        order.setProduct(product);

        Item item = new Item();
        item.setStockReserved(1L);
        item.setStockAvailable(1L);
        item.setId(1L);

        when(itemRepository.save(any())).thenThrow(BadJpqlGrammarException.class);

        assertThrows(ServiceException.class, () -> itemService.rollbackReservation(order));

        verify(itemRepository).findById(any());
    }

    @Test
    public void rollbackReservation_success() {
        Product product = new Product();
        product.setId(1L);
        product.setQuantity(1);
        Order order = new Order();
        order.setProduct(product);

        Item item = new Item();
        item.setStockReserved(1L);
        item.setStockAvailable(1L);
        item.setId(1L);

        when(itemRepository.findById(any())).thenReturn(Optional.of(item));

        itemService.rollbackReservation(order);

        verify(itemRepository).findById(any());
        verify(itemRepository).save(item);
        verifyNoMoreInteractions(itemRepository);

        verify(itemStockValidator).validateReservationRollback(item, order);
        verifyNoMoreInteractions(itemStockValidator);
    }

    @ParameterizedTest
    @NullSource
    public void confirmReservation_whenOrderIsNull_throwIllegalArgumentException(Order nullOrder) {
        assertThrows(IllegalArgumentException.class, () -> itemService.confirmReservation(nullOrder));
    }

    @Test
    public void confirmReservation_whenItemReservationValidatorThrowsValidationException_throwValidationException() {
        Product product = new Product();
        product.setId(1L);
        product.setQuantity(1);
        Order order = new Order();
        order.setProduct(product);

        Item item = new Item();
        item.setStockReserved(1L);
        item.setStockAvailable(1L);
        item.setId(1L);

        when(itemRepository.findById(any())).thenReturn(Optional.of(item));

        doThrow(ValidationException.class).when(itemStockValidator).validateReservationConfirmation(item, order);

        assertThrows(ValidationException.class, () -> itemService.confirmReservation(order));

        verify(itemStockValidator).validateReservationConfirmation(item, order);
    }

    @Test
    public void confirmReservation_whenItemDoesNotExists_throwEntityNotFoundException() {
        Product product = new Product();
        product.setId(1L);
        product.setQuantity(1);
        Order order = new Order();
        order.setProduct(product);

        Item item = new Item();
        item.setStockReserved(1L);
        item.setStockAvailable(1L);
        item.setId(1L);

        when(itemRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemService.confirmReservation(order));

        verify(itemRepository).findById(any());
    }

    @Test
    public void confirmReservation_whenItemRepositoryThrowsExceptionExtendsDataAccessException_throwServiceException() {
        Product product = new Product();
        product.setId(1L);
        product.setQuantity(1);
        Order order = new Order();
        order.setProduct(product);

        Item item = new Item();
        item.setStockReserved(1L);
        item.setStockAvailable(1L);
        item.setId(1L);

        when(itemRepository.save(any())).thenThrow(BadJpqlGrammarException.class);

        assertThrows(ServiceException.class, () -> itemService.confirmReservation(order));

        verify(itemRepository).findById(any());
    }

    @Test
    public void confirmReservation_success() {
        Product product = new Product();
        product.setId(1L);
        product.setQuantity(1);
        Order order = new Order();
        order.setProduct(product);

        Item item = new Item();
        item.setStockReserved(1L);
        item.setStockAvailable(1L);
        item.setId(1L);

        when(itemRepository.findById(any())).thenReturn(Optional.of(item));

        itemService.confirmReservation(order);

        verify(itemRepository).findById(any());
        verify(itemRepository).save(item);
        verifyNoMoreInteractions(itemRepository);

        verify(itemStockValidator).validateReservationConfirmation(item, order);
        verifyNoMoreInteractions(itemStockValidator);
    }
}

