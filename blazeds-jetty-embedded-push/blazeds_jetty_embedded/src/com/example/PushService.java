package com.example;

import flex.messaging.MessageBroker;
import flex.messaging.messages.AsyncMessage;
import flex.messaging.util.UUIDUtils;

/**
 * 
 * @author peter
 *
 * message directed to the PushServiceAdapter here
 *
 */
public class PushService {
	
	private MessageBroker messageBroker;
	
	public void getBroker() {
		if (messageBroker == null) {
	        messageBroker = MessageBroker.getMessageBroker(null);
	    }
	}
	
	public void sendMessage(Long client, String type, Object obj){
		String topicName = "clients.client"+client+"."+type;//TODO: find faster approach for topic name composition
		AsyncMessage message = new AsyncMessage();
		message.setBody(obj);
		message.setDestination("PushDestination");
		message.setHeader(AsyncMessage.SUBTOPIC_HEADER_NAME, topicName);
		message.setMessageId(UUIDUtils.createUUID());
		message.setClientId(UUIDUtils.createUUID());
		message.setTimestamp(System.currentTimeMillis());
		getBroker();
		messageBroker.routeMessageToService(message,null);
	}
	
	
}
