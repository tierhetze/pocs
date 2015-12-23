package com.beep02.ps.service.system.api;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.cometd.annotation.Session;
import org.cometd.bayeux.server.BayeuxServer;
import org.cometd.bayeux.server.ConfigurableServerChannel;
import org.cometd.bayeux.server.ServerChannel;
import org.cometd.bayeux.server.ServerSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beep02.ps.datastream.api.ObservableStreamDataSource;
import com.beep02.ps.datastream.api.PushData;
import com.beep02.ps.datastream.api.StreamDataSource;
import com.beep02.ps.service.api.ConfigurableService;


/**
Base class for system services
System services responsible to get data from {@link StreamDataSource} implementations and pass the data
to the system channels.
Here also system channels created

@author Vladimir Nabokov
@since 11/06/2013
**/

public abstract class SystemService extends ConfigurableService implements StreamDataSourceObserver {
    
	private final static Logger logger = LoggerFactory.getLogger(SystemService.class);
	
	@Session
    protected ServerSession serverSession;
	
	
	@Inject
	protected BayeuxServer bayeux;
	
	
	protected ObservableStreamDataSource dataSource;
	
	
    

	@PostConstruct
    protected void init(){
		//creates multi-user(system)channels
		//takes the channels data from the configuration
		List<String> systemChannelsTypes = getServiceChannelTypes();
		if(systemChannelsTypes==null){
			String message = "Service "+ getClass().getName() + " is not configured properly";
			logger.error(message);
			throw new RuntimeException(message);
		}
		for(String channelType: systemChannelsTypes){
			bayeux.createIfAbsent(SYSTEM_CHANNEL_PREFIX+channelType, new DistributionServiceChannelInitializer());
		}
		//to listen to the stream data source events(output)
		registerAsObserver();
		
		logger.warn("initialized "+ getClass().getName());
	}
	
	
	protected void registerAsObserver(){
		org.cometd.annotation.Service serviceAnnotation =  getClass().getAnnotation(org.cometd.annotation.Service.class);
		
		if (serviceAnnotation != null){
		    String serviceName = serviceAnnotation.value();
		    if(serviceName == null){
		    	throw new RuntimeException("service name is not configured properly");
		    }
		}else{
			throw new RuntimeException("service name is not configured properly");
		}
		
		dataSource = locateDataSource();
		((StreamDataSource)dataSource).init();
		dataSource.addObserver(this);
	}

	/**
	 * By default takes the first (and single) DataStream specified in the Service annotation
	 * You can rewrite that.
	 * @return
	 */
    protected ObservableStreamDataSource locateDataSource() {
    	StreamDataSources serviceAnnotation =  getClass().getAnnotation(StreamDataSources.class);
    	if(serviceAnnotation==null){
    		String message = "failed locate stream data source";
    		logger.error(message);
    		throw new RuntimeException(message);
    	}
    	Class<? extends ObservableStreamDataSource> [] clazzes = serviceAnnotation.value();
    	if(clazzes==null || clazzes.length <1){
    		String message = "failed locate stream data source(2)";
    		logger.error(message);
    		throw new RuntimeException(message);
    	}
    	Class<? extends ObservableStreamDataSource> myDataSource = clazzes[0];
    	
    	try {
			return myDataSource.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			String message = "failed locate stream data source(3)";
			logger.error(message, e);
    		throw new RuntimeException(message, e);
    	}
    }

	

	/**
	 * @see {@link StreamDataSourceObserver#notify(PushData)}
	 */
    @Override
    public void notify(final PushData data) {
		String type = data.getType();
		//TODO: check is the channel is present in a registry 
		if(true){
			ServerChannel schannel = bayeux.getChannel(SYSTEM_CHANNEL_PREFIX+type);
			schannel.publish(serverSession, data, null/*TODO: check if ID required here*/);
		}
		
	}
	
	
	class DistributionServiceChannelInitializer implements ConfigurableServerChannel.Initializer{
        @Override
		public void configureChannel(ConfigurableServerChannel channel) {
			channel.setPersistent(true);
		}
		
	}
	
	
	
}
