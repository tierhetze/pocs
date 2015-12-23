package com.sample.kafka.producer.sender;

import java.util.Random;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import com.sample.kafka.message.SampleKey;
import com.sample.kafka.message.SampleMessage;
import com.sample.kafka.producer.Producer;

@PropertySource("classpath:/producer.sender.properties")
@Configuration
public class ExampleMessageSender implements InitializingBean{
	@Autowired
	private Producer producer;
	
	@Autowired
    private Environment env;

	public void afterPropertiesSet() throws Exception {
		
		String topicName = env.getProperty("topic.name");
		
		
		//sample data, that becomes part of the partitioner Key
		String []  regions  = new String[]{"LN", "NY", "TY"};
		Character [] sides  = new Character[]{'A', 'B'};
		int []  securities  = new int[]{1,2,3,4,5,6,7,8,9,10};
		String [] producers = new String[]{"P1", "P2", "P3"};
		
		
		Random r = null;
		
		
		while(true){
			r = new Random(System.currentTimeMillis());
			int index = r.nextInt(regions.length);
			String region = regions[index];
			index = r.nextInt(sides.length);
			Character side = sides[index];
			index = r.nextInt(securities.length);
			int security = securities[index];
			index = r.nextInt(producers.length);
			String prodinstance = producers[index];
					
			SampleKey key = new SampleKey(region, side , security, prodinstance);
			
			
			SampleMessage message = new SampleMessage();
			message.setProp1(1);
			message.setProp2("value");
			ProducerRecord<SampleKey, SampleMessage> record = new ProducerRecord<SampleKey, SampleMessage>(topicName, key, message);
			producer.getKafkaEndpoint().send(record);
			
		}
    }
	
}
