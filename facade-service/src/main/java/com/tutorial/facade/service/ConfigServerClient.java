package com.tutorial.facade.service;

import com.tutorial.facade.dto.ServiceInfoDto;
import com.tutorial.facade.grpc.LogRequest;
import com.tutorial.facade.grpc.LogResponse;
import com.tutorial.facade.grpc.LoggingServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class ConfigServerClient {

    private final RestClient restClient;
    private final String configServerUrl;

    public ConfigServerClient(RestClient restClient,
                              @Value("${config.server.url}") String configServerUrl) {
        this.restClient = restClient;
        this.configServerUrl = configServerUrl;
    }

    public ServiceInfoDto getServiceInfo(String serviceName) {
        String url = UriComponentsBuilder.fromHttpUrl(configServerUrl)
                .pathSegment("services", serviceName)
                .build()
                .toString();
        try {
            return restClient.get()
                    .uri(url)
                    .retrieve()
                    .body(ServiceInfoDto.class);
        } catch (Exception ex) {
            log.error("Помилка при виклику config-server: {}", ex.getMessage());
            throw new RuntimeException("Не вдалося отримати IP/порти сервісу " + serviceName);
        }
    }

    public void invokeLoggingServiceGrpc(LogRequest request) {
        ServiceInfoDto info = getServiceInfo("logging-service");
        if (info == null || info.getGrpcPorts() == null || info.getGrpcPorts().isEmpty()) {
            throw new RuntimeException("Немає gRPC портів для logging-service");
        }
        List<Integer> ports = new ArrayList<>(info.getGrpcPorts());
        Collections.shuffle(ports);

        RuntimeException lastEx = null;
        for (Integer port : ports) {
            try {
                LoggingServiceGrpc.LoggingServiceBlockingStub stub =
                        buildStub(info.getHost(), port);
                LogResponse resp = stub.log(request);
                log.info("gRPC logging-service: {}  ID: {}, Message: {}", port,
                        request.getId(), request.getMessage());
                if (!resp.getSuccess()) {
                    throw new RuntimeException("Помилка при логуванні");
                }
                return; // успіх
            } catch (RuntimeException e) {
                lastEx = e;
            }
        }
        throw new RuntimeException("Не вдалося викликати logging-service (gRPC) на жодному з портів: " + lastEx.getMessage());
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