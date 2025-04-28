package com.tutorial.facade.controller;

import com.tutorial.facade.grpc.LogRequest;
import com.tutorial.facade.service.FacadeService;
import com.tutorial.facade.service.GrpcLoggingService;
import com.tutorial.facade.service.KafkaMessageService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@Slf4j
@RestController
@AllArgsConstructor
public class FacadeController {

    public static final String LOGGING_SERVICE = "logging-service";
    public static final String MESSAGE_SERVICE = "message-service";
    private final GrpcLoggingService grpcLoggingService;
    private final KafkaMessageService kafkaMessageService;
    private final FacadeService facadeService;

    @PostMapping("/message")
    @CircuitBreaker(name = MESSAGE_SERVICE, fallbackMethod = "handleMessageFallback")
    public ResponseEntity<UUID> handleMessage(@RequestBody String message) {
        UUID messageId = UUID.randomUUID();

        kafkaMessageService.sendMessage(messageId, message);

        LogRequest request = LogRequest.newBuilder()
                .setId(messageId.toString())
                .setMessage(message)
                .build();
        grpcLoggingService.log(request);
        return ResponseEntity.ok(messageId);
    }

    public ResponseEntity<UUID> handleMessageFallback(String message, Exception ex) {
        log.error("Circuit Breaker відкритий. Помилка при спробі логування повідомлення: {}", ex.getMessage());
        return ResponseEntity.ok(UUID.randomUUID());
    }

    @GetMapping("/messages")
    public String getAllMessages() {
        return facadeService.getAllMessages();
    }
}