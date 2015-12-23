package com.example;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringServicesFactory {
	private static ApplicationContext context;
	private static QuoteService quoteService;
	private static UsersRegistry usersRegistry;
	
	
	public static QuoteService getQuoteService(){
		return quoteService;
	}
	
	
	public static UsersRegistry getUsersRegistry() {
		return usersRegistry;
	}


	public static void init(){
		context  = new ClassPathXmlApplicationContext("resources/application-context.xml");
		quoteService = (QuoteService)context.getBean("quoteService");
		usersRegistry= (UsersRegistry)context.getBean("usersRegistry");
	}
	
	
}
