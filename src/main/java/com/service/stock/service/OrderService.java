package com.service.stock.service;

import com.service.avro.model.AvroOrder;
import com.service.stock.converter.ConverterService;
import com.service.stock.exception.ServiceException;
import com.service.stock.model.Order;
import com.service.stock.model.OrderStatus;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {

    private final ItemService itemService;

    private final KafkaStockOrderProducerService kafkaStockOrderProducerService;

    private final ConverterService converter;

    public void processNewOrder(@NonNull AvroOrder avroOrder) {
        Order order = convertToEntity(avroOrder);

        try {
            itemService.createReservation(order);
            order.setStatus(OrderStatus.ACCEPT);
        } catch (ServiceException e) {
            order.setStatus(OrderStatus.REJECT);
            log.info("Error during reservation creation", e);
        }

        kafkaStockOrderProducerService.sendOrder(order);
    }

    public void processRollbackOrder(@NonNull AvroOrder avroOrder) {
        Order order = convertToEntity(avroOrder);
        try {
            itemService.rollbackReservation(order);
        } catch (ServiceException e) {
            log.error("Error during rollback reservation", e);
        }

    }

    public void processConfirmationOrder(@NonNull AvroOrder avroOrder) {
        Order order = convertToEntity(avroOrder);
        try {
            itemService.confirmReservation(order);
        } catch (ServiceException e) {
            log.error("Error during confirmation reservation", e);
        }
    }

    private Order convertToEntity(AvroOrder avroOrder) {
        return converter.convert(avroOrder, Order.class);
    }

}
