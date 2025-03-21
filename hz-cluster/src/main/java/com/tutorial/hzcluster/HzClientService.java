package com.tutorial.hzcluster;


import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@RestController
public class HzClientService {


    @GetMapping("/hz-test")
    public String hzTest() throws InterruptedException {

        ClientConfig clientConfig = new ClientConfig();
        clientConfig.setClusterName("lab-config");

        ExecutorService executorService = Executors.newFixedThreadPool(3);
        for (int i = 0; i < 3; i++) {
            executorService.submit(() -> {
                HazelcastInstance client = HazelcastClient.newHazelcastClient(clientConfig);
                IMap<String, Integer> map = client.getMap("yo counter");
                map.putIfAbsent("key", 0);
                for (int k = 0; k < 10_000; k++) {
                    var value = map.get("key");
                    value++;
                    map.put("key", value);
                }
                client.shutdown();
            });
        }
        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.MINUTES);
        return "Item added to Hazelcast map";
    }



    @GetMapping("/hz-test-pesim")
    public String hzTestPesim() throws InterruptedException {
        long startTime = System.currentTimeMillis();

        ClientConfig clientConfig = new ClientConfig();
        clientConfig.setClusterName("lab-config");

        ExecutorService executorService = Executors.newFixedThreadPool(3);
        for (int i = 0; i < 3; i++) {
            executorService.submit(() -> {
                sendMessagesToClient(clientConfig);
            });
        }
        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.MINUTES);
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        return "Item added to Hazelcast map pessimistic in  " + duration + " ms";
    }

    private static void sendMessagesToClient(ClientConfig clientConfig) {
        HazelcastInstance client = HazelcastClient.newHazelcastClient(clientConfig);
        IMap<String, Integer> map = client.getMap("yo counter");
        map.putIfAbsent("key", 0);
        for (int k = 0; k < 10_000; k++) {
            String key = "key";
            map.lock(key);
            try {


            var value = map.get(key);
            Thread.sleep( 10 );
            value++;
            map.put(key, value);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                map.unlock(key);
            }
        }

        client.shutdown();
    }

    @GetMapping("/hz-test-optimistic")
    public String hzTestOptimistic() throws InterruptedException {
        long startTime = System.currentTimeMillis();

        ClientConfig clientConfig = new ClientConfig();
        clientConfig.setClusterName("lab-config");

        ExecutorService executorService = Executors.newFixedThreadPool(3);
        for (int i = 0; i < 3; i++) {
            executorService.submit(() -> {
                sendMessagesToClientOptimistic(clientConfig);
            });
        }
        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.MINUTES);

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        return "Item added to Hazelcast optimistic map in " + duration + " ms";
    }

    private void sendMessagesToClientOptimistic(ClientConfig clientConfig) {
        HazelcastInstance client = HazelcastClient.newHazelcastClient(clientConfig);
        IMap<String, Integer> map = client.getMap("yo counter");
        map.putIfAbsent("key", 0);
        for (int k = 0; k < 10_000; k++) {
            String key = "key";
            while (true) {
                Integer oldValue = map.get(key);
                Integer newValue = oldValue + 1;
                if (map.replace(key, oldValue, newValue)) {
                    break;
                }
            }
        }
        client.shutdown();
    }
}