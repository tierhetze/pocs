package com.example;

import flex.messaging.messages.CommandMessage;
import flex.messaging.messages.Message;
import flex.messaging.services.MessageService;
import flex.messaging.services.ServiceAdapter;

/**
 * 
 * @author peter
 *
 * Service redirects messages to clients
 *
 */
public class PushServiceAdapter extends ServiceAdapter{
    public Object invoke(Message newMessage) {
		MessageService msgService = (MessageService) getDestination().getService();
		msgService.pushMessageToClients(newMessage,false);
		return null;
	}
    @Override
	public Object manage(CommandMessage commandMessage) {
		// TODO Auto-generated method stub
		System.out.println("commandMessage="+commandMessage);
		switch (commandMessage.getOperation()) {
	        case CommandMessage.SUBSCRIBE_OPERATION:
	            System.out.println("SUBSCRIBE_OPERATION = " + commandMessage.getHeaders());
	            break;
	        case CommandMessage.UNSUBSCRIBE_OPERATION:
	            System.out.println("UNSUBSCRIBE_OPERATION = " + commandMessage.getHeaders());               
	            break;
        }
        return super.manage(commandMessage);
	}

	@Override
	public boolean handlesSubscriptions() {
		return super.handlesSubscriptions();
	}
}
