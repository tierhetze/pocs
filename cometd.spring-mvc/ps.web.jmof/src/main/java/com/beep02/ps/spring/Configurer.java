package com.beep02.ps.spring;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.servlet.ServletContext;

import org.cometd.annotation.ServerAnnotationProcessor;
import org.cometd.bayeux.server.BayeuxServer;
import org.cometd.server.BayeuxServerImpl;
import org.cometd.websocket.server.WebSocketTransport;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletContextAware;


/**

@author Vladimir Nabokov

The class responsible to wire comet-d objects and the app

This class may also be used to configure comet-d server
for example, I added web-socket transport here
@Component - Indicates that an annotated class is a "component". 
Such classes are considered as candidates for auto-detection when 
using annotation-based configuration and class-path scanning. 
see http://cometd.org/documentation/2.x/cometd-java/server/services/integration-spring
**/
@Component
public class Configurer implements DestructionAwareBeanPostProcessor, ServletContextAware{
	private BayeuxServer bayeuxServer;
    private ServerAnnotationProcessor processor;

    @Inject
    private void setBayeuxServer(BayeuxServer bayeuxServer)
    {
        this.bayeuxServer = bayeuxServer;
    }

    @PostConstruct
    private void init()
    {
        this.processor = new ServerAnnotationProcessor(bayeuxServer);
        
    }

    public Object postProcessBeforeInitialization(Object bean, String name) throws BeansException
    {
        processor.processDependencies(bean);
        processor.processConfigurations(bean);
        processor.processCallbacks(bean);
        return bean;
    }

    public Object postProcessAfterInitialization(Object bean, String name) throws BeansException
    {
        return bean;
    }

    public void postProcessBeforeDestruction(Object bean, String name) throws BeansException
    {
        processor.deprocessCallbacks(bean);
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public BayeuxServer bayeuxServer(){
        BayeuxServerImpl bean = new BayeuxServerImpl();
        bean.setOption(BayeuxServerImpl.LOG_LEVEL, "2");//3 - console log INFO level, 2 is warn, 4 is debug
        bean.addTransport(new WebSocketTransport(bean));//add websocket transport
        return bean;
    }

    public void setServletContext(ServletContext servletContext)
    {
        servletContext.setAttribute(BayeuxServer.ATTRIBUTE, bayeuxServer);
    }
}
