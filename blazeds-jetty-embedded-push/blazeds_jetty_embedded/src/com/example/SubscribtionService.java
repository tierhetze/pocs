package com.example;

/**
 * 
 * @author peter
 * Note this service is not singleton
 * It is remote service, created per client
 *
 */
public class SubscribtionService {

    

	public String subscribe(Long client , String type) {
        switch (type) {
			case "quotes":
				SpringServicesFactory.getQuoteService().startSubscribtion(client);
				return "OK";
			default:
				return "FAILURE";
		}
	}

    public String unsubscribe(Long client , String type) {
        switch (type) {
			case "quotes":
				SpringServicesFactory.getQuoteService().stopSubscribtion(client);
				return "OK";
			default:
				return "FAILURE";
		}
	}
	
	
	
}
