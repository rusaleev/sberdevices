package com.sberdevices.kafka;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import com.sberdevices.dao.MessageListDao;
import com.sberdevices.dto.MessageListDto;

/**
 * @author rusaleev
 *
 * this listener gets a serialized Kafka message and
 * sends it to dto for further processing
 *
 */
@Service
public class MessageListener {
	
	@Autowired
	MessageListDao messageListDao;
	
	@KafkaListener(topics="helloworld",containerFactory = "kafkaListenerContainerFactory")
	public void listen(@Payload List<MessageListDto> messages,
			@Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String key,
	        @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
	        @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
	        @Header(KafkaHeaders.RECEIVED_TIMESTAMP) long ts){
		for (MessageListDto dto:messages){
			if (dto.getMessages()!=null&&dto.getMessages().length>0){
				messageListDao.save(dto);
			}
		}
	}
}
