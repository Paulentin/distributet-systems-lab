package com.tutorial.message.kafka;

import com.tutorial.message.storage.MessageStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaConsumer {

    private final MessageStorage messageStorage;

    @KafkaListener(topics = "${spring.kafka.topic.messages:messages-topic}", 
                  groupId = "${spring.kafka.consumer.group-id:message-service-group}")
    public void consume(@Payload String message,
                       @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        log.info("Received message from Kafka: key={}, message={}", key, message);
        messageStorage.storeMessage(key, message);
    }
}