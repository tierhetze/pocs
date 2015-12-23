package com.sample.kafka.consumer;

import java.util.Properties;

import kafka.consumer.ConsumerConfig;
import kafka.javaapi.consumer.ZookeeperConsumerConnector;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

/**
 * 
 * Example high level consumer
 *
 */
@PropertySource("classpath:/consumer1.properties")
@Configuration
public class Consumer1 implements InitializingBean {

	@Autowired
	private Environment env;

	private ZookeeperConsumerConnector zkConnector; 
	
	public void afterPropertiesSet() throws Exception {
		Properties props = new Properties();
		props.put("group.id", env.getProperty("group.id"));
		props.put("zookeeper.connect", env.getProperty("zookeeper.connect"));
		props.put("session.timeout.ms", env.getProperty("session.timeout.ms"));
		props.put("auto.commit.enable", env.getProperty("auto.commit.enable"));
		props.put("auto.commit.interval.ms", env.getProperty("auto.commit.interval.ms"));
		props.put("partition.assignment.strategy", env.getProperty("partition.assignment.strategy"));
		ConsumerConfig configuration = new ConsumerConfig(props);
		zkConnector = new ZookeeperConsumerConnector(configuration, true);
	}

	public ZookeeperConsumerConnector getZkConnector() {
		return zkConnector;
	}
    
    
    

	

	
	
	
	

}
