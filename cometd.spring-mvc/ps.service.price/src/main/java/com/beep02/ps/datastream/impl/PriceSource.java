package com.beep02.ps.datastream.impl;

import java.util.Random;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beep02.ps.datastream.api.StreamDataSource;
import com.beep02.ps.service.system.api.StreamDataSourceObserver;
import com.beep02.ps.service.user.impl.PricePushData;


/**
@author Vladimir Nabokov
@since 11/06/2013
 **/
public class PriceSource extends StreamDataSource {

	private ScheduledThreadPoolExecutor parserMockingMessagesCreatorExe = new ScheduledThreadPoolExecutor(1);
	private final static Logger logger = LoggerFactory.getLogger(PriceSource.class);
	
	@Override
    public void init(){
		//imitates start of socket work
		//we started send proces here
		ParserMessageRunner runnerTask = new ParserMessageRunner("price", "eurusd", 200);
		parserMockingMessagesCreatorExe.submit(runnerTask);
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			logger.error("caught while sleeps", e);
		}
		ParserMessageRunner runnerTask2 = new ParserMessageRunner("price", "usdjpy", 300);
		parserMockingMessagesCreatorExe.submit(runnerTask2);
	}
	
	class ParserMessageRunner implements Runnable{
		String pair;
		long pause;
		String type;
		Random m = new Random(System.currentTimeMillis());
		
		public ParserMessageRunner(String type, String pair, long pause){
			this.pair = pair;
			this.pause= pause;
			this.type = type;
		}

		@Override
		public void run() {
			
			try {
				Thread.sleep(pause);
			} catch (InterruptedException e) {
				logger.error("caught while sleeps", e);
			}
			
			for(String observer: observers.keySet()){
				
				StreamDataSourceObserver systemService = observers.get(observer);
				
				if(systemService!=null){
					
					PricePushData pd  = null;
					
					//for price
					if(type.equals("price")){
					   int bitt = m.nextInt(7);	
					   pd = new PricePushData("1.250"+bitt, "1.250"+(bitt+2), pair,"1.250"+(bitt+2),"1.250"+(bitt+2),"1.250"+(bitt+2),"1.250"+(bitt+2),"1.250"+(bitt+2),"1.250"+(bitt+2),"1.250"+(bitt+2),"1.250"+(bitt+2),"1.250"+(bitt+2),"1.250"+(bitt+2),"1.250"+(bitt+2),"1.250"+(bitt+2),"1.250"+(bitt+2),"1.250"+(bitt+2),"1.250"+(bitt+2),"1.250"+(bitt+2),"1.250"+(bitt+2),"1.250"+(bitt+2),"1.250"+(bitt+2),"1.250"+(bitt+2),"1.250"+(bitt+2),"1.250"+(bitt+2),"1.250"+(bitt+2),"1.250"+(bitt+2),"1.250"+(bitt+2),"1.250"+(bitt+2),"1.250"+(bitt+2),"1.250"+(bitt+2),"1.250"+(bitt+2),"1.250"+(bitt+2),"1.250"+(bitt+2),"1.250"+(bitt+2),"1.250"+(bitt+2),"1.250"+(bitt+2),"1.250"+(bitt+2));
					   pd.setType(type+"/"+pair);
					   pd.setSent(System.currentTimeMillis());
					   
					}else if(type.equals("deals")){
						System.out.println("deals are not supported");
					}else{
						System.out.println("something.. is not supported");
					}
					systemService.notify(pd);
				}
				
				
			}
			
			
			
			parserMockingMessagesCreatorExe.submit(this);
			
			
			
		}

		public String getPair() {
			return pair;
		}

		public void setPair(String pair) {
			this.pair = pair;
		}
		
		
		
	}
	

}
