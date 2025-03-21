package com.tutorial.hzcluster.config;

import com.hazelcast.collection.IQueue;
import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;


@Configuration
public class HazelcastConfig {

  @Bean
  public List<HazelcastInstance> hazelcastInstances() throws InterruptedException {
    List<HazelcastInstance> instances = new ArrayList<>();
    Config helloWorldConfig = new Config();
//    helloWorldConfig.getNetworkConfig().setPublicAddress("host.docker.internal:5701");
    helloWorldConfig.setClusterName("lab-config");
    for (int i = 0; i < 3; i++) {
      instances.add(Hazelcast.newHazelcastInstance(helloWorldConfig));
    }

    HazelcastInstance hz = Hazelcast.newHazelcastInstance();
    IQueue<Integer> queue = hz.getQueue( "queue" );


    for ( int k = 1; k < 100; k++ ) {
      queue.put( k );
      System.out.println( "Producing: " + k );
      Thread.sleep(1000);
    }
    queue.put( -1 );
    System.out.println( "Producer Finished!" );


    instances.add(hz);
    return instances;
  }
}