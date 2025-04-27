package com.tutorial.message.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MessageServiceTest {

    private MockMvc mockMvc;
    private MessageService messageService;

    @BeforeEach
    void setUp() {
        messageService = new MessageService();
        mockMvc = MockMvcBuilders.standaloneSetup(messageService).build();
    }

    @Test
    void getMessages_shouldReturnNotImplementedYet() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/messages"))
                .andExpect(status().isOk())
                .andExpect(content().string("not implemented yet"));
    }
}