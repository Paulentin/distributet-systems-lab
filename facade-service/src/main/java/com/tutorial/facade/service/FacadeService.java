package com.tutorial.facade.service;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Service
@AllArgsConstructor
public class FacadeService {
    private final RestTemplate restTemplate;

    public String getAllMessages() {

        String loggingUrl = getUrl("logging-service");

        String messageServiceUrl = getUrl("message-service");

        String loggingMessages = restTemplate.getForObject(loggingUrl, String.class);
        String staticMessage = restTemplate.getForObject(messageServiceUrl, String.class);

        return "Logging: " + loggingMessages + "\n Message: " + staticMessage;
    }

    private static String getUrl(String serviceInfo) {
        return UriComponentsBuilder.newInstance()
                .scheme("http")
                .host(serviceInfo)
                .path("/messages")
                .build()
                .toString();
    }
}
