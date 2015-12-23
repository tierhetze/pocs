package com.beep02.ps.datastream.api;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beep02.ps.service.system.api.StreamDataSourceObserver;
import com.beep02.ps.service.system.api.SystemService;


/**
StreamDataSource able to send message to appropriate observers, 
which registered to get the processed data from the StreamDataSource.
 
{@link SystemService} able to register itself as an observer to such parsers

@author Vladimir Nabokov
@since 11/06/2013
**/
public abstract class StreamDataSource implements ObservableStreamDataSource{
	
	private final static Logger logger = LoggerFactory.getLogger(StreamDataSource.class);
	/**
	 * Observers map:
	 * 
	 * key is service name, value - is a service object.
	 *  
	 * the service listens to stream data
	 * 
	 * Map gives a theoretical possibility to register more, than 1 service to observe the StreamDataSource.
	 * However in most of cases it is better(simpler) to have 1 StreamDataSource per unique system service
	 */
	protected final Map<String, StreamDataSourceObserver> observers = new ConcurrentHashMap<String, StreamDataSourceObserver>();
	/**
	 * 
	 * @param systemService - registered observer, StreamDataSource is responsible to notify all it's observers
	 */
	@Override
	public void addObserver(StreamDataSourceObserver observer) {
		synchronized (observers) {
			/**
			 * class name of the observer is it's unique identity in the map,
			 * this is because we know, that each observer is a singleton in the VM
			 * **/
			if(!observers.containsKey(observer.getClass().getName())){
				observers.put(observer.getClass().getName(), observer);
				logger.warn("added observer :"+ observer.getClass().getName() + " to "+ getClass().getName());
			}
		}
	}
	
	/**
	 * Write Data Source Initialization here
	 */
	public abstract void init();
}
