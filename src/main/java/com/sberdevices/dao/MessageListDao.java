package com.sberdevices.dao;

import com.sberdevices.dto.MessageListDto;

/**
 * @author rusaleev
 * DAO interface 
 * that takes an MessageListDto object as an input 
 * and saves the underlying messages into DB 
 */
public interface MessageListDao {

	/**
	 * that takes an MessageListDto object as an input 
	 * and saves the underlying messages into DB 
	 * 
	 * @param messages
	 */
	void save(MessageListDto messages);

}