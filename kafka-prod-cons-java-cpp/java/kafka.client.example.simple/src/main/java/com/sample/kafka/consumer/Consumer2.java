package com.sample.kafka.consumer;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import kafka.api.FetchRequest;
import kafka.api.FetchRequestBuilder;
import kafka.api.PartitionOffsetRequestInfo;
import kafka.common.ErrorMapping;
import kafka.common.TopicAndPartition;
import kafka.javaapi.FetchResponse;
import kafka.javaapi.OffsetResponse;
import kafka.javaapi.PartitionMetadata;
import kafka.javaapi.TopicMetadata;
import kafka.javaapi.TopicMetadataRequest;
import kafka.javaapi.consumer.SimpleConsumer;
import kafka.message.MessageAndOffset;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

/**
 * 
 * Example low level consumer
 *
 */
@PropertySource("classpath:/consumer2.properties")
@Configuration
public class Consumer2 implements InitializingBean{
	@Autowired
	private Environment env;
	private Map<String, Integer> m_replicaBrokers = new HashMap<String, Integer>();
	private ExecutorService service = Executors.newSingleThreadExecutor();
	
	public void afterPropertiesSet() throws Exception {
		Runnable r = new Runnable(){
			public void run() {
				try{
				    runSimpleConsumer();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		};
		service.execute(r);
	}

	private void runSimpleConsumer() throws Exception {
		String brokers = env.getProperty("seed.brokers");
		String topic = env.getProperty("topic.name");
		int partition = Integer.valueOf(env.getProperty("partition"));
		
		String[] seedbrokers = brokers.split(",");
		Map<String,Integer> seedBrokersMap = new HashMap<String, Integer>();
		for(String broker: seedbrokers){
			String[] hostPort = broker.split(":");
			String host = hostPort[0];
			int port = Integer.valueOf(hostPort[1]);
			seedBrokersMap.put(host,port);
		}
		
	    PartitionMetadata meta = findLeader(seedBrokersMap, topic, partition);
	    if (meta == null) {
            System.out.println("Can't find metadata for Topic and Partition. Exiting");
            return;
        }
	    
	    if (meta.leader() == null) {
            System.out.println("Can't find Leader for Topic and Partition. Exiting");
            return;
        }
	    
	    String leadBroker = meta.leader().host();
	    int leadPort  = meta.leader().port();
        String clientName = "Client_" + topic + "_" + partition;
        
        
        SimpleConsumer consumer = new SimpleConsumer(leadBroker, leadPort, 100000, 64 * 1024, clientName);
        long readOffset = getLastOffset(consumer, topic, partition, kafka.api.OffsetRequest.EarliestTime(), clientName);
	    int numErrors = 0;
        while (true) {
            if (consumer == null) {
                consumer = new SimpleConsumer(leadBroker, leadPort, 100000, 64 * 1024, clientName);
            }
            FetchRequest req = new FetchRequestBuilder().clientId(clientName)
                    .addFetch(topic, partition, readOffset, 100000) // Note: this fetchSize of 100000 might need to be increased if large batches are written to Kafka
                    .build();
            FetchResponse fetchResponse = consumer.fetch(req);
 
            if (fetchResponse.hasError()) {
                numErrors++;
                // Something went wrong!
                short code = fetchResponse.errorCode(topic, partition);
                System.out.println("Error fetching data from the Broker:" + leadBroker + " Reason: " + code);
                if (numErrors > 5) break;
                if (code == ErrorMapping.OffsetOutOfRangeCode())  {
                    // We asked for an invalid offset. For simple case ask for the last element to reset
                    readOffset = getLastOffset(consumer,topic, partition, kafka.api.OffsetRequest.LatestTime(), clientName);
                    continue;
                }
                consumer.close();
                consumer = null;
                leadBroker = findNewLeader(leadBroker, topic, partition, leadPort);
                continue;
            }
            numErrors = 0;
            
 
            long numRead = 0;
            for (MessageAndOffset messageAndOffset : fetchResponse.messageSet(topic, partition)) {
                long currentOffset = messageAndOffset.offset();
                if (currentOffset < readOffset) {
                    System.out.println("Found an old offset: " + currentOffset + " Expecting: " + readOffset);
                    continue;
                }
                readOffset = messageAndOffset.nextOffset();
                ByteBuffer payload = messageAndOffset.message().payload();
 
                byte[] bytes = new byte[payload.limit()];
                payload.get(bytes);
                
                System.out.println("SimpleConsumer message: offest: " + String.valueOf(messageAndOffset.offset()) + " and message:  " + bytes);
                
                
                
                numRead++;
            }
 
            if (numRead == 0) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ie) {
                }
            }
        }
        if (consumer != null) consumer.close();
	}

	public static long getLastOffset(SimpleConsumer consumer, String topic,	int partition, long whichTime, String clientName) {
		TopicAndPartition topicAndPartition = new TopicAndPartition(topic, partition);
	    Map<TopicAndPartition, PartitionOffsetRequestInfo> requestInfo = new HashMap<TopicAndPartition, PartitionOffsetRequestInfo>();
	    requestInfo.put(topicAndPartition, new PartitionOffsetRequestInfo(whichTime, 1));
	    kafka.javaapi.OffsetRequest request = new kafka.javaapi.OffsetRequest(requestInfo, kafka.api.OffsetRequest.CurrentVersion(),clientName);
	    OffsetResponse response = consumer.getOffsetsBefore(request);
        if (response.hasError()) {
			System.out.println("Error fetching data Offset Data the Broker. Reason: " + response.errorCode(topic, partition));
			return 0;
		}
		long[] offsets = response.offsets(topic, partition);
		return offsets[0];
	}
	
	
	private PartitionMetadata findLeader(Map<String,Integer> seedBrokersMap, String a_topic, int a_partition) {
        PartitionMetadata returnMetaData = null;
        loop:
        for (String seed : seedBrokersMap.keySet()) {
        	
            SimpleConsumer consumer = null;
            try {
                
            	consumer = new SimpleConsumer(seed, seedBrokersMap.get(seed), 100000, 64 * 1024, "leaderLookup");
            	
                List<String> topics = Collections.singletonList(a_topic);
                
                TopicMetadataRequest req = new TopicMetadataRequest(topics);
                
                kafka.javaapi.TopicMetadataResponse resp = consumer.send(req);
 
                List<TopicMetadata> metaData = resp.topicsMetadata();
                for (TopicMetadata item : metaData) {
                    for (PartitionMetadata part : item.partitionsMetadata()) {
                        if (part.partitionId() == a_partition) {
                            returnMetaData = part;
                            break loop;
                        }
                    }
                }
                
            } catch (Exception e) {
                System.out.println("Error communicating with Broker [" + seed + "] to find Leader for [" + a_topic
                        + ", " + a_partition + "] Reason: " + e);
            } finally {
                if (consumer != null) consumer.close();
            }
        }
		
        if (returnMetaData != null) {
            m_replicaBrokers.clear();
            for (kafka.cluster.Broker replica : returnMetaData.replicas()) {
                m_replicaBrokers.put(replica.host(), replica.port());
            }
        }
        
        return returnMetaData;
    }
	
	private String findNewLeader(String a_oldLeader, String a_topic, int a_partition, int a_port) throws Exception {
        for (int i = 0; i < 3; i++) {
            boolean goToSleep = false;
            PartitionMetadata metadata = findLeader(m_replicaBrokers,  a_topic, a_partition);
            if (metadata == null) {
                goToSleep = true;
            } else if (metadata.leader() == null) {
                goToSleep = true;
            } else if (a_oldLeader.equalsIgnoreCase(metadata.leader().host()) && i == 0) {
                // first time through if the leader hasn't changed give ZooKeeper a second to recover
                // second time, assume the broker did recover before failover, or it was a non-Broker issue
                //
                goToSleep = true;
            } else {
                return metadata.leader().host();
            }
            if (goToSleep) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ie) {
                }
            }
        }
        System.out.println("Unable to find new leader after Broker failure. Exiting");
        throw new Exception("Unable to find new leader after Broker failure. Exiting");
    }

}
