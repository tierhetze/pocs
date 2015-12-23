package com.sample.kafka.consumer.subscriber;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.message.MessageAndMetadata;
import kafka.serializer.DefaultDecoder;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import com.sample.kafka.consumer.Consumer1;
import com.sample.kafka.producer.api.CustomKeyDeserializer;

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
        
        executor = Executors.newFixedThreadPool(partitionsNumber);
        DefaultDecoder defaultDecoder = new DefaultDecoder(null);
        Map<String, List<KafkaStream<String, byte[]>>>  streams = consumer.getZkConnector().createMessageStreams(topicsCountMap, keyDeserialzer, defaultDecoder);
        
        int streamCounter = 0;//need it to debug only
        
        for(String topic: streams.keySet()){
        	
        	List<KafkaStream<String, byte[]>> streamsOfTopic = streams.get(topic);
        	
        	for(KafkaStream<String, byte[]> partStream:streamsOfTopic){
        		
        		 final ConsumerIterator<String, byte[]> it = partStream.iterator();
        		 
        		 final int count = streamCounter++;
        		 
        		 Runnable r = new Runnable(){
        			public void run() {
        				while(it.hasNext()){
        					try{
	        	    			MessageAndMetadata<String, byte[]> data =  it.next();
	        	    			byte[] message = data.message();
	        	    			String key = data.key();
	        	    			int partition = data.partition();
	        	    			System.out.println("stream=" + count + ",key=" + key +  ",partition=" + partition + ",message=" + message);
        					}catch(Exception e){e.printStackTrace();}
        	    		}
        			} 
        	     };
        	     executor.execute(r);
        	     
        	}
        }
    }
}
