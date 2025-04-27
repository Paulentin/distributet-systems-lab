package com.tutorial.configserver.controller;

import com.tutorial.configserver.config.ServicesConfig;
import com.tutorial.configserver.model.ServiceInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ConfigControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ServicesConfig servicesConfig;

    private ConfigController configController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        configController = new ConfigController(servicesConfig);
        mockMvc = MockMvcBuilders.standaloneSetup(configController).build();
    }

    @Test
    void getServiceInfo_shouldReturnServiceInfo_whenServiceExists() throws Exception {
        // Arrange
        String serviceName = "logging-service";
        ServiceInfo serviceInfo = new ServiceInfo();
        serviceInfo.setHost("localhost");
        serviceInfo.setGrpcPorts(List.of(9090));
        serviceInfo.setRestPorts(List.of(8080));

        Map<String, ServiceInfo> serviceMap = new HashMap<>();
        serviceMap.put(serviceName, serviceInfo);

        when(servicesConfig.getMap()).thenReturn(serviceMap);

        // Act & Assert
        mockMvc.perform(get("/services/{serviceName}", serviceName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.host").value("localhost"))
                .andExpect(jsonPath("$.grpcPorts[0]").value(9090))
                .andExpect(jsonPath("$.restPorts[0]").value(8080));
    }

    @Test
    void getServiceInfo_shouldReturnNull_whenServiceDoesNotExist() throws Exception {
        // Arrange
        String serviceName = "non-existent-service";
        Map<String, ServiceInfo> serviceMap = new HashMap<>();

        when(servicesConfig.getMap()).thenReturn(serviceMap);

        // Act & Assert
        mockMvc.perform(get("/services/{serviceName}", serviceName))
                .andExpect(status().isOk());
    }
}