package com.sberdevices.kafka;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

/**
 * @author rusaleev
 * simple Kafka config where we set the topics we need
 */
@Configuration
public class KafkaConfig {
	@Value(value = "${kafka.bootstrapAddress}")
    private String bootstrapAddress;
	
	@Value(value = "${kafka.topic}")
    private String topic;
	
	/**
	 * >=2 due to requirements
	 */
	@Value(value = "${kafka.partitions}")
	private int partitions;
 
    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<String, Object>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        return new KafkaAdmin(configs);
    }
     
    @Bean
    public NewTopic topic1() {
         return new NewTopic(topic, partitions, (short) 1);
    }
}
