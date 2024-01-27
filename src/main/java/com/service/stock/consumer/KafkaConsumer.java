package com.service.stock.consumer;

import com.service.avro.model.AvroOrder;
import com.service.stock.service.OrderProcessingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@EnableKafka
public class KafkaConsumer {

    private final OrderProcessingService orderProcessingService;

    @KafkaListener(id = "orders", topics = "orders", groupId = "stock")
    public void onOrderReceive(AvroOrder avroOrder) {
        log.info("Received from Kafka: {}", avroOrder);
        orderProcessingService.process(avroOrder);
    }

}
