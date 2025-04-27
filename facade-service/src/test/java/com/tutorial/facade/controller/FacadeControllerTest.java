package com.tutorial.facade.controller;

import com.tutorial.facade.grpc.LogRequest;
import com.tutorial.facade.service.FacadeService;
import com.tutorial.facade.service.GrpcLoggingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class FacadeControllerTest {

    private MockMvc mockMvc;

    @Mock
    private GrpcLoggingService grpcLoggingService;

    @Mock
    private FacadeService facadeService;

    private FacadeController facadeController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        facadeController = new FacadeController(grpcLoggingService, facadeService);
        mockMvc = MockMvcBuilders.standaloneSetup(facadeController).build();
    }

    @Test
    void handleMessage_shouldReturnUUID_whenMessageIsProcessed() throws Exception {
        // Arrange
        String message = "Test message";
        doNothing().when(grpcLoggingService).log(any(LogRequest.class));

        // Act & Assert
        mockMvc.perform(post("/message")
                .contentType(MediaType.TEXT_PLAIN)
                .content(message))
                .andExpect(status().isOk());
    }

    @Test
    void getAllMessages_shouldReturnMessages() throws Exception {
        // Arrange
        String expectedMessages = "Message 1\nMessage 2\nStatic message";
        when(facadeService.getAllMessages()).thenReturn(expectedMessages);

        // Act & Assert
        mockMvc.perform(get("/messages"))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedMessages));
    }

    @Test
    void handleMessageFallback_shouldReturnUUID_whenCircuitBreakerIsOpen() {
        // Arrange
        String message = "Test message";
        Exception exception = new RuntimeException("Service unavailable");

        // Act & Assert
        var response = facadeController.handleMessageFallback(message, exception);
        assertNotNull(response.getBody());
        assertTrue(response.getStatusCode().is2xxSuccessful());
    }
}
