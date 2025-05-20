package com.tutorial.logging.config;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.Response;
import com.ecwid.consul.v1.kv.model.GetValue;
import com.hazelcast.config.Config;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Slf4j
@Configuration
@RefreshScope
public class HazelcastConfiguration {

    public static final String LOCAL_CLUSTER = "local-cluster";
    @Value("${spring.cloud.consul.host:localhost}")
    private String consulHost;

    @Value("${spring.cloud.consul.port:8500}")
    private int consulPort;


    @Bean
    public HazelcastInstance hazelcastInstance() {
        Config config = new Config();

        // Fetch cluster name from Consul
        String clusterName = fetchClusterNameFromConsul();
        log.info("Hazelcast Cluster Name:cluster-name {}", clusterName);
        config.setClusterName(clusterName);

        NetworkConfig networkConfig = config.getNetworkConfig();
        networkConfig.setPortAutoIncrement(true);
        return Hazelcast.newHazelcastInstance(config);
    }

    private String fetchClusterNameFromConsul() {
        try {
            ConsulClient consulClient = new ConsulClient(consulHost, consulPort);
            Response<GetValue> response = consulClient.getKVValue("hazelcast/cluster-name");

            if (response != null && response.getValue() != null && response.getValue().getDecodedValue() != null) {
                String consulClusterName = response.getValue().getDecodedValue();
                log.info("Successfully fetched cluster name from Consul: {}", consulClusterName);
                return consulClusterName;
            } else {
                log.warn("Could not fetch cluster name from Consul, using default: {}", LOCAL_CLUSTER);
                return LOCAL_CLUSTER;
            }
        } catch (Exception e) {
            return LOCAL_CLUSTER;
        }
    }
}
