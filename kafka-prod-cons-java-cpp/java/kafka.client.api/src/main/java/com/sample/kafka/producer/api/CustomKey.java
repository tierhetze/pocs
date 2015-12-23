package com.sample.kafka.producer.api;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 
 * Basic partition key (must be extended by the real key)
 *
 */
public abstract class CustomKey implements Serializable{
	
	private static final long serialVersionUID = 2958117124260058576L;
	private static Map<Integer, Integer> keyDistributionTable = new HashMap<Integer, Integer>();
	private static AtomicInteger sequence = new AtomicInteger();
	private ReentrantLock lock = new ReentrantLock();
	
	protected final int getSequenceNumber(){
		
		int sequentualNumber = 0;
		int uniqueId = getUniqueTopicSegmentId();
		
		if(keyDistributionTable.containsKey(uniqueId)){
			sequentualNumber = keyDistributionTable.get(uniqueId);
		}else{//synchronized region
			//used only for new Keys, so high waiting time for monitor expected only on start
			lock.lock();
			try{
				if(keyDistributionTable.containsKey(uniqueId)){
					sequentualNumber =  keyDistributionTable.get(uniqueId);
				}else{
					int seq = sequence.incrementAndGet();
					keyDistributionTable.put(uniqueId, seq);
					sequentualNumber =  seq;
				}
			}finally{
				lock.unlock();
			}
		}
		return sequentualNumber;
		
	}
	
	
	protected abstract int getUniqueTopicSegmentId();
	
}
