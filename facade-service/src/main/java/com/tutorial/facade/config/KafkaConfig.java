package com.tutorial.facade.config;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.Response;
import com.ecwid.consul.v1.kv.model.GetValue;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.yaml.snakeyaml.Yaml;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
@RefreshScope
public class KafkaConfig {

    @Value("${spring.cloud.consul.host:localhost}")
    private String consulHost;

    @Value("${spring.cloud.consul.port:8500}")
    private int consulPort;

    @Bean
    public Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();
        String value = fetchKafkaConfig("message-queue/kafka/bootstrap-servers");
        log.info("bootstrap-servers: {}", value);
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, value);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return props;
    }

    @Bean
    public ProducerFactory<String, String> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public NewTopic messagesTopic() {
        String messages = fetchKafkaConfig("message-queue/kafka/topic/messages");
        log.info("messages: {}", messages);
        return new NewTopic(messages, 1, (short) 1);
    }

    public String fetchKafkaConfig(String key) {
            ConsulClient consulClient = new ConsulClient(consulHost, consulPort);
            Response<GetValue> response = consulClient.getKVValue(key);

            if (response != null && response.getValue() != null && response.getValue().getDecodedValue() != null) {
                return response.getValue().getDecodedValue();
            } else {
                throw new RuntimeException("Could not fetch " + key);
            }
    }

}