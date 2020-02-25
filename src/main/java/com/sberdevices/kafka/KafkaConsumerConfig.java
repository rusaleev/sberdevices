package com.sberdevices.kafka;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListenerConfigurer;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerEndpointRegistrar;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties.AckMode;
import org.springframework.kafka.listener.SeekToCurrentBatchErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer2;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;

import com.sberdevices.dto.MessageListDto;

/**
 * @author rusaleev
 * Kafka consumer config
 * 
 * Requirement to save data in batches is implemented on consumer level
 * I.e. data is read from Kafka in batches and then saved
 * Each Kafka message contents is saved individually
 * in order to have the ability to commit a part of a batch in case errors
 * occurred during batch handling
 */
@EnableKafka
@Configuration
public class KafkaConsumerConfig implements KafkaListenerConfigurer{
	
	@Value(value = "${kafka.bootstrapAddress}")
    private String bootstrapAddress;
	
	@Value(value = "${kafka.groupId}")
    private String groupId;
	
	/**
	 * min batch size. 
	 * should be set in settings by requirements
	 */
	@Value(value = "${kafka.fetch.min.bytes}")
	private String minBytes;
	
	/**
	 * max timeout between batch reads. 
	 * should be set in settings by requirements 
	 */
	@Value(value = "${kafka.fetch.max.wait.ms}")
	private String maxWait;
	
	/**
	 * >=2 due to requirements
	 */
	@Value(value = "${kafka.consumers}")
	private Integer consumers;
	
	@Override
    public void configureKafkaListeners(KafkaListenerEndpointRegistrar registrar) {
    }
		
	@Bean
	public ConsumerFactory<String, MessageListDto> consumerFactory() {
		Map<String, Object> props = new HashMap<>();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
		props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer2.class);
		props.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, minBytes);
		props.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, maxWait);
		JsonDeserializer<MessageListDto> deserializer = new JsonDeserializer<>();
		deserializer.addTrustedPackages("com.sberdevices.dto");
		Map<String, Object> deserializerProps = new HashMap<>();
		deserializerProps.put(JsonDeserializer.VALUE_DEFAULT_TYPE, MessageListDto.class);
		deserializer.configure(deserializerProps, false);
		return new DefaultKafkaConsumerFactory<>(props,new StringDeserializer(),deserializer);
	}

	/**
	 * Listener container factory
	 * 
	 * Here we set the ack mode to "batch"
	 * i.e. refetch messages that failed
	 * and provide error handler with infinite retries
	 * (as per requirements)
	 * 
	 * This error handler does not handle data format
	 * and other data errors, that are handled by deserializer
	 * and dao layers
	 * 
	 * This error handler is intended to handle database unavailability
	 * and other severe errors
	 * 
	 * @return
	 */
	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, MessageListDto> kafkaListenerContainerFactory() {

		ConcurrentKafkaListenerContainerFactory<String, MessageListDto> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(consumerFactory());
		factory.setConcurrency(consumers);
        factory.getContainerProperties().setPollTimeout(3000);
        factory.setBatchListener(true);
        factory.getContainerProperties().setAckMode(AckMode.BATCH);
        SeekToCurrentBatchErrorHandler errorHandler = new SeekToCurrentBatchErrorHandler();
        errorHandler.setBackOff(new FixedBackOff(FixedBackOff.DEFAULT_INTERVAL, FixedBackOff.UNLIMITED_ATTEMPTS));
        factory.setBatchErrorHandler(errorHandler);
		return factory;
	}
}
