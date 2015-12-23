package com.sample.kafka.consumer.subscriber;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.message.MessageAndMetadata;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import com.sample.kafka.consumer.Consumer1;
import com.sample.kafka.message.SampleKey;
import com.sample.kafka.message.SampleMessage;
import com.sample.kafka.producer.api.CustomKey;
import com.sample.kafka.producer.api.CustomKeyDeserializer;
import com.sample.kafka.producer.api.CustomValue;
import com.sample.kafka.producer.api.CustomValueDeserializer;

/**
 * 
 * Example high level consumer subscriber
 *
 */
@PropertySource("classpath:/consumer1.subscriber.properties")
@Configuration
public class Consumer1Subscriber implements InitializingBean {
	
	@Autowired
	private Environment env;
	
	@Autowired
	private Consumer1 consumer;
	
	private ExecutorService executor;
	
	public void afterPropertiesSet() throws Exception {
		
		String topicName = env.getProperty("topic.name");
		//note, that number better be greater than available topic partitions
		//because, if producer decide create more partitions, you will be ready to that
		int partitionsNumber = Integer.valueOf(env.getProperty("patitions.number"));
		
        Map<String, Integer> topicsCountMap = new HashMap<String, Integer>();
        topicsCountMap.put(topicName, partitionsNumber);
		
        CustomKeyDeserializer keyDeserialzer = new CustomKeyDeserializer();
        CustomValueDeserializer messageDeserializer = new CustomValueDeserializer();
        
        executor = Executors.newFixedThreadPool(partitionsNumber);
        Map<String, List<KafkaStream<CustomKey, CustomValue>>>  streams = consumer.getZkConnector().createMessageStreams(topicsCountMap, keyDeserialzer, messageDeserializer);
        
        int streamCounter = 0;//need it to debug only
        
        for(String topic: streams.keySet()){
        	
        	List<KafkaStream<CustomKey, CustomValue>> streamsOfTopic = streams.get(topic);
        	
        	for(KafkaStream<CustomKey, CustomValue> partStream:streamsOfTopic){
        		
        		 final ConsumerIterator<CustomKey, CustomValue> it = partStream.iterator();
        		 final int count = streamCounter++; 
        		 Runnable r = new Runnable(){
        			public void run() {
        				while(it.hasNext()){
        	    			MessageAndMetadata<CustomKey, CustomValue> data =  it.next();
        	    			SampleMessage message = (SampleMessage)data.message();
        	    			SampleKey key = (SampleKey)data.key();
        	    			int partition = data.partition();
        	    			System.out.println("stream=" + count + ",key=" + key +  ",partition=" + partition + ",message=" + message);
        	    		}
        			} 
        	     };
        	     executor.execute(r);
        	     
        	}
        }
    }
}
