package com.tutorial.facade.service;

import com.tutorial.facade.grpc.LogRequest;
import com.tutorial.facade.grpc.LogResponse;
import com.tutorial.facade.grpc.LoggingServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class GrpcLoggingService {

    private final DiscoveryClient discoveryClient;

    public GrpcLoggingService(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }

    public void log(LogRequest request) {
        invokeLoggingServiceGrpc(request);
    }


    public void invokeLoggingServiceGrpc(LogRequest request) {
        String serviceName = "logging-service";
        List<ServiceInstance> instances = discoveryClient.getInstances(serviceName);

        if (instances.isEmpty()) {
            throw new RuntimeException("No instances of logging-service found in Consul");
        }

        log.info("gRPC logging-service: {} instances found", instances.size());
        // Get the first available instance
        ServiceInstance instance = instances.get(new java.util.Random().nextInt(instances.size()));
        log.info("gRPC logging-service: {} instance: {}", serviceName, instance);

        String host = instance.getHost();
        int port = 9090;

        LoggingServiceGrpc.LoggingServiceBlockingStub stub = buildStub(host, port);
        LogResponse resp = stub.log(request);

        log.info("gRPC logging-service: {}:{}  ID: {}, Message: {}", host, port,
                request.getId(), request.getMessage());

        if (!resp.getSuccess()) {
            throw new RuntimeException("Помилка при логуванні");
        }
    }


    public LoggingServiceGrpc.LoggingServiceBlockingStub buildStub(String host, int port) {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress(host, port)
                .usePlaintext()
                .build();

        // Задаємо deadline 5 с. (аналогічно facade-service налаштуванню)
        return LoggingServiceGrpc.newBlockingStub(channel)
                .withDeadlineAfter(5, TimeUnit.SECONDS);
    }
}

