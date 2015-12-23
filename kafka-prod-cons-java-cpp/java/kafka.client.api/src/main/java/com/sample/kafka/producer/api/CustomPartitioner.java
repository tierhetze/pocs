package com.sample.kafka.producer.api;

import java.util.List;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.PartitionInfo;

/**
 * 
 * Basic partitioner
 *
 */
public class CustomPartitioner {
	
	public int partition(ProducerRecord<CustomKey, Object> record, Cluster cluster) {
		
		int seq = record.key().getSequenceNumber();
		
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
}
