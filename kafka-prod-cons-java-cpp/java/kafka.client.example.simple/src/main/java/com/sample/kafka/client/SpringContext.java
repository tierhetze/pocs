package com.sample.kafka.client;

import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class SpringContext {
    private static ApplicationContext context =  new ClassPathXmlApplicationContext("applicationContext.xml");
	private static AtomicBoolean stop = new AtomicBoolean(false);
	public static void load(){
		while(!stop.get()){
			synchronized (SpringContext.class) {
				try {
					SpringContext.class.wait();
				} catch (InterruptedException e) {
					 ;
				}
			}
		}
	}
	
	public static void stop(){
		SpringContext.stop.set(true);
		synchronized (SpringContext.class) {
			SpringContext.class.notify();
		}
	}
}
