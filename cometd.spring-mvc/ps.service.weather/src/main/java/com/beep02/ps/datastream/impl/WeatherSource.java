package com.beep02.ps.datastream.impl;

import java.util.Random;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beep02.ps.datastream.api.StreamDataSource;
import com.beep02.ps.service.system.api.StreamDataSourceObserver;
import com.beep02.ps.service.user.impl.WeatherPushData;


/**
@author Vladimir Nabokov
@since 11/06/2013
 **/
public class WeatherSource extends StreamDataSource {

	private final static Logger logger = LoggerFactory.getLogger(WeatherSource.class);
	private ScheduledThreadPoolExecutor parserMockingMessagesCreatorExe = new ScheduledThreadPoolExecutor(1);
	
	
	@Override
    public void init(){
		//imitates start of socket work
		//we started send proces here
		ParserMessageRunner runnerTask = new ParserMessageRunner("weather", 1000);
		parserMockingMessagesCreatorExe.submit(runnerTask);
		
	}
	
	class ParserMessageRunner implements Runnable{
		
		long pause;
		String type;
		Random m = new Random(System.currentTimeMillis());
		
		public ParserMessageRunner(String type, long pause){
			this.pause= pause;
			this.type = type;
		}

		@Override
		public void run() {
			
			try {
				Thread.sleep(pause+ m.nextInt(500));
			} catch (InterruptedException e) {
				logger.error("caught while sleeps", e);
			}
			
			for(String observer: observers.keySet()){
				
				StreamDataSourceObserver systemService = observers.get(observer);
				
				String towns[] = new String[]{"moscow","zürich","london","tel-aviv"};
				String ts[] = new String[]{"-10 C","+46 F","-3 C","+25 C"};
				String airs[] = new String[]{"cloudy","snowy","rainy","buzzy"};
				
				
				if(systemService!=null){
					WeatherPushData pd  = new WeatherPushData();
					pd.setTown(towns[m.nextInt(4)]);
					pd.setTemperature(ts[m.nextInt(4)]);
					pd.setAir(airs[m.nextInt(4)]);
					pd.setType("weather");
					pd.setSent(System.currentTimeMillis());
					systemService.notify(pd);
				}
				
				
			}
			
			
			
			parserMockingMessagesCreatorExe.submit(this);
			
			
			
		}

		
		
		
	}
	

}
