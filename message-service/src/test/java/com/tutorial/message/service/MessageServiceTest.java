package com.tutorial.message.service;

import com.tutorial.message.storage.MessageStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MessageServiceTest {

    private MockMvc mockMvc;
    private MessageService messageService;

    @Mock
    private MessageStorage messageStorage;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        messageService = new MessageService(messageStorage);
        mockMvc = MockMvcBuilders.standaloneSetup(messageService).build();
    }

    @Test
    void getMessages_shouldReturnNoMessagesAvailable() throws Exception {
        // Arrange
        when(messageStorage.getFormattedMessages()).thenReturn("No messages available");

        // Act & Assert
        mockMvc.perform(get("/messages"))
                .andExpect(status().isOk())
                .andExpect(content().string("No messages available"));
    }
}
