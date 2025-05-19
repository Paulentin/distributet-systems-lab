package com.tutorial.message.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class MessageStorage {

    private final Map<String, String> messages = new ConcurrentHashMap<>();

    public void storeMessage(String messageId, String message) {
        log.info("Storing message with ID {}: {}", messageId, message);
        messages.put(messageId, message);
    }

    public List<String> getAllMessages() {
        log.info("Retrieving all messages. Current count: {}", messages.size());
        return new ArrayList<>(messages.values());
    }

    public String getFormattedMessages() {
        List<String> messageList = getAllMessages();
        if (messageList.isEmpty()) {
            return "No messages available";
        }
        return String.join("\n", messageList);
    }

    public int getMessageCount() {
        return messages.size();
    }
}