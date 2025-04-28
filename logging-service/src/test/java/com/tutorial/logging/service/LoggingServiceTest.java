package com.tutorial.logging.service;

import com.tutorial.logging.storage.MessageStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class LoggingServiceTest {

    @Mock
    private MessageStorage messageStorage;

    private LoggingService loggingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        loggingService = new LoggingService(messageStorage);
    }

    @Test
    void getAllMessages_shouldReturnMessagesFromStorage() {
        // Arrange
        String expectedMessages = "Message 1\nMessage 2";
        when(messageStorage.getAllMessages()).thenReturn(expectedMessages);

        // Act
        String actualMessages = loggingService.getAllMessages();

        // Assert
        assertEquals(expectedMessages, actualMessages);
    }
}