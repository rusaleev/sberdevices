package com.sberdevices.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.sberdevices.dto.MessageListDto;

/**
 * @author rusaleev
 *
 * a publisher with 2 methods that are used for test purposes
 * one method publishes a properly serialized MessageListDto object
 * the other method publishes arbitrary strings
 */
@Service
public class MessagePublisher {
	
	@Value(value = "${kafka.topic}")
    private String topic;
	
	@Autowired
	private KafkaTemplate<String, MessageListDto> kafkaTemplate;
	
	@Autowired 
	private KafkaTemplate<String,String> kafkaTemplate2;
	 
	public void sendMessage(MessageListDto msg) {
	    kafkaTemplate.send(topic, msg);
	}
	
	public void sendString(String msg){
		kafkaTemplate2.send(topic,msg);
	}
}
