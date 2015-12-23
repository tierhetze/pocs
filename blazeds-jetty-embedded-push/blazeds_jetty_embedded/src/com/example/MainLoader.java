package com.example;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;

public class MainLoader {
	
	
	
	public static void main(String[] args) throws Exception{
		
		
		
		//loads jetty
		
		Server server = new Server(8080);

        HandlerCollection contexts = new HandlerCollection();
        
        ServletContextHandler messageBroker = new ServletContextHandler(contexts, "/app", WebAppContext.SESSIONS );
        
        
        ServletHolder holder = messageBroker.addServlet("flex.messaging.MessageBrokerServlet", "/messagebroker/*");
        
        String path = System.getProperty("user.dir") + "/src/resources/WEB-INF/flex/services-config.xml";
        
        holder.setInitParameter("services.configuration.file", path);
        
        ContextHandler async=new ContextHandler("/flexdata");
        
        ResourceHandler asyncClient = new ResourceHandler();

        asyncClient.setWelcomeFiles(new String[]{"index.html"});

        asyncClient.setResourceBase("src/resources/flexdata");

        async.setHandler(asyncClient);

        contexts.setHandlers(new Handler[]{messageBroker,async});
        
        HandlerCollection handlers = new HandlerCollection();

        handlers.setHandlers(new Handler[]{contexts,new DefaultHandler()});

        server.setHandler(handlers);
        
        server.start();
        
        //init spring classes
        SpringServicesFactory.init();

        server.join();
        
        
        
    }

	

}
