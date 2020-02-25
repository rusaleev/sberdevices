package com.sberdevices.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @author rusaleev
 * transport layer-level class that represents a list of Message entities
 * in the format provided by the specification
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "messages" })
@JsonDeserialize(using = CustomMessageListDtoDeserializer.class)
public class MessageListDto {
	@JsonProperty("messages")
	private MessageDto[] messages;

	@JsonProperty("messages")
	public MessageDto[] getMessages() {
		return messages;
	}

	@JsonProperty("messages")
	public void setMessages(MessageDto[] messages) {
		this.messages = messages;
	}

	@JsonCreator
	public MessageListDto(@JsonProperty("messages") MessageDto[] messages) {
		super();
		this.messages = messages;
	}
}
