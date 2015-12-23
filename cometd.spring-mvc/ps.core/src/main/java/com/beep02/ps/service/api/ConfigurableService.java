package com.beep02.ps.service.api;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.cometd.bayeux.server.ServerSession.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;

import com.beep02.ps.service.user.api.UserService;


/**
Basic push service
Serves to configure the service channels
Direct subclasses are {@link UserService} and {@ SystemService}
@author Vladimir Nabokov
@since 11/06/2013
**/

public abstract class ConfigurableService implements ApplicationContextAware {
	
	public static String SYSTEM_CHANNEL_PREFIX = "/system/";
	public static String USER_CHANNEL_PREFIX   = "/user/";
	public static final Logger logger = LoggerFactory.getLogger(ConfigurableService.class);
	
	public static final String CHANNEL_RESOLVER_TYPE = "channel.resolver.type";
	
	private ApplicationContext context;
	
    @Override
	public void setApplicationContext(ApplicationContext context)	throws BeansException {
		this.context = context;
	}

    
    
    //load extension definition and configure it
    protected Extension loadExtension (final Class<? extends Extension> ex) {
    	String channelResolverType =  System.getProperty(CHANNEL_RESOLVER_TYPE);
    	
    	org.cometd.annotation.Service serviceAnnotation =  getClass().getAnnotation(org.cometd.annotation.Service.class);
    	String serviceName = serviceAnnotation.value();
		   
		if(channelResolverType==null || channelResolverType.equals(ChannelResolvers.STATIC.name())){
			//assume configuration is static
			return loadExtFromFileSystem(ex, serviceName);
		}else if(channelResolverType.equals(ChannelResolvers.DB.name())){
			//assume configuration is Db based
			return loadExtFromDataStorage(ex, serviceName);
		}else{
			logger.error("unknown configuration source");
			return null;
		}
	}



	private Extension loadExtFromDataStorage(Class<? extends Extension> ex, String serviceName) {
		// TODO Auto-generated method stub
		return null;
	}



	private Extension loadExtFromFileSystem(Class<? extends Extension> ex, String serviceName) {
		   try {
			   return ex.newInstance();
		   }catch (InstantiationException e) {
			   logger.error("Failed load extension", e);
		   }catch (IllegalAccessException e) {
			   logger.error("Failed load extension", e);
		   }
		   return null;
	}



	/**
	 * On base of the service name,
	 * find a channels types list to load by this service
	 * The list is configurable in /WEB-INF/config/<servicename>.xml, 
	 * where <servicename> is from the annotation {@link org.cometd.annotation.Service}
	 * @return
	 */
	@SuppressWarnings("all")
	protected List<String> getServiceChannelTypes(){
		//Analyzing the org.cometd.annotation.Service annotation to find a corresponding channels configuration file
        org.cometd.annotation.Service serviceAnnotation =  getClass().getAnnotation(org.cometd.annotation.Service.class);
		if (serviceAnnotation != null){
		    
			String serviceName = serviceAnnotation.value();
		   
		    String channelResolverType =  System.getProperty(CHANNEL_RESOLVER_TYPE);
		   
			if(channelResolverType==null || channelResolverType.equals(ChannelResolvers.STATIC.name())){
				//assume configuration is static
				return extractFromFileSystem(serviceName);
			}else if(channelResolverType.equals(ChannelResolvers.DB.name())){
				//assume configuration is Db based
				return extractFromDatastorage(serviceName);
			}else{
				logger.error("unknown configuration source");
				return null;
			}
		}
		return null;
	}




    /**TODO: Implement DS configuration extraction**/
	private List<String> extractFromDatastorage(String serviceName) {
		// TODO Auto-generated method stub
		return null;
	}




    @SuppressWarnings("all")
	private List<String> extractFromFileSystem(String serviceName) {
		   String fileName = serviceName + ".xml";
		  
		   
		   XMLConfiguration config = getServiceConfiguration(fileName);
		   
		   List<String> channelsList = (List<String>)(List<?>)config.getList("channels");
		   return channelsList;
	}



	private XMLConfiguration getServiceConfiguration(String fileName) {
		Resource[] xmlResources = null;
		   try {
			    xmlResources = context.getResources("classpath:/"+fileName);
		   } catch (IOException e2) {
			   logger.error("failed configure service", e2);
			   return null;
		   }
		
		   if(xmlResources==null || xmlResources.length != 1){
			   logger.error("failed configure service "+fileName);
			   return null;
		   }
		  
		   File configurationFile = null;
		  
		   try {
				configurationFile =  xmlResources[0].getFile();
		   } catch (IOException e1) {
			    logger.error("failed configure service "+fileName, e1);
				return null;
		   }
		   
		   
		   
		   //read the configuration and return available user channels
		   XMLConfiguration.setDefaultListDelimiter('|');
		   XMLConfiguration config = null;
		   try {
				config = new XMLConfiguration(configurationFile);
		   } catch (ConfigurationException e) {
			    logger.error("failed configure by XML", e);
				return null;
		   }
		return config;
	}
}
