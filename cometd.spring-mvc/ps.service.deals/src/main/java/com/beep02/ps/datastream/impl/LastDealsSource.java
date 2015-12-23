package com.beep02.ps.datastream.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beep02.ps.datastream.api.StreamDataSource;
import com.beep02.ps.service.system.api.StreamDataSourceObserver;
import com.beep02.ps.service.user.impl.DealPushData;
import com.beep02.ps.service.user.impl.LastDealsPushData;


/**
@author Vladimir Nabokov
@since 11/06/2013
@see {@link StreamDataSource}
 **/
public class LastDealsSource extends StreamDataSource {

	private ScheduledThreadPoolExecutor parserMockingMessagesCreatorExe = new ScheduledThreadPoolExecutor(1);
	private final static Logger logger = LoggerFactory.getLogger(LastDealsSource.class);
	
	@Override
	public void init(){
		//imitates start of socket work
		//we started send proces here
		ParserMessageRunner runnerTask = new ParserMessageRunner("deals", "eurusd", 200);
		parserMockingMessagesCreatorExe.submit(runnerTask);
		ParserMessageRunner runnerTask2 = new ParserMessageRunner("deals", "usdjpy", 300);
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
					
					LastDealsPushData pd  = null;
					
					//for price
					if(type.equals("deals")){
					  
					   List<DealPushData> list = new ArrayList<DealPushData>();
						
					   for(int i=0;i<50;i++){
						    DealPushData dpd = new DealPushData();
					   	    double newAmount = m.nextDouble();
					        dpd.setAmount(newAmount);
					        dpd.setPair(pair);
					        list.add(dpd);
					   }
					   
					   pd = new LastDealsPushData();
					   pd.setDeals(list);
					   pd.setPair(pair);
					   
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
