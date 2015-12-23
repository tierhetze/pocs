package com.sample.kafka.producer.api;

import java.util.Map;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.kafka.common.serialization.Serializer;



public class CustomValueSerializer implements Serializer<CustomValue>{
    public void close() {
		// nothing to do
	}
    public void configure(Map<String, ?> arg0, boolean arg1) {
		// nothing to do
	}
    public byte[] serialize(String topic, CustomValue data) {
		if(data==null){
			return null;
		}
		return SerializationUtils.serialize(data);
	}
}
