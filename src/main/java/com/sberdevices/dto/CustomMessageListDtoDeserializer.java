package com.sberdevices.dto;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

/**
 * @author rusaleev
 * Implemented custom json deserializer 
 * to manually handle deserialization exceptions
 * in proper way.
 * 
 * This approach was chosen due to requirement 
 * to handle different errors types in different ways 
 * on Kafka consumer level:
 * data errors are commited
 * database unavailability errors are rolled back
 * 
 * Thus I decided to handle data errors on deserializer and dao level
 * so Consumer doesn't consider them errors
 * And the Consumer is responsible for unavailability errors
 * and handles them in generic way 
 */
public class CustomMessageListDtoDeserializer extends StdDeserializer<MessageListDto> {
	
	Logger logger = LoggerFactory.getLogger(CustomMessageListDtoDeserializer.class);

	public CustomMessageListDtoDeserializer(){
		this(null);
	}
	
	protected CustomMessageListDtoDeserializer(Class<?> vc) {
		super(vc);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -3139508162476907416L;

	@Override
	public MessageListDto deserialize(JsonParser jp, DeserializationContext context)
			throws IOException, JsonProcessingException {
		List<MessageDto> messageList = new ArrayList<MessageDto>();
		try{
			JsonNode node = jp.getCodec().readTree(jp);
			//logger.info("custom deserializer");
			//logger.info(node.toPrettyString());
			if (!node.has("messages")){
				logger.warn("No messages field in json object: "+node.toPrettyString());
			} else {
				JsonNode messages = (JsonNode)node.get("messages");
				if (messages.isArray()){
					Iterator<JsonNode> iterator = messages.elements();
					if (!iterator.hasNext()){
						logger.warn("Empty message list: "+ node.toPrettyString());
					}
					while (iterator.hasNext()){
						JsonNode messageNode = iterator.next();
						try{
							MessageDto dto = new ObjectMapper().readValue(messageNode.toPrettyString(), MessageDto.class);
							messageList.add(dto);
						} catch (JsonProcessingException ex){
							logger.warn("Failed to parse json string: "+ messageNode.toPrettyString(), ex);
						}
					}
				} else {
					logger.warn("Failed to parse json node as array: "+ messages.toPrettyString());
				}
			}
		} catch (JsonParseException ex){
			logger.warn("Failed to parse string as json: "+ jp.toString());//,ex);
		}
		return new MessageListDto(messageList.toArray(new MessageDto[0]));
	}

}
