package com.tutorial.logging.service;

import com.tutorial.logging.LogRequest;
import com.tutorial.logging.LogResponse;
import com.tutorial.logging.storage.MessageStorage;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class GrpcLoggingServiceTest {

    @Mock
    private MessageStorage messageStorage;

    @Mock
    private StreamObserver<LogResponse> responseObserver;

    private GrpcLoggingService grpcLoggingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        grpcLoggingService = new GrpcLoggingService(messageStorage);
    }

    @Test
    void log_shouldAddMessageToStorage_andReturnSuccessResponse() {
        // Arrange
        UUID id = UUID.randomUUID();
        String message = "Test message";
        LogRequest request = LogRequest.newBuilder()
                .setId(id.toString())
                .setMessage(message)
                .build();

        // Act
        grpcLoggingService.log(request, responseObserver);

        // Assert
        verify(messageStorage).addMessage(id, message);
        
        ArgumentCaptor<LogResponse> responseCaptor = ArgumentCaptor.forClass(LogResponse.class);
        verify(responseObserver).onNext(responseCaptor.capture());
        verify(responseObserver).onCompleted();
        
        LogResponse response = responseCaptor.getValue();
        assertTrue(response.getSuccess());
    }
}