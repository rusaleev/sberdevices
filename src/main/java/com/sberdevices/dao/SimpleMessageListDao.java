package com.sberdevices.dao;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintViolationException;

import org.hibernate.exception.DataException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import com.sberdevices.dto.MessageDto;
import com.sberdevices.dto.MessageListDto;
import com.sberdevices.model.Message;

/**
 * @author rusaleev
 * Dao that takes an MessageListDto object as an input
 * and saves the underlying messages to the database
 * ConstraintViolationException, DataException, DataIntegrityViolationException
 * are handled and logged. All other exceptions are delegated to caller methods 
 */
@Repository
public class SimpleMessageListDao implements MessageListDao {
	
	Logger logger = LoggerFactory.getLogger(SimpleMessageListDao.class);

	/**
	 * spring boot auto-generated crud repository class and bean
	 */
	@Autowired
	MessageRepository messageRepository;

	/* (non-Javadoc)
	 * @see com.sberdevices.dao.MessageListDao#save(com.sberdevices.dto.MessageListDto)
	 * implementation details:
	 * For performance purpose we first try save an entire list 
	 * with one operation (saveAll).
	 * 
	 * If it fails, then we re-try with each message in the list
	 * successful messages are saved, failed messages are logged
	 * This implemetation was chosen due to this requirement:
	 * 
	 * A message with a "messageId" not meeting constraint 
	 * requiremnts is commited, not saved and logged.
	 * 
	 * Thus, it is explicitly required to handle individual message
	 * rather then the entire message list.
	 * 
	 * For performance purpose it would have been better to discard
	 * the entire message list and avoid individual save operations at all
	 * This can be easily done by changing the outer catch block in this method 
	 */
	@Override
	public void save(MessageListDto messages) {
		
		List<Message> entities = new ArrayList<>();
		if (messages.getMessages()!=null && messages.getMessages().length>0){
			for (MessageDto message:messages.getMessages()){
				Message entity = new Message(message);
				entities.add(entity);
			}
		}
		if (entities.size()>0){
			try{
				messageRepository.saveAll(entities);
			} catch (ConstraintViolationException|DataException|DataIntegrityViolationException ex){
				for (Message entity:entities){
					try{
						messageRepository.save(entity);
					} catch (ConstraintViolationException|DataException|DataIntegrityViolationException ex1){
						logger.warn("Failed to save message: "+entity.getMessageId());//,ex1);
					}
				}
			}// catch (GenericJDBCException|JDBCConnectionException|QueryTimeoutException ex){
				
			//}
		}
	}

}
