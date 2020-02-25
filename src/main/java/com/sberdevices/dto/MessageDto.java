package com.sberdevices.dto;

import javax.validation.constraints.Min;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * @author rusaleev
 * transport-layer level object that represents a Message entity
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "messageId", "payload" })
public class MessageDto {
	@Min(0)
	@JsonProperty("messageId")
	private Integer messageId;

	@JsonProperty("payload")
	private String payload;

	@JsonProperty("messageId")
	public Integer getMessageId() {
		return messageId;
	}

	@JsonProperty("messageId")
	public void setMessageId(Integer messageId) {
		this.messageId = messageId;
	}

	@JsonProperty("payload")
	public String getPayload() {
		return payload;
	}

	@JsonProperty("payload")
	public void setPayload(String payload) {
		this.payload = payload;
	}

	@JsonCreator
	public MessageDto(@Min(0) @JsonProperty("messageId") Integer messageId, @JsonProperty("payload") String payload) {
		super();
		this.messageId = messageId;
		this.payload = payload;
	}
}
