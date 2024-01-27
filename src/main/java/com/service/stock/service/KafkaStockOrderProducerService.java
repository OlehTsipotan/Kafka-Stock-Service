package com.service.stock.service;

import com.service.avro.model.AvroOrder;
import com.service.stock.converter.OrderToAvroOrderConverter;
import com.service.stock.exception.ServiceException;
import com.service.stock.model.Order;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaStockOrderProducerService {

    private final KafkaTemplate<String, AvroOrder> template;

    private final OrderToAvroOrderConverter converter;

    @Value("${kafka.payment-orders.topic}")
    private String topic;

    public void sendOrder(@NonNull Order order) {
        AvroOrder avroOrder = converter.convert(order);
        try {
            this.template.send(topic, String.valueOf(avroOrder.getId()), avroOrder);
            log.info("Produced to Kafka: {}", avroOrder);
        } catch (KafkaException e) {
            throw new ServiceException("Error sending order to Kafka", e);
        }
    }


}
