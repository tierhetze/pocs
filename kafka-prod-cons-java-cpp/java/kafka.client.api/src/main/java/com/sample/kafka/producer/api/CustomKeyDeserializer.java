package com.sample.kafka.producer.api;

import kafka.serializer.Decoder;

import org.apache.commons.lang3.SerializationUtils;

/**
 * 
 * Basic key deserializer
 *
 */
public class CustomKeyDeserializer implements Decoder<CustomKey>{

	public CustomKey fromBytes(byte[] data) {
		return SerializationUtils.deserialize(data);
	}

	

}
