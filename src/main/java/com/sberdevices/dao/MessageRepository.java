package com.sberdevices.dao;

import org.springframework.data.repository.CrudRepository;

import com.sberdevices.model.Message;

/**
 * @author rusaleev
 * Spring boot will generate 
 * an actual crud repository 
 * for Message entity out of this interface
 */
public interface MessageRepository extends CrudRepository<Message, Integer> {

}
