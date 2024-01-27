package com.service.stock.service;

import com.service.avro.model.AvroOrder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderProcessingService {

    private final OrderService orderService;

    public void process(@NonNull AvroOrder avroOrder) {
        if (avroOrder.getStatus() == null) {
            log.warn("Order status is null for order: {}", avroOrder);
            return;
        }
        switch (avroOrder.getStatus()) {
            case NEW:
                orderService.processNewOrder(avroOrder);
                break;
            case ROLLBACK:
                orderService.processRollbackOrder(avroOrder);
                break;
            case CONFIRMATION:
                orderService.processConfirmationOrder(avroOrder);
                break;
            default:
                log.warn("Unknown order status: {}", avroOrder.getStatus());
        }
    }
}
