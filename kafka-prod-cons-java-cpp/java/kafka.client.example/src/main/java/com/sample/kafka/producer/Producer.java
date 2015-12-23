package com.sample.kafka.producer;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import com.sample.kafka.message.SampleKey;
import com.sample.kafka.message.SampleMessage;


@PropertySource("classpath:/producer.properties")
@Configuration
public class Producer implements InitializingBean{
	
	@Autowired
    private Environment env;


	private KafkaProducer<SampleKey, SampleMessage> kafkaEndpoint;
	
	
	
    public void afterPropertiesSet() throws Exception {
		Properties props = new Properties();
        props.put("bootstrap.servers", env.getProperty("bootstrap.servers"));
        props.put("value.serializer", env.getProperty("value.serializer"));
        props.put("partitioner.class", env.getProperty("partitioner.class"));
        props.put("key.serializer", env.getProperty("key.serializer"));
        
        kafkaEndpoint = new KafkaProducer<SampleKey, SampleMessage>(props);
    }



	public KafkaProducer<SampleKey, SampleMessage> getKafkaEndpoint() {
		return kafkaEndpoint;
	}
    
}
