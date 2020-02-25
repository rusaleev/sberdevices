package com.sberdevices;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.sberdevices.dto.MessageDto;
import com.sberdevices.dto.MessageListDto;
import com.sberdevices.kafka.MessagePublisher;

/**
 * @author rusaleev
 * This bean generates Kafka traffic internally for test purpose.
 * It depends on KafkaPublisher and KafkaProducerConfig beans to send data to Kafka
 */
@Component
public class ScheduledTask {
	
	/**
	 * use this to send test data to Kafka
	 */
	@Autowired
	MessagePublisher kafkaPublisher;
	
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

	/**
	 * "success" data generator. 
	 * produces well formed json messages 
	 * of correct type with correct headers 
	 * and sends them to publisher
	 */
	@Scheduled(fixedRate = 1000)
	public void sendMessagesToKafka(){
		Integer amount = new Long(Math.round(Math.random()*100)).intValue();
		MessageDto[] array = new MessageDto[amount];
		for (int i=0;i<amount;i++){
			Integer messageId = new Long(Math.round(Math.random()*10000)).intValue();
			MessageDto message = new MessageDto(messageId, "Generated at: "+dateFormat.format(new Date()));
			array[i] = message;
		}
		MessageListDto list = new MessageListDto(array);
		kafkaPublisher.sendMessage(list);
	}
	
	/**
	 * "fail" data generator.
	 * produces raw string data without headers
	 * error types:
	 * 1) invalid json string (to generate json parse error)
	 * 2) json string without required "messages" field (to generate mapping error, handled by deserializer)
	 * 3) json string with "messages" field not being an array (to generate mapping error, handled by deserializer)
	 * 4) json string with empty message list (to generate mapping error, handled by deserializer)
	 * 5) json string with messageId<0 (to generate constraint violation error, handled by dao service)
	 * 6) json string with repeating messageId -- tried to generate constraint violation db error,
	 *  but in fact this is handled by Hibernate 
	 *  (due to using "saveAll" method) 
	 *  it just re-writes records in case of identical private key
	 *  I have decided not to change this behavior as it was not explicitly specified     
	 */
	@Scheduled(fixedRate = 10000)
	public void sendInvalidMessagesToKafka(){
		kafkaPublisher.sendString("{invalid json syntax");
		kafkaPublisher.sendString("{\"test\":\"test\"}");
		kafkaPublisher.sendString("{\"messages\":\"test\"}");
		kafkaPublisher.sendString("{\"messages\":[]}");
		kafkaPublisher.sendString("{\"messages\":[{\"messageId\":-1,\"payload\":\"negative id\"}]}");
		kafkaPublisher.sendString("{\"messages\":[{\"messageId\":1,\"payload\":\"repeating id "+dateFormat.format(new Date())+"\"}]}");
	}
	
}
