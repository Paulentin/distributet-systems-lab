package com.tutorial.facade.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaMessageService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${spring.kafka.topic.messages:messages-topic}")
    private String messagesTopic;

    public void sendMessage(UUID messageId, String message) {
        log.info("Sending message to Kafka topic {}: {}", messagesTopic, message);
        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(messagesTopic, messageId.toString(), message);
        
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Message sent successfully to topic {}: {}", messagesTopic, message);
            } else {
                log.error("Failed to send message to topic {}: {}", messagesTopic, ex.getMessage());
            }
        });
    }
}