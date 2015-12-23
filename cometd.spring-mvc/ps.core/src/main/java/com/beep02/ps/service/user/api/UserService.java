package com.beep02.ps.service.user.api;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.cometd.annotation.Session;
import org.cometd.bayeux.Message;
import org.cometd.bayeux.client.ClientSessionChannel;
import org.cometd.bayeux.server.BayeuxServer;
import org.cometd.bayeux.server.ConfigurableServerChannel;
import org.cometd.bayeux.server.ServerChannel;
import org.cometd.bayeux.server.ServerSession;
import org.cometd.bayeux.server.ServerSession.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beep02.ps.datastream.api.PushData;
import com.beep02.ps.service.api.ConfigurableService;
import com.beep02.ps.service.system.api.SystemService;


/**
 * This is a base for services of the user layer (user services)
 * such services publish to private user channels
 * Such service able to modify the data before publishing
 * (TODO: review customization options to expose to developer)
 * Such services get data from the system channels, to which they are registered as subscribers 
 * 
 * See server side configuration doc here:
 * http://cometd.org/documentation/2.x/cometd-java/server/configuration
 * http://cometd.org/documentation/2.x/cometd-java/server/services/integration-spring
 * http://docs.cometd.org/reference/#java_server_services
 * 
 * @author Vladimir Nabokov
 * @since 11/06/2013
 * 
**/

public abstract class UserService extends ConfigurableService {
	
	private final static Logger logger = LoggerFactory.getLogger(UserService.class);
	/**
	 * When this system property set, we may have performance gain,
	 * but also some risk of queue overloading
	 * the feature is experimental, need to be tested with high number of users to comparison 
	 * */
	private static final String USER_THREAD_POOL_ENABLED = "user.thread.pool.enabled";
	
	private static final String USER_CHANNEL_REGEX_PATTERN = "^"+USER_CHANNEL_PREFIX+"\\w+/.+";
	private static final String USER_CHANNEL_REGEX_PATTERN_WITHOUT_CHANNEL_TYPE = "^"+USER_CHANNEL_PREFIX+"\\w+/";
	
	@Session
    protected ServerSession serverSession;
	
	@Inject
	protected BayeuxServer bayeux;
	
	
    //key - channel type
	//value - set of channels of that type (depend on set of connected users)
	private Map<String, Set<String>> userChannelsByType = new ConcurrentHashMap<String, Set<String>>();
	
	private final int DEFAULT_CORE_POOL_SIZE = 5;
	
	private boolean isThreadPoolEnabled = false;
	/**
	 * 
	 * externalize configuration
	 * 
	 */
	private ExecutorService servicePerDataTypeDistributor;
	
	/**
	 * 
	 */
	private Map<String, ConcurrentLinkedQueue<PushData>> queuePerType = new HashMap<String, ConcurrentLinkedQueue<PushData>>();
	
	
	class ChannelsFeeder implements Runnable{

		final String channleType;
		final ConcurrentLinkedQueue<PushData> pendingTypeData;
		long lastIterationTime;
		
		public ChannelsFeeder(final String channleType, final ConcurrentLinkedQueue<PushData> pendingTypeData){
			this.channleType     = channleType;
			this.pendingTypeData = pendingTypeData;
			lastIterationTime = System.currentTimeMillis();
		}
		
		@Override
		public void run() {
			
			Set<String> channels = userChannelsByType.get(channleType);
			/**
			 *  Distribute the message between user channels 
			 *  (between users, who desire factually receive data from the system channel of that particular type)
			 * */
			synchronized (pendingTypeData) {
				if(channels!=null && channels.size()>0){
					
					
				    PushData pdata = pendingTypeData.poll();
					if(pdata != null){
						for(String userChannel: channels){
							ClientSessionChannel uchannel = serverSession.getLocalSession().getChannel(userChannel);
							uchannel.publish(pdata);
						}
						
						if(System.currentTimeMillis() - lastIterationTime >20000){
							lastIterationTime = System.currentTimeMillis();
							logger.warn("distribution queue size ("+channleType+") = "+ pendingTypeData.size());
							//System.out.println("distribution queue size ("+channleType+") = "+ pendingTypeData.size());
						}
						
					}else{
						try {
							pendingTypeData.wait();
						} catch (InterruptedException e) {
							logger.warn("ChannelsFeeder interrupted , while waits for work", e);
						}
					}
					
				}else{
					try {
						pendingTypeData.wait();
					} catch (InterruptedException e) {
						logger.warn("ChannelsFeeder interrupted , while waits for work", e);
					}
				}
			}
			servicePerDataTypeDistributor.submit(this);
		}
		
	}
	
    @PostConstruct
    protected void init(){
		/**
    	 * This listener notices that channel added or removed, upon that the service can cleanup the data and release resources
    	**/
    	bayeux.addListener(new ClientChannelListener());
    	
        List<String> serviceUserChannelTypes = getServiceChannelTypes();
        
        if( serviceUserChannelTypes == null ){
        	String message = "the service "+ getClass().getName()+ ", is not configured properly ";
        	logger.error(message);
        	throw new RuntimeException(message);
        }
        
        
        isThreadPoolEnabled = getThreadPoolStatus();
        
        if(isThreadPoolEnabled){
        	int coreThreadPoolSize = getCorePoolSize();
            servicePerDataTypeDistributor = Executors.newScheduledThreadPool(coreThreadPoolSize);
	        //create and start single threaded executers per channel type 
	        for(String channelType: serviceUserChannelTypes){
	        	ConcurrentLinkedQueue<PushData> pendingTypeData = new ConcurrentLinkedQueue<PushData>();
	        	queuePerType.put(channelType, pendingTypeData);
	        	registerMultiUserChannelListener(channelType);
			    ChannelsFeeder feeder = new ChannelsFeeder(channelType, pendingTypeData); 
			    servicePerDataTypeDistributor.submit(feeder);
			}
	    }else{
	    	for(String channelType: serviceUserChannelTypes){
	    		registerMultiUserChannelListener(channelType);
	    	}
	    }
        
        
        
        addExtensions();
        
        logger.warn("initialized "+ getClass().getName());
        
        //logger for testings
        //TODO hide
        Thread t = new Thread(new LoggerThread());
        t.start();
    }
    
    
    class LoggerThread implements Runnable{

		@Override
		public void run(){
			
			while(true){
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				for(String type : userChannelsByType.keySet()){
					int size = userChannelsByType.get(type).size();
					logger.warn("type"+type + ", size="+ size);
				}
				
				
			}
			
		}
    	
    }

	private boolean getThreadPoolStatus() {
		if(System.getProperty(USER_THREAD_POOL_ENABLED)!=null)
		   return true;
		else
		   return false;	
	}




	private int getCorePoolSize() {
		UserServiceLevelThreadPool serviceAnnotation =  getClass().getAnnotation(UserServiceLevelThreadPool.class);
		if(serviceAnnotation==null){
		    return DEFAULT_CORE_POOL_SIZE;
		}
		return serviceAnnotation.coreSize()!=0?serviceAnnotation.coreSize():DEFAULT_CORE_POOL_SIZE;
	}




	private void addExtensions() {
		UserServiceLevelExtensions ext = getClass().getAnnotation(UserServiceLevelExtensions.class);
        if(ext!=null){
        	
        	for(Class<? extends Extension> ex : ext.value()){
        		
        		Extension e = null;
        		//loads extension from configuration
        		e = loadExtension(ex);
				
        		if(e!=null){
        			serverSession.addExtension(e);
        		}
        	}
        }
	}
	
    

	

	/**
     * When user channel created, the listener invoked.
     * channels like:
     * /user/p1/xxx
     * /user/p1/xxx/yyy
     * are all subjects for adding them to the map userChannelsByType
     * The map will have the following structure
     * key(channel type):   value(set of user channel ids, relative to this type):
     * /xxx                 {/user/p1/xxx,     /user/p2/xxx    , /user/p3/xxx}
     * /xxx/yyy             {/user/p1/xxx/yyy, /user/p2/xxx/yyy, /user/p3/xxx/yyy}
     * such a map allows a mapping betweeen channel type and personal user channels, subscribed to this type
     * That allows us:
     * When the message comes from a system channel with type xxx/yyy -> publish this message to the set of user's channels 
     * @param channel
	 **/
    protected void onUserChannelCreation(final ServerChannel channel) {
    	String channelId  = channel.getId();
    	String type = channelId.replaceFirst(USER_CHANNEL_REGEX_PATTERN_WITHOUT_CHANNEL_TYPE, "");
		if(type != null && type.length() > 0){
    		Set<String> channels = userChannelsByType.get(type);
			synchronized (userChannelsByType) {
				if(channels== null){
			        channels = new HashSet<String>();
			        userChannelsByType.put(type, channels);
				}
				channels.add(channel.getId());
			}
		}
    }
	
	
	
	
	/**
	 *  
	 * @param channelId
	 */
	protected void onUserChannelRemoval(final String channelId) {
    	
		String type = channelId.replaceFirst(USER_CHANNEL_REGEX_PATTERN_WITHOUT_CHANNEL_TYPE, "");
		
    	if(type!=null && type.length() > 0){
			
			Set<String> channels = userChannelsByType.get(type);
			
			synchronized (userChannelsByType) {
				if(channels != null){
					channels.remove(channelId);
				}
			}
		}
	}
	
	/**
	 * Implementing service must call that method to be registered to get messages from appropriate channels of the system (from system channels)
	 * @param type - to which system channel to register
	 * 
	*/
    private void registerMultiUserChannelListener(final String type) {
    	final ConcurrentLinkedQueue<PushData> pendingData = queuePerType.get(type);
    	serverSession.getLocalSession().getChannel(SystemService.SYSTEM_CHANNEL_PREFIX + type).subscribe(new ClientSessionChannel.MessageListener() {
			/**
			 * registered on messages from system channel of particular type 
			 */
			@Override
			public void onMessage(ClientSessionChannel channel, Message message) {
				Set<String> channels =  userChannelsByType.get(type);
				if(channels != null && channels.size()>0){
					PushData pdata = (PushData) message.getData();
					if(isThreadPoolEnabled){
						//populate queue
						synchronized (pendingData) {
						    pendingData.add(pdata);
						    pendingData.notify();
						}
					}else{
						//distribute between channels directly
						for(String userChannel: channels){
							ClientSessionChannel uchannel = serverSession.getLocalSession().getChannel(userChannel);
							uchannel.publish(pdata);
						}
					}
				}
			}
		});
	}
    
    
    
    
    class ClientChannelListener implements BayeuxServer.ChannelListener{
		@Override
		public void configureChannel(ConfigurableServerChannel channel) {
			
			
		}
        //TODO ensure concurrency support for the channel adding/removal
		@Override
		public void channelAdded(ServerChannel channel){
			String channelId = channel.getId();
			if(channelId.matches(USER_CHANNEL_REGEX_PATTERN)){
				onUserChannelCreation(channel);
			}
		}

		/**
		 * Thanks to Cometd, it removes all chanels, where client does not respond
		 * So if I attached resource to the client channel, I also may clean it up
		 */
		//TODO ensure concurrency support for the channel adding/removal
		@Override
		public void channelRemoved(String channelId) {
			//channel removed, if it is user channel - cleanup resources for the user
			if(channelId.matches(USER_CHANNEL_REGEX_PATTERN)){
				onUserChannelRemoval(channelId);
			}
		}
	}
	

}
