package com.service.stock.service;

import com.service.avro.model.AvroOrder;
import com.service.stock.converter.ConverterService;
import com.service.stock.exception.ServiceException;
import com.service.stock.model.Order;
import com.service.stock.model.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class OrderServiceTest {

    private OrderService orderService;

    @Mock
    private ItemService itemService;

    @Mock
    private KafkaStockOrderProducerService kafkaStockOrderProducerService;

    @Mock
    private ConverterService converterService;

    @BeforeEach
    public void setUp(){
        this.orderService = new OrderService(itemService, kafkaStockOrderProducerService, converterService);
    }

    @ParameterizedTest
    @NullSource
    public void processNewOrder_whenAvroOrderIsNull_throwIllegalArgumentException(AvroOrder nullAvroOrder){
        assertThrows(IllegalArgumentException.class, () -> orderService.processNewOrder(nullAvroOrder));
    }

    @ParameterizedTest
    @NullSource
    public void processRollbackOrder_whenAvroOrderIsNull_throwIllegalArgumentException(AvroOrder nullAvroOrder){
        assertThrows(IllegalArgumentException.class, () -> orderService.processRollbackOrder(nullAvroOrder));
    }

    @ParameterizedTest
    @NullSource
    public void processConfirmationOrder_whenAvroOrderIsNull_throwIllegalArgumentException(AvroOrder nullAvroOrder){
        assertThrows(IllegalArgumentException.class, () -> orderService.processConfirmationOrder(nullAvroOrder));
    }

    @Test
    public void processNewOrder_whenItemServiceThrowsServiceException_setOrderStatusToRejectAndDoNotThrowAnyException(){
        AvroOrder avroOrder = new AvroOrder();
        Order order = new Order();
        order.setStatus(OrderStatus.NEW);

        when(converterService.convert(avroOrder, Order.class)).thenReturn(order);
        doThrow(ServiceException.class).when(itemService).createReservation(order);

        assertDoesNotThrow(() -> orderService.processNewOrder(avroOrder));
        assertEquals(order.getStatus(), OrderStatus.REJECT);

        verify(converterService).convert(avroOrder, Order.class);
        verify(itemService).createReservation(order);
        verify(kafkaStockOrderProducerService).sendOrder(order);
    }

    @Test
    public void processNewOrder_whenItemServiceDoesNotThrowServiceException_setOrderStatusToAccept(){
        AvroOrder avroOrder = new AvroOrder();
        Order order = new Order();
        order.setStatus(OrderStatus.NEW);

        when(converterService.convert(avroOrder, Order.class)).thenReturn(order);

        assertDoesNotThrow(() -> orderService.processNewOrder(avroOrder));
        assertEquals(order.getStatus(), OrderStatus.ACCEPT);

        verify(converterService).convert(avroOrder, Order.class);
        verify(itemService).createReservation(order);
        verify(kafkaStockOrderProducerService).sendOrder(order);
    }

    @Test
    public void processRollbackOrder_whenAllIsFine(){
        AvroOrder avroOrder = new AvroOrder();
        Order order = new Order();
        order.setStatus(OrderStatus.NEW);

        when(converterService.convert(avroOrder, Order.class)).thenReturn(order);

        assertDoesNotThrow(() -> orderService.processRollbackOrder(avroOrder));

        verify(converterService).convert(avroOrder, Order.class);
        verify(itemService).rollbackReservation(order);
        verifyNoInteractions(kafkaStockOrderProducerService);
    }

    @Test
    public void processRollbackOrder_whenItemServiceThrowServiceException_doesNotThrowsException(){
        AvroOrder avroOrder = new AvroOrder();
        Order order = new Order();
        order.setStatus(OrderStatus.NEW);

        when(converterService.convert(avroOrder, Order.class)).thenReturn(order);
        doThrow(ServiceException.class).when(itemService).rollbackReservation(order);

        assertDoesNotThrow(() -> orderService.processRollbackOrder(avroOrder));

        verify(converterService).convert(avroOrder, Order.class);
        verify(itemService).rollbackReservation(order);
        verifyNoInteractions(kafkaStockOrderProducerService);
    }

    @Test
    public void processConfirmationOrder_whenAllIsFine(){
        AvroOrder avroOrder = new AvroOrder();
        Order order = new Order();
        order.setStatus(OrderStatus.NEW);

        when(converterService.convert(avroOrder, Order.class)).thenReturn(order);
        doThrow(ServiceException.class).when(itemService).confirmReservation(order);

        assertDoesNotThrow(() -> orderService.processConfirmationOrder(avroOrder));

        verify(converterService).convert(avroOrder, Order.class);
        verify(itemService).confirmReservation(order);
        verifyNoInteractions(kafkaStockOrderProducerService);
    }


}
