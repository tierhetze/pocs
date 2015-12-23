package com.sample.kafka.producer.api;

import java.util.Map;

import org.apache.kafka.common.serialization.Serializer;


/**
 * 
 * Basic key serializer
 *
 */
public class CustomKeySerializer implements Serializer<String>{
    public void close() {
		// nothing to do
	}
    public void configure(Map<String, ?> arg0, boolean arg1) {
		// nothing to do
	}
    public byte[] serialize(String topic, String data) {
		if(data==null){
			return null;
		}
		return data.getBytes();
	}
}
