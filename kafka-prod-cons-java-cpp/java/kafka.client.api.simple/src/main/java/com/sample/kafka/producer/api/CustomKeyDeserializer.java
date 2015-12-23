package com.sample.kafka.producer.api;

import kafka.serializer.Decoder;

/**
 * 
 * Basic key deserializer
 *
 */
public class CustomKeyDeserializer implements Decoder<String>{

	public String fromBytes(byte[] data) {
		return new String(data);
	}

	

}
