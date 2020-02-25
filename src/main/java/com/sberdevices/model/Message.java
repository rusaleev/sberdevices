package com.sberdevices.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.sberdevices.dto.MessageDto;

/**
 * @author entity class to be stored in the database
 *
 */
@Entity
@Table(name="message")
public class Message {
	
	@Id
	@Column(name="id")
	private Integer messageId;

	@Column(name="payload")
	private String payload;

	public Integer getMessageId() {
		return messageId;
	}

	public void setMessageId(Integer messageId) {
		this.messageId = messageId;
	}

	public String getPayload() {
		return payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}

	public Message(MessageDto dto) {
		super();
		this.messageId = dto.getMessageId();
		this.payload = dto.getPayload();
	}

	public Message() {
	}
	
	
}
