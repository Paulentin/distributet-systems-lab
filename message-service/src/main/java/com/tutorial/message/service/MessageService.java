package com.tutorial.message.service;

import com.tutorial.message.storage.MessageStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MessageService {

    private final MessageStorage messageStorage;

    @GetMapping("/messages")
    public String getMessages() {
        log.info("Received request to get all messages");
        return messageStorage.getFormattedMessages();
    }
}
