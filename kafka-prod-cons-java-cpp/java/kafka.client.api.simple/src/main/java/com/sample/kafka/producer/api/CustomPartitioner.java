package com.sample.kafka.producer.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.PartitionInfo;

/**
 * 
 * Basic partitioner
 *
 */
public class CustomPartitioner {
	
	private static Map<String, Integer> keyDistributionTable = new HashMap<String, Integer>();
	private static AtomicInteger sequence = new AtomicInteger();
	private ReentrantLock lock = new ReentrantLock();
	
	public int partition(ProducerRecord<String, Object> record, Cluster cluster) {
		
		String key = record.key();
		int seq = figureSeq(key);
		
		List<PartitionInfo> availablePartitions = cluster.availablePartitionsForTopic(record.topic());
		
		if (availablePartitions.size() > 0) {
            int part = seq % availablePartitions.size();
            return availablePartitions.get(part).partition();
        } else {
        	List<PartitionInfo> partitions = cluster.partitionsForTopic(record.topic());
            int numPartitions = partitions.size();
            // no partitions are available, give a non-available partition
            return seq % numPartitions;
        }
    }


	private int figureSeq(String key) {
		int sequentualNumber = 0;
		if(keyDistributionTable.containsKey(key)){
			sequentualNumber = keyDistributionTable.get(key);
		}else{//synchronized region
			//used only for new Keys, so high waiting time for monitor expected only on start
			lock.lock();
			try{
				if(keyDistributionTable.containsKey(key)){
					sequentualNumber =  keyDistributionTable.get(key);
				}else{
					int seq = sequence.incrementAndGet();
					keyDistributionTable.put(key, seq);
					sequentualNumber =  seq;
				}
			}finally{
				lock.unlock();
			}
		}
		return sequentualNumber;
	}
}
