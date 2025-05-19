package com.tutorial.facade.service;


import com.tutorial.facade.dto.ServiceInfoDto;
import io.github.resilience4j.core.ConfigurationNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class FacadeService {
    private final ConfigServerClient configServerClient;
    private final RestTemplate restTemplate;

    public String getAllMessages() {

            String loggingUrl = pickServiceRestUrl("logging-service", true);

            String messageServiceUrl = pickServiceRestUrl("message-service", true);

            String loggingMessages = restTemplate.getForObject(loggingUrl, String.class);
            String staticMessage = restTemplate.getForObject(messageServiceUrl, String.class);

            return loggingMessages + "\n" + staticMessage;
    }


    public String pickServiceRestUrl(String serviceName, boolean doShuffle) {
        ServiceInfoDto info = configServerClient.getServiceInfo(serviceName);
        if (CollectionUtils.isEmpty(info.getRestPorts())) {
            throw new ConfigurationNotFoundException("Немає REST портів для " + serviceName);
        }
        List<Integer> restPorts = new ArrayList<>(info.getRestPorts());
        if(doShuffle) {
            Collections.shuffle(restPorts);
        }
        return getUrl(restPorts, info);
    }

    private static String getUrl(List<Integer> ports, ServiceInfoDto serviceInfo) {
        int port = ports.get(0);
        return UriComponentsBuilder.newInstance()
                .scheme("http")
                .host(serviceInfo.getHost())
                .port(port)
                .path("/messages")
                .build()
                .toString();
    }
}
